package com.androidspace.fusion.ui.common.web

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.data.model.URLString
import com.androidspace.fusion.databinding.FragmentWebBinding
import com.androidspace.fusion.ui.common.OnBottomBarVisible
import com.androidspace.fusion.ui.common.OnLoadURL

@Layout(R.layout.fragment_web)
class WebFragment : BaseFragment<FragmentWebBinding, WebViewModel>(), OnLoadURL {
    private var onBottomBarVisible: OnBottomBarVisible? = null
    private val USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36 OPR/38.0.2220.41"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.wvMain.clearCache(true)
        binding.wvMain.clearHistory()
        binding.wvMain.getSettings().javaScriptEnabled = true
        binding.wvMain.getSettings().javaScriptCanOpenWindowsAutomatically = true
        binding.wvMain.getSettings().loadsImagesAutomatically = true
        binding.wvMain.getSettings().loadWithOverviewMode = true
        binding.wvMain.getSettings().useWideViewPort = true
        binding.wvMain.getSettings().domStorageEnabled = true
        binding.wvMain.getSettings().userAgentString = USER_AGENT
        onBottomBarVisible?.onBottomBarVisible(false)
        val param = binding.clMain.layoutParams as ViewGroup.MarginLayoutParams
        param.bottomMargin = onBottomBarVisible?.navigationBarHeight()?:0
        binding.clMain.layoutParams = param
    }
    override fun onInit() {
        super.onInit()
        viewModel.onLoadURL = this
        binding.model = viewModel
        viewModel.loaded = false
        arguments?.let {
            //viewModel.back = it.getInt("back", R.id.menuFragment)
            viewModel.url = it.getString("url")
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBottomBarVisible = context as OnBottomBarVisible
    }

    override fun onDetach() {
        super.onDetach()
        onBottomBarVisible = null
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.loaded){
            viewModel.onBackPressed()
        }
    }

    override fun onLoadURL(url: URLString) {
        binding.wvMain.webViewClient = SmallWebClient(viewModel)
        binding.wvMain.loadUrl(url.link)
    }
}