package com.infotech.mines.ui.splash

import android.os.Bundle
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.databinding.FragmentSplashBinding
import com.androidspace.fusion.ui.splash.SplashViewModel

@Layout(R.layout.fragment_splash)
class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = requireActivity()
        binding.model = viewModel
        arguments?.let {
            viewModel.next = it.getInt("next")
        }
        viewModel.startApp()
    }

}