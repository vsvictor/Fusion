package com.androidspace.fusion.ui.common.camera

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.MediaController
import androidx.core.view.isVisible
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.databinding.FragmentAlphaVideoBinding
import java.lang.RuntimeException
import java.util.*

@Layout(R.layout.fragment_alpha_video)
class AlphaVideoFragment : BaseFragment<FragmentAlphaVideoBinding, ShowVideoViewModel>() {
    private var navigateViewState: Boolean = true
    private lateinit var timer:Timer
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        onActionBarState?.onActionBarView(true)
        binding.ivClose.setOnClickListener{
            requireActivity().onBackPressed()
        }
    }
    override fun onInit() {
        super.onInit()
        binding.model = viewModel
        arguments?.let {
            viewModel.backID = it.getInt("back", -1)
            val url = it.getString("url")
            url?.let {
                if(!it.isEmpty()){
                    //Picasso.get().load(it).into(binding.ivPhoto)
                    showVideo(Uri.parse(it))
                }
            }
        }
    }

    private fun showVideo(uri: Uri){
        binding.prBar.isVisible = true
        timer = Timer()
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.vvVideo)
        binding.vvVideo.setMediaController(mediaController);
        binding.vvVideo.setVideoURI(uri);
        binding.vvVideo.requestFocus();
        binding.vvVideo.setOnPreparedListener(object: MediaPlayer.OnPreparedListener{
            override fun onPrepared(mp: MediaPlayer?) {
                binding.prBar.isVisible = false
                mediaController.show(mp?.duration?:0)
                timer.schedule(object: TimerTask(){
                    override fun run() {
                        try {
                            mediaController.show()
                        }catch (ex: RuntimeException){
                            timer.cancel()
                        }
                    }
                }, 250, 250)
            }
        })
        binding.vvVideo.setOnCompletionListener(object: MediaPlayer.OnCompletionListener{
            override fun onCompletion(mp: MediaPlayer?) {
                mp?.seekTo(0)
            }
        })
        binding.vvVideo.start();
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
    }
}