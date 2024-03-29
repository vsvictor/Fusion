package com.androidspace.fusion.ui.common.camera

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.databinding.FragmentShowPhotoBinding
import com.squareup.picasso.Picasso

@Layout(R.layout.fragment_show_photo)
class ShowPhotoFragment : BaseFragment<FragmentShowPhotoBinding, ShowPhotoViewModel>() {
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
                    Picasso.get().load(it).into(binding.ivPhoto)
                }
            }
        }
    }
}