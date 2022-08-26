package com.androidspace.fusion.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresPermission
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.databinding.FragmentRegionMapBinding
import com.androidspace.fusion.ui.profile.RegionMapViewModel
import com.androidspace.fusion.ui.route.data.OnMap
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
@Layout(R.layout.fragment_region_map)
class RegionMapFragment : BaseFragment<FragmentRegionMapBinding, RegionMapViewModel>(), OnMap {
    private val TAG = RegionMapFragment::class.java.simpleName
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.onMap = this
        binding.model = viewModel
        arguments?.let {
            viewModel.region = it.getParcelable("region")
            startLoadWithPermissionCheck()
        }
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun startLoad(){
        viewModel.loadMap()
    }
    @OnPermissionDenied(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onDenied(){
        viewModel.loadMap()
    }

    @OnNeverAskAgain(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onNever(){
        viewModel.loadMap()
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

    override fun onMapLoaded(map: ArcGISMap?, layer: FeatureLayer?) {
        Log.d(TAG, "On loaded")
        map?.let {
            binding.mapView.map = map
            Log.d(TAG, "Map loaded")
            for(l in binding.mapView.map.operationalLayers){
                try {
                    Log.d(TAG, "Layer name:"+l.name+", id:"+l.id+" visible:"+l.isVisible+", item: "+l.item.itemId)
                }catch (ex: Exception){
                    ex.printStackTrace()
                }
            }
        }?: kotlin.run {
            Log.d(TAG, "Map is null")
        }
    }
}