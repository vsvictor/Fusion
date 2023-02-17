package com.androidspace.fusion.ui.common.camera

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.MediaController
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.databinding.FragmentShowVideoBinding

@Layout(R.layout.fragment_show_video)
class ShowVideoFragment : BaseFragment<FragmentShowVideoBinding, ShowVideoViewModel>() {
    private var navigateViewState: Boolean = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        onActionBarState?.onActionBarView(true)
    }
    override fun onInit() {
        super.onInit()
        binding.model = viewModel
        arguments?.let {
            viewModel.backID = it.getInt("back", -1)
            val url = it.getString("url")
            url?.let {
                if(!it.isEmpty()){
                    showVideo(Uri.parse(it))
                }
            }
        }
    }

    private fun showVideo(uri: Uri){
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.vvVideo)
        binding.vvVideo.setMediaController(mediaController);
        binding.vvVideo.setVideoURI(uri);
        binding.vvVideo.requestFocus();
        binding.vvVideo.start();
    }
}