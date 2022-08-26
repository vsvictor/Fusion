package com.androidspace.fusion.ui.profile

import android.annotation.SuppressLint
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
import com.androidspace.fusion.data.model.RegionData
import com.androidspace.fusion.databinding.FragmentRegionListBinding
import com.androidspace.fusion.ui.common.OnProgress
import com.androidspace.fusion.ui.profile.data.OnDownload
import com.androidspace.fusion.ui.route.data.OnMap
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
@Layout(R.layout.fragment_region_list)
class RegionListFragment : BaseFragment<FragmentRegionListBinding, RegionListViewModel>(), OnMap, OnDownload, OnProgress {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.onDownload = this
        viewModel.onProgress = this
        viewModel.onMap = this
        binding.model = viewModel
        viewModel.loadMap()
    }
    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun loadfile(data: RegionData){
        viewModel.offlineMap(data)
    }
    override fun onDownloadFile(data: RegionData) {
        data.let {
            loadfileWithPermissionCheck(it)
        }
    }

    override fun onProgerssVisible(visible: Boolean) {
        binding.prBar.isVisible = visible
    }

    override fun onMapLoaded(map: ArcGISMap?, layer: FeatureLayer?) {
        map?.let {
            //binding.mapView.map = map
            viewModel.loadRegions()
        }
    }

}