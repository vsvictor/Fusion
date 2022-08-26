package com.androidspace.fusion.ui.common.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.databinding.FragmentAlphaPhotoBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

@Layout(R.layout.fragment_alpha_photo)
class AlphaPhotoFragment : BaseFragment<FragmentAlphaPhotoBinding, ShowPhotoViewModel>() {
    private var navigateViewState: Boolean = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        onActionBarState?.onActionBarView(true)
        binding.ivClose.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.model = viewModel
        arguments?.let {
            viewModel.backID = it.getInt("back", -1)
            val url = it.getString("url")
            url?.let {
                if(!it.isEmpty()){
                    binding.prBar.isVisible = true
/*                    Picasso.get().load(it).into(object: Target{
                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            bitmap?.let {
                                binding.prBar.isVisible = false
                                binding.ivPhoto.setImageBitmap(it)
                            }
                        }
                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                    })*/
                    Glide.with(requireContext()).load(it).listener(object: RequestListener<Drawable>{
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            return true
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            binding.prBar.isVisible = false
                            binding.ivPhoto.setImageDrawable(resource)
                            return true
                        }
                    }).into(binding.ivPhoto)
                }
            }
        }
    }
}