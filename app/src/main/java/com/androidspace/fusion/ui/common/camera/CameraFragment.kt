package com.androidspace.fusion.ui.common.camera

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.databinding.FragmentCameraBinding
import com.androidspace.fusion.ui.common.camera.data.OnBitmapDestinatation
import com.androidspace.fusion.ui.common.camera.data.OnVideoDestinatation
import com.androidspace.fusion.ui.common.camera.data.VPAdapter
import com.androidspace.fusion.utils.navigationBarHeight

@Layout(R.layout.fragment_camera)
class CameraFragment : BaseFragment<FragmentCameraBinding, PhotoViewModel>() {
    companion object{
        const val NORM_LIGHT = 150
        fun setOnDestinatationListener(bitmapListener: OnBitmapDestinatation?, videoListener: OnVideoDestinatation?){
            PhotoFragment.setOnDestinatationListener(bitmapListener)
            VideoFragment.setOnDestinatationListener(videoListener)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val param = binding.vpPage.layoutParams as ViewGroup.MarginLayoutParams
        param.bottomMargin = requireContext().navigationBarHeight
        binding.vpPage.layoutParams = param
    }
    override fun onInit() {
        super.onInit()
        val list = ArrayList<Fragment>()
        list.add(PhotoFragment())
        list.add(VideoFragment())
        binding.vpPage.adapter = VPAdapter(this, list)
        binding.model = viewModel
        arguments?.let {
            val p = it.getInt("page", 0)
            binding.vpPage.post {
                binding.vpPage.currentItem = p
            }
        }
    }
}