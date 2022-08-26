package com.androidspace.fusion.ui.profile.offline

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import com.androidspace.fusion.Constants
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.data.model.MapData
import com.androidspace.fusion.domain.usecase.UseCaseBasemap
import com.androidspace.fusion.ui.common.OnProgress
import com.androidspace.fusion.ui.route.data.OnMap
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.portal.PortalItem

class RegionOfflineMapViewModel(override val app: Application, private val useCaseBasemap: UseCaseBasemap) : BaseViewModel<Object>(app) {
    private val TAG = RegionOfflineMapViewModel::class.java.simpleName
    var data: MapData? = null
    var portalItem = PortalItem(Constants.portal, useCaseBasemap.basemap)
    var onMap: OnMap? = null
    var onProgress: OnProgress? = null
    var point: Point? = null
    lateinit var map: ArcGISMap
    fun loadMap() {
        onProgress?.onProgerssVisible(true)
        //map = ArcGISMap(Basemap(portalItem)).apply {
        map = ArcGISMap(Basemap(portalItem)).apply {
            addLoadStatusChangedListener {
                val state = it.newLoadStatus
                Log.d(TAG, "Map:" + state)
                if(state == LoadStatus.FAILED_TO_LOAD){
                    onProgress?.onProgerssVisible(false)
                }
                if (state == LoadStatus.LOADED) {
                    onProgress?.onProgerssVisible(false)
                    map = this
                    onMap?.onMapLoaded(this)
                    //loadData(this)
                }
            }
            loadAsync()
        }
    }

    private fun loadData(map: ArcGISMap){
        val table = ServiceFeatureTable(Constants.regions)
        onProgress?.onProgerssVisible(true)
        FeatureLayer(table).apply {
            addLoadStatusChangedListener {
                val state = it.newLoadStatus
                Log.d(TAG, "Layer:" + state)
                if(state == LoadStatus.FAILED_TO_LOAD){
                    onProgress?.onProgerssVisible(false)
                }
                if (state == LoadStatus.LOADED) {
                    onProgress?.onProgerssVisible(false)
                    map.operationalLayers.add(this)
                    onMap?.onMapLoaded(map)
                }
            }
            loadAsync()
        }
    }
}