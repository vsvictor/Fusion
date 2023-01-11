package com.androidspace.fusion.ui.navigation

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.databinding.FragmentNavigationBinding
import com.androidspace.fusion.ui.route.data.OnMap
import com.esri.arcgisruntime.ArcGISRuntimeException
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap

@Layout(R.layout.fragment_navigation)
class NavigationFragment : BaseFragment<FragmentNavigationBinding, NavigationViewModel>(), OnMap {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.onMap = this
        binding.model = viewModel
        viewModel.loadLocalMap()
    }

    override fun onMapLoaded(map: ArcGISMap?, layer: FeatureLayer?) {
            try {
                binding.mapView.map = map
            }catch (ex: ArcGISRuntimeException){}
    }
    override fun onResume() {
        super.onResume()
        binding.mapView.resume()
    }
    override fun onPause() {
        binding.mapView.pause()
        super.onPause()
    }
    override fun onDestroyView() {
        binding.mapView.dispose()
        super.onDestroyView()
    }

}