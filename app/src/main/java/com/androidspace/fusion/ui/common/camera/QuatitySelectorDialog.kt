package com.androidspace.fusion.ui.common.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import com.androidspace.fusion.R
import com.androidspace.fusion.databinding.QualitySelectorBinding
import com.androidspace.fusion.ui.common.camera.data.OnQualitySelect
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class QuatitySelectorDialog: BottomSheetDialogFragment() {
    companion object {
        private var listener: OnQualitySelect? = null

        fun newInstance(selector: OnQualitySelect): QuatitySelectorDialog {
            listener = selector
            return QuatitySelectorDialog()
        }
    }
    private lateinit var binding: QualitySelectorBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.quality_selector, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<LinearLayoutCompat>(R.id.llUHD).setOnClickListener {
            listener?.onUHD()
            dismiss()
        }
        view.findViewById<LinearLayoutCompat>(R.id.llFHD).setOnClickListener {
            listener?.onFHD()
            dismiss()
        }
        view.findViewById<LinearLayoutCompat>(R.id.llHD).setOnClickListener {
            listener?.onHD()
            dismiss()
        }
        view.findViewById<LinearLayoutCompat>(R.id.llSD).setOnClickListener {
            listener?.onSD()
            dismiss()
        }
    }
}