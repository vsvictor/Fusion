package com.androidspace.fusion.ui.common.camera

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.Observer
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.databinding.FragmentCropBinding
import com.androidspace.fusion.utils.toBitmap

@Layout(R.layout.fragment_crop)
class CropFragment : BaseFragment<FragmentCropBinding, CropViewModel>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        onActionBarState?.onActionBarView(true)
    }
    override fun onInit() {
        super.onInit()
        binding.model = viewModel
        val back = arguments?.getInt("back")
        back?.let {
            viewModel.backID = it
        }
        val arr = arguments?.getByteArray("picture")
        arr?.let {
            binding.cropImageView.setImageBitmap(it.toBitmap())
        }
        viewModel.apply {
            cropper.observe(viewLifecycleOwner, Observer {
                if(it){
                    viewModel.cropped = binding.cropImageView.croppedBitmap
                }
            })
        }
    }
}