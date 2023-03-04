package com.androidspace.fusion.ui.maps

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.androidspace.fusion.Constants
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.databinding.FragmentNavigationBinding
import com.androidspace.fusion.ui.route.data.OnMap
import com.arcgismaps.data.Feature
import com.arcgismaps.location.LocationDataSourceStatus
import com.arcgismaps.location.LocationDisplayAutoPanMode
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.layers.ArcGISVectorTiledLayer
import com.arcgismaps.mapping.layers.FeatureLayer
import com.arcgismaps.tasks.exportvectortiles.ItemResourceCache
import kotlinx.coroutines.launch
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
@Layout(R.layout.fragment_navigation)
class MapFragment : BaseFragment<FragmentNavigationBinding, MapViewModel>(), OnMap {
    private val TAG = MapFragment::class.java.simpleName
    override fun onInit() {
        super.onInit()
        viewModel.onMap = this
        lifecycle.addObserver(binding.mapView)
        binding.model = viewModel
        //viewModel.loadMap()
        viewModel.createDatabase()
    }
    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onMapLoaded(map: ArcGISMap?, layer: FeatureLayer?) {
            map?.let {
                binding.mapView.apply {
                    this.map = it
                }
                layer?.let {
                    initSelectRegion(it)
                }
                binding.mapView.post {
                    startPositionWithPermissionCheck()
                }
            }
    }


    @NeedsPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun startPosition(){
        Log.d(TAG, binding.mapView.locationDisplay.dataSource.status.value.toString())
        if(binding.mapView.locationDisplay.dataSource.status.value == LocationDataSourceStatus.Started){
            myPosition()
        }else{
            lifecycleScope.launch {
                binding.mapView.locationDisplay.dataSource.start().onSuccess {
                    myPosition()
                }.onFailure {
                    Log.d(TAG, "Start fail...")
                }
            }
        }

    }

    private fun myPosition() = lifecycleScope.launch {
        binding.mapView.apply {
            locationDisplay.apply {
                setAutoPanMode(LocationDisplayAutoPanMode.Recenter)
            }

            locationDisplay.location.collect{
                Log.d(TAG, "New viewpoint")
                it?.let {
                    val vp = Viewpoint(it.position, 100000.0)
                    this.setViewpoint(vp)
                }?: kotlin.run {
                    Log.d(TAG, "Empty viewpoint")
                }
            }
        }
    }


    fun initSelectRegion(layer: FeatureLayer){
        layer.clearSelection()
        lifecycleScope.launch {
            binding.mapView.let {view ->
                view.onSingleTapConfirmed.collect{ event ->
                    val indentify = view.identifyLayer(layer, event.screenCoordinate, Constants.tolerance, false, 1)
                    indentify.apply {
                        onSuccess { result ->
                            try {
                                val feature = result.geoElements.filterIsInstance<Feature>().first()
                                //layer.selectFeature(feature)
                                val scale = view.mapScale.value
                                viewModel.exportTiles(feature, 0.0, { tiles, path ->
                                    tiles?.let { tileRes ->
                                        path?.let {
                                            val res = ItemResourceCache(it)
                                            val tilesLayer = ArcGISVectorTiledLayer(tileRes)//, res)
                                            binding.mapView.map?.basemap?.value?.baseLayers?.add(
                                                tilesLayer
                                            )
                                        } ?: kotlin.run {
                                            val tilesLayer = ArcGISVectorTiledLayer(tileRes)
                                            binding.mapView.map?.basemap?.value?.baseLayers?.add(
                                                tilesLayer
                                            )
                                        }
                                    }
                                }, {
                                    viewModel.error.postValue(it)
                                })
                            }catch (ex: NoSuchElementException){
                                ex.printStackTrace()
                            }
                        }
                        onFailure {
                            Log.d(TAG, "Not selected")
                        }
                    }
                }
            }

        }
    }

}