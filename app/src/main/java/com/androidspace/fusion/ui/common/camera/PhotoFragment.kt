package com.androidspace.fusion.ui.common.camera

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.camera2.CameraDevice
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.core.impl.CameraConfig
import androidx.camera.core.impl.PreviewConfig
import androidx.camera.core.impl.SessionConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import permissions.dispatcher.OnPermissionDenied
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.databinding.FragmentPhotoBinding
import com.androidspace.fusion.ui.common.camera.data.OnBitmapDestinatation
import com.androidspace.fusion.ui.common.camera.data.OnFragmentChange

private const val RATIO_4_3_VALUE = 4.0 / 3.0
private const val RATIO_16_9_VALUE = 16.0 / 9.0

@Layout(R.layout.fragment_photo)
@RuntimePermissions
class PhotoFragment : BaseFragment<FragmentPhotoBinding, PhotoViewModel>(), OnFragmentChange {

    companion object{
        private var dest: OnBitmapDestinatation? = null
        const val NORM_LIGHT = 150
        fun setOnDestinatationListener(listener: OnBitmapDestinatation?){
            this.dest = listener
        }
    }

    private val TAG = PhotoFragment::class.java.simpleName

    private var cameraProvider: ProcessCameraProvider? = null
    private var lensFacing: Int = CameraSelector.LENS_FACING_FRONT
    private lateinit var viewFinder: PreviewView
    private var cameraSelector: CameraSelector? = null
    private var preview: Preview? = null
    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var flashMode = ImageCapture.FLASH_MODE_AUTO

    private lateinit var mainExecutor: Executor
    //private lateinit var cameraExecutor: ExecutorService

    private lateinit var sessionConfig: SessionConfig

    private var screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels
    private var screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels
    private var displayId: Int = -1
    private val displayManager by lazy {
        requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@PhotoFragment.displayId) {
                view.display?.let {
                    Log.d(TAG, "Rotation changed: ${it.rotation}")
                    imageCapture?.targetRotation = it.rotation
                }
            }
        } ?: Unit
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //onNavigateView = context as OnNavigateView
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainExecutor = ContextCompat.getMainExecutor(requireContext())
        //cameraExecutor = Executors.newSingleThreadExecutor()
        //thread.start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        onActionBarState?.onActionBarView(true)

        requireActivity().setTitle(R.string.camera)
        //onNavigateView?.onBottomBarVisible(false)

        screenWidth = Resources.getSystem().displayMetrics.widthPixels
        screenHeight = Resources.getSystem().displayMetrics.heightPixels
        //val d = displayManager.displays
        Log.d(TAG, "Size:"+screenWidth+"x"+screenHeight)
        //handler = Handler(thread.looper)

        //val sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //val light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        displayManager.registerDisplayListener(displayListener, null)
        viewFinder = binding.pvCamera
/*        viewFinder.previewStreamState.observe(viewLifecycleOwner, Observer {
            when(it){
                PreviewView.StreamState.IDLE -> {}
                PreviewView.StreamState.STREAMING -> {}
            }
        })*/
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.model = viewModel
        viewModel.setOnDestinatationListener(dest)
        viewModel.args = arguments
        viewModel.setOnFragmentCloseListener(this)
        val back = arguments?.getInt("back")
        back?.let {
            viewModel.backID = it
        }
        initPhoto()
        initFlash()
    }

