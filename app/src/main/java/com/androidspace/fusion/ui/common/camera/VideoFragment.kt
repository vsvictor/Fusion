package com.androidspace.fusion.ui.common.camera

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.concurrent.futures.await
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.util.Consumer
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.databinding.FragmentVideoBinding
import com.androidspace.fusion.ui.common.camera.data.*
import com.androidspace.fusion.ui.common.camera.extensions.getAspectRatioString
import com.androidspace.fusion.utils.bitmapFromReource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import java.io.File
import java.util.*

@RuntimePermissions
@Layout(R.layout.fragment_video)
class VideoFragment : BaseFragment<FragmentVideoBinding, VideoViewModel>(), OnVideoEvents,
    OnVideoUI {
    private val TAG = VideoFragment::class.java.simpleName
    private var enumerationDeferred: Deferred<Unit>? = null
    private lateinit var videoCapture: VideoCapture<Recorder>
    private var currentRecording: Recording? = null
    private lateinit var recordingState: VideoRecordEvent
    private lateinit var provider: ProcessCameraProvider
    private lateinit var mediaStoreOutput: MediaStoreOutputOptions
    private val mainThreadExecutor by lazy { ContextCompat.getMainExecutor(requireContext()) }
    companion object{
        private var dest: OnVideoDestinatation? = null
        const val NORM_LIGHT = 150
        fun setOnDestinatationListener(listener: OnVideoDestinatation?){
            this.dest = listener
        }
    }
    init {
        enumerationDeferred = lifecycleScope.async {
            whenCreated {
                provider = ProcessCameraProvider.getInstance(requireContext()).await()

                provider.unbindAll()
                for (camSelector in arrayOf(
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    CameraSelector.DEFAULT_FRONT_CAMERA
                )) {
                    try {
                        // just get the camera.cameraInfo to query capabilities
                        // we are not binding anything here.
                        if (provider.hasCamera(camSelector)) {
                            val camera = provider.bindToLifecycle(requireActivity(), camSelector)
                            QualitySelector.getSupportedQualities(camera.cameraInfo).filter { quality ->
                                    listOf(Quality.UHD, Quality.FHD, Quality.HD, Quality.SD).contains(quality)
                                }.also {
                                    viewModel.cameraCapabilities.add(VideoViewModel.CameraCapability(camSelector, it))
                                }
                        }
                    } catch (exc: java.lang.Exception) {
                        Log.e(TAG, "Camera Face $camSelector is not supported")
                    }
                }
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
/*        val param = binding.clPanel.layoutParams as ViewGroup.MarginLayoutParams
        param.bottomMargin = requireContext().navigationBarHeight
        binding.clPanel.layoutParams = param*/
        checkCameraWithPermissionCheck()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.onVideoEvents = this
        viewModel.onVideoUI = this
        viewModel.setOnDestinatationListener(dest)
        binding.model = viewModel
    }
    override fun onDestroyView() {
        super.onDestroyView()
        //onNavigateView?.onBottomBarVisible(false)
        stopCamera()
    }
    private fun stopCamera() {
        provider.unbindAll()
        Log.d(TAG, "Camera stopped")
    }
    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
    private fun initCameraFragment() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (enumerationDeferred != null) {
                enumerationDeferred!!.await()
                enumerationDeferred = null
            }
        }
    }
    override fun onBindCaptureUseCase(cameraIndex: Int, qualityIndex: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            bindCaptureUsecase(cameraIndex, qualityIndex)
        }
    }

    override fun onStartRecord() {
        if (!this@VideoFragment::recordingState.isInitialized || recordingState is VideoRecordEvent.Finalize) {
            startRecording()
        } else {
            when (recordingState) {
                is VideoRecordEvent.Start -> {
                    currentRecording?.pause()
                    //captureViewBinding.stopButton.visibility = View.VISIBLE
                }
                is VideoRecordEvent.Pause -> currentRecording?.resume()
                is VideoRecordEvent.Resume -> currentRecording?.pause()
                else -> throw IllegalStateException("recordingState in unknown state")
            }
        }
    }

    private suspend fun bindCaptureUsecase(cameraIndex: Int, qualityIndex: Int) {
        val cameraProvider = ProcessCameraProvider.getInstance(requireContext()).await()
        val cameraSelector = getCameraSelector(cameraIndex)
        val quality = viewModel.cameraCapabilities[cameraIndex].qualities[qualityIndex]
        val qualitySelector = QualitySelector.from(quality)

        binding.pvCamera.updateLayoutParams<ConstraintLayout.LayoutParams> {
            val orientation = this@VideoFragment.resources.configuration.orientation
            dimensionRatio = quality.getAspectRatioString(quality,
                (orientation == Configuration.ORIENTATION_PORTRAIT))
        }

        val preview = Preview.Builder()
            //.setTargetRotation(Surface.ROTATION_90)
            //.setTargetAspectRatio(quality.getAspectRatio(quality))
            .build().apply {
                setSurfaceProvider(binding.pvCamera.surfaceProvider)
            }

        // build a recorder, which can:
        //   - record video/audio to MediaStore(only shown here), File, ParcelFileDescriptor
        //   - be used create recording(s) (the recording performs recording)
        val recorder = Recorder.Builder()
            .setQualitySelector(qualitySelector)
            .build()
        //videoCapture
        videoCapture = VideoCapture.withOutput(recorder)

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, videoCapture, preview)
        } catch (ex: Exception) {
            // we are on main thread, let's reset the controls on the UI.
            //Log.e(TAG, "Use case binding failed", exc)
            //resetUIandState("bindToLifecycle failed: $exc")
            ex.printStackTrace()
        }
        //enableUI(true)
    }
    private fun getCameraSelector(idx: Int) : CameraSelector {
        if (viewModel.cameraCapabilities.size == 0) {
            Log.i(TAG, "Error: This device does not have any camera, bailing out")
            requireActivity().finish()
        }
        return (viewModel.cameraCapabilities[idx % viewModel.cameraCapabilities.size].camSelector)
    }
    @SuppressLint("MissingPermission")
    private fun startRecording() {
        // create MediaStoreOutputOptions for our recorder: resulting our recording!
        val name = "mine_"+Calendar.getInstance().time.time + ".mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, name)
        }
        mediaStoreOutput = MediaStoreOutputOptions.Builder(
            requireActivity().contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        // configure Recorder and Start recording to the mediaStoreOutput.
        val p = videoCapture.output.prepareRecording(requireActivity(), mediaStoreOutput)
            .apply {
                if (viewModel.audioEnabled) {
                    this.withAudioEnabled()
                }
                viewModel.state = VideoState.RECORD
            }
        currentRecording = p.start(mainThreadExecutor, captureListener)

        Log.i(TAG, "Recording started")
    }
    @NeedsPermission(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO)
    fun checkCamera(){
        viewModel.audioEnabled = true
        initCameraFragment()
    }
    @OnPermissionDenied(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO)
    fun onCameradenied(){
        Toast.makeText(requireContext(), R.string.need_camera, Toast.LENGTH_LONG).show()
        onBackPressed()
    }
    override fun onStopRecord() {
        if (currentRecording == null || recordingState is VideoRecordEvent.Finalize) {
            viewModel.state = VideoState.IDLE
            return
        }
        val recording = currentRecording
        if (recording != null) {
            recording.stop()
            currentRecording = null
            viewModel.state = VideoState.IDLE
        }
    }

    override fun onQualityChanged() {
        val qualityDialog = QuatitySelectorDialog.newInstance(object: OnQualitySelect {
            override fun onUHD() {
                val q = viewModel.cameraCapabilities.get(viewModel.cameraIndex).qualities.find { it == Quality.UHD }
                q?.let {
                    viewModel.qualityIndex = viewModel.cameraCapabilities.get(viewModel.cameraIndex).qualities.indexOf(it)
                    binding.ivQuality.isEnabled = true
                    binding.ivQuality.setImageResource(R.drawable.q_uhd)
                    viewModel.onRebind()
                }
            }
            override fun onFHD() {
                val q = viewModel.cameraCapabilities.get(viewModel.cameraIndex).qualities.find { it == Quality.FHD }
                q?.let {
                    viewModel.qualityIndex = viewModel.cameraCapabilities.get(viewModel.cameraIndex).qualities.indexOf(it)
                    binding.ivQuality.isEnabled = true
                    binding.ivQuality.setImageResource(R.drawable.q_fhd)
                    viewModel.onRebind()
                }
            }
            override fun onHD() {
                val q = viewModel.cameraCapabilities.get(viewModel.cameraIndex).qualities.find { it == Quality.HD }
                q?.let {
                    viewModel.qualityIndex = viewModel.cameraCapabilities.get(viewModel.cameraIndex).qualities.indexOf(it)
                    binding.ivQuality.isEnabled = true
                    binding.ivQuality.setImageResource(R.drawable.q_hd)
                    viewModel.onRebind()
                }
            }
            override fun onSD() {
                val q = viewModel.cameraCapabilities.get(viewModel.cameraIndex).qualities.find { it == Quality.SD }
                q?.let {
                    viewModel.qualityIndex = viewModel.cameraCapabilities.get(viewModel.cameraIndex).qualities.indexOf(it)
                    binding.ivQuality.isEnabled = true
                    binding.ivQuality.setImageResource(R.drawable.q_sd)
                    viewModel.onRebind()
                }
            }
        })
        qualityDialog.show(childFragmentManager, "QUALITY")
    }

    override fun onVideoStart() {
        binding.ivCameraSwitch.isEnabled = true
        binding.ivQuality.isEnabled = true
        binding.ivStartVideo.setImageResource(R.drawable.start_video)
    }

    override fun onVideoStop() {
        binding.ivCameraSwitch.isEnabled = false
        binding.ivQuality.isEnabled = false
        binding.ivStartVideo.setImageResource(R.drawable.two_circle)
    }
    @SuppressLint("NewApi")
    private val captureListener = Consumer<VideoRecordEvent> { event ->
        if (event !is VideoRecordEvent.Status) {
            recordingState = event
        }
        if (event is VideoRecordEvent.Finalize) {
            lifecycleScope.launch {
                Log.d(TAG, "State:"+viewModel.state.toString())
                val uri = event.outputResults.outputUri
                val bitmap = bitmapFromReource(requireContext(), R.drawable.ic_video)
                VideoFragment.dest?.onDestinatation(bitmap!!, uri)
            }
        }
    }
}