package com.androidspace.fusion.ui.route

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.view.isVisible
import com.androidspace.fusion.Constants
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.data.model.MapData
import com.androidspace.fusion.databinding.FragmentRouteBinding
import com.androidspace.fusion.ui.common.OnProgress
import com.androidspace.fusion.ui.route.data.OnMap
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import com.esri.arcgisruntime.portal.PortalItem
import com.esri.arcgisruntime.security.AuthenticationChallengeHandler
import com.esri.arcgisruntime.security.AuthenticationManager
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions


@RuntimePermissions
@Layout(R.layout.fragment_route)
class RouteFragment : BaseFragment<FragmentRouteBinding, RouteViewModel>(), OnProgress, OnMap {
    private val TAG = RouteFragment::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val handler: AuthenticationChallengeHandler = DefaultAuthenticationChallengeHandler(requireActivity())
        AuthenticationManager.setAuthenticationChallengeHandler(handler)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.onProgress = this
        viewModel.onMap = this
        binding.model = viewModel
        arguments?.let {
            val basemap = it.getParcelable<MapData>("basemap")
            basemap?.let {
                viewModel.data = it
                viewModel.portalItem = PortalItem(Constants.portal, it.itemID)
            }
        }
        startLoadWithPermissionCheck()
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
    override fun onResume() {
        super.onResume()
        binding.mapView.resume()
    }
    override fun onPause() {
        binding.mapView.pause()
        super.onPause()
    }
    override fun onDestroy() {
        binding.mapView.dispose()
        super.onDestroy()
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

    override fun onProgerssVisible(visible: Boolean) {
        binding.prBar.isVisible = visible
    }

    override fun onMapLoaded(mainMap: ArcGISMap?, layer: FeatureLayer?) {
        mainMap?.let { lMap ->
            binding.mapView.apply {
                map = lMap

                viewModel.point?.let {
                    onKeyboadVisibleChanged?.setKeyboarVisible(false)
                    setViewpointAsync(Viewpoint(it, 20000.0))
                } ?: kotlin.run {
                    locationDisplay.apply {
                        autoPanMode = LocationDisplay.AutoPanMode.RECENTER
                        //initialZoomScale = 50000.0
                        addDataSourceStatusChangedListener {
                            if (it.isStarted) {
                                location?.position?.let {
                                    val regions = lMap.operationalLayers.find { it.name == "ukraine_regions" }
                                    regions?.let {
                                        binding.mapView.setViewpoint(Viewpoint(it.fullExtent))
                                    }
                                }
                            }
                        }
                    }.startAsync()

                }
            }
        }
    }
}