/*    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        bindCameraUseCases()
    }*/

    override fun onResume() {
        super.onResume()
        initCamera()
    }

    private fun initCamera(){
        viewFinder.post{
            displayId = viewFinder.display.displayId
            startCameraWithPermissionCheck()
        }

    }
    private fun initPhoto(){
        viewModel.apply {
            takePhoto.observe(viewLifecycleOwner, Observer {
                if(it){
                    capturePhoto()
                }
            })
            selector.observe(viewLifecycleOwner, Observer {
                lensFacing = if(it) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT
                bindCameraUseCases()
            })
        }
    }
    private fun initFlash(){
        viewModel.flash.observe(viewLifecycleOwner, Observer {
            when(it){
                FlashState.AUTO -> {
                    imageCapture?.flashMode = ImageCapture.FLASH_MODE_AUTO
                    binding.ivFlash.setImageResource(R.drawable.flash_auto)
                }
                FlashState.ON -> {
                    imageCapture?.flashMode = ImageCapture.FLASH_MODE_ON
                    binding.ivFlash.setImageResource(R.drawable.flash_on)
                }
                FlashState.OFF -> {
                    imageCapture?.flashMode = ImageCapture.FLASH_MODE_OFF
                    binding.ivFlash.setImageResource(R.drawable.flash_off)
                }
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        //onNavigateView?.onBottomBarVisible(false)
        stopCamera()
    }

    override fun onDetach() {
        super.onDetach()
        //onNavigateView = null
    }

    @NeedsPermission(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            lensFacing = when {
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }
            //lensFacing = CameraSelector.LENS_FACING_FRONT
            try {
                bindCameraUseCases()
            }catch (ex: IllegalStateException){
                onBackPressed()
            }
        }, mainExecutor)
    }
    @OnPermissionDenied(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun cameraDenied(){
        Toast.makeText(requireContext(), R.string.need_camera, Toast.LENGTH_LONG).show()
        onBackPressed()
    }
    @SuppressLint("UnsafeExperimentalUsageError", "RestrictedApi")
    protected fun bindCameraUseCases(){
        val metrics = DisplayMetrics().also {
            if(viewFinder.display == null) {
                return
            }
            viewFinder.display.getRealMetrics(it)

        }
        //val metrics = Resources.getSystem().displayMetrics

        Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        var rotation = viewFinder.display.rotation

        Log.d(TAG, "Rotation changed on bind: ${viewFinder.display.rotation}")

        //val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")
        cameraProvider?.let {
            cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

            val sessionBuilder = SessionConfig.Builder()
            sessionBuilder.addDeviceStateCallback(deviceStateCallback)
            sessionBuilder.build()
            sessionConfig = sessionBuilder.build()
            sessionConfig.surfaces.forEach {
                it.surface.get().apply {
                }
            }

            val previewBuilder = Preview.Builder()
                .setDefaultResolution(Size(screenWidth, screenHeight))
                .setMaxResolution(Size(screenWidth, screenHeight))
                .setTargetResolution(Size(screenWidth, screenHeight))
                .setTargetRotation(rotation)
                .setDefaultSessionConfig(sessionConfig)
            preview = previewBuilder.build().apply {
                this.setSurfaceProvider(viewFinder.surfaceProvider)
                //this.targetRotation = Surface.ROTATION_0
                //it.setViewPortCropRect(Rect(0,0,screenHeight, screenWidth))
                //it.updateSuggestedResolution(Size(800, 600))
            }

            val builder = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetAspectRatio(screenAspectRatio)
                    .setFlashMode(flashMode)
                    .setBufferFormat(ImageFormat.YUV_420_888)
                    .setDefaultResolution(Size(800,600))
                    .setTargetRotation(rotation)
            imageCapture = builder.build()
            it.unbindAll()
            try {
                camera = it.bindToLifecycle(this, cameraSelector!!, preview, imageCapture)
                val i = 0
                //viewFinder.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                //imagePreview?.setSurfaceProvider(binding.previewView.surfaceProvider)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }
    }

    fun capturePhoto() {
        Log.d(TAG, "Captured photo")
        val detect = object : ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeExperimentalUsageError")
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                Log.d(TAG, "Success captured")
/*
                val degree = imageProxy.imageInfo.rotationDegrees
                val bitmap = Bitmap.createBitmap(imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888)
                MediaStore.Images.Media.insertImage(requireContext().getContentResolver(), bitmap, "Deminding" , "");
*/
                viewModel.convert(imageProxy)
                super.onCaptureSuccess(imageProxy)
            }
            override fun onError(exception: ImageCaptureException) {
                try {
                    stopCamera()
                    exception.printStackTrace()
                }catch (ex :IllegalStateException){
                    ex.printStackTrace()
                }
            }
        }
        //imageCapture?.takePicture(cameraExecutor, detect)
        imageCapture?.takePicture(Executors.newSingleThreadExecutor(), detect)
    }
    private fun stopCamera() {
        //cameraExecutor.shutdown()
        displayManager.unregisterDisplayListener(displayListener)
        cameraProvider?.unbindAll()
        Log.d(TAG, "Camera stopped")
    }
    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }
    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }
    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }
    private val deviceStateCallback = object : CameraDevice.StateCallback(){
        override fun onOpened(camera: CameraDevice) {
            Log.d(TAG, "Camera opened")
        }

        override fun onDisconnected(camera: CameraDevice) {
            Log.d(TAG, "Camera disconnected")
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.d(TAG, "Camera error")
        }
    }

    override fun onFragmentOpen(fragment: Fragment?) {
        fragment?.let {
            //requireActivity().supportFragmentManager.beginTransaction().add(R.id.llCamera, it).commit()
        }
    }
    override fun onFragmentClose(fragment: Fragment?) {
        fragment?.let {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }?: kotlin.run {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }
    }
}