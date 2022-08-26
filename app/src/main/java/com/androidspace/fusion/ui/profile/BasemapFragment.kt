package com.androidspace.fusion.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.databinding.FragmentBasemapBinding
import com.androidspace.fusion.ui.OnCredentions
import com.androidspace.fusion.ui.common.OnProgress
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.security.AuthenticationManager
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler

@Layout(R.layout.fragment_basemap)
class BasemapFragment : BaseFragment<FragmentBasemapBinding, BasemapViewModel>(), OnProgress {
    //private var onCredentions: OnCredentions? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.onProgress = this
        binding.model = viewModel
        //viewModel.portal = onCredentions?.portal
        //viewModel.clear()
        viewModel.loadMaps()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //onCredentions = context as OnCredentions
    }

    override fun onDetach() {
        //onCredentions = null
        super.onDetach()
    }
    override fun onProgerssVisible(visible: Boolean) {
        binding.prBar.isVisible = visible
    }
}