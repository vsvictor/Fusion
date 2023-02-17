package com.androidspace.fusion.ui.common.camera

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import com.androidspace.fusion.R
import com.androidspace.fusion.databinding.LayoutPhotoBottomSheetBinding
import com.androidspace.fusion.ui.common.camera.data.OnBitmapDestinatation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SourcePhotoDialog : BottomSheetDialogFragment() {
    companion object {
        private var listener: OnSelectSourcePhoto? = null
        private var title: String? = null

        fun newInstance(selector: OnSelectSourcePhoto, titleString: String? = ""): SourcePhotoDialog {
            listener = selector
            title = titleString
            return SourcePhotoDialog()
        }
    }

    private var dest: OnBitmapDestinatation? = null

    private lateinit var binding: LayoutPhotoBottomSheetBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<LayoutPhotoBottomSheetBinding>(inflater, R.layout.layout_photo_bottom_sheet, container, false)

        //val view = inflater.inflate(R.layout.layout_photo_bottom_sheet, container,false)

        title?.let {
            binding.tvTakePhoto.text = title
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTakePhoto.setOnClickListener {
            dialog?.dismiss()
            listener?.onPhoto()
        }
        binding.tvCamera.setOnClickListener {
            dialog?.dismiss()
            listener?.onCamera(dest)
        }
        binding.tvVideo.setOnClickListener {
            dialog?.dismiss()
            listener?.onVideo(dest)
        }
        binding.tvGallery.setOnClickListener {
            dialog?.dismiss()
            listener?.onGallery()
        }
        binding.tvCancel.setOnClickListener {
            dialog?.dismiss()
            listener?.onCancel()
        }
    }
    fun setOnDestinatationListener(listenr: OnBitmapDestinatation){
        this.dest = listenr
    }

    override fun onCancel(dialog: DialogInterface) {
        listener?.onCancel()
        super.onCancel(dialog)
    }

}