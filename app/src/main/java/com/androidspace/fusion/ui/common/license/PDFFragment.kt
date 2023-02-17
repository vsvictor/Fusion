package com.androidspace.fusion.ui.common.license

import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.databinding.FragmentPdfBinding
import com.infotech.mines.ui.common.license.PDFViewModel
import java.io.InputStream
import java.net.URL


@Layout(R.layout.fragment_pdf)
class PDFFragment : BaseFragment<FragmentPdfBinding, PDFViewModel>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.polView.setBackgroundColor(Color.LTGRAY);
    }
    override fun onInit() {
        super.onInit()
        binding.model = viewModel
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        arguments.let {
            viewModel.last = it?.getInt("last")?:-1
            val fileName = it?.getString("name")
            fileName?.let {
                val input: InputStream = URL(fileName).openStream()
                binding.polView.fromStream(input).defaultPage(0)
                    .enableAnnotationRendering(true)
                    .spacing(10)
                    .load()
            }
            //viewModel.back = it?.getInt("back")?:R.id.menuFragment
            val p = it?.getInt("page", -1)
            p?.let {
                viewModel.page = it
            }
        }
    }
}