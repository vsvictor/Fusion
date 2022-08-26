package com.androidspace.fusion.ui.route

import android.app.Application
import android.util.Log
import android.view.View
import com.androidspace.fusion.BuildConfig
import com.androidspace.fusion.Constants
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.data.model.MapData
import com.androidspace.fusion.domain.usecase.UseCaseBasemap
import com.androidspace.fusion.ui.common.OnProgress
import com.androidspace.fusion.ui.route.data.OnMap
import com.androidspace.fusion.utils.toByteArray
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.portal.*
import com.esri.arcgisruntime.security.UserCredential

class RouteViewModel(override val app: Application, private val useCaseBasemap: UseCaseBasemap) : BaseViewModel<Object>(app) {
    private val TAG = RouteViewModel::class.java.simpleName
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
                    loadData(this)
                }
            }
            loadAsync()
        }
    }

    private fun loadData(map: ArcGISMap){
        val table = ServiceFeatureTable(Constants.regions)
        onProgress?.onProgerssVisible(true)
        FeatureLayer(table).apply {
            //renderer = regRenderer
            //isLabelsEnabled = true
            addLoadStatusChangedListener {
                val state = it.newLoadStatus
                Log.d(TAG, "Layer:" + state)
                if(state == LoadStatus.FAILED_TO_LOAD){
                    onProgress?.onProgerssVisible(false)
                }
                if (state == LoadStatus.LOADED) {
                    onProgress?.onProgerssVisible(false)
                    //map.operationalLayers.add(this)
                    //Zmap.saveAsAsync(corporatePortal, baseMapFolder, portalItem.title, portalItem.tags, portalItem.description, )
                    onMap?.onMapLoaded(map)
                }
            }
            loadAsync()
        }
    }
    fun saveTo(view: View){
        var currFolder: PortalFolder? = null
        val corporatePortal = Portal(BuildConfig.CORPORATE_PORTAL_URL, true).apply {
            credential = UserCredential("viktor.dzhurliak", "Vestern74");
        }
        corporatePortal.addDoneLoadingListener {
            Log.d(TAG, "Status:"+corporatePortal.loadStatus.name)
            if(corporatePortal.loadStatus == LoadStatus.LOADED){
                try {
                    val content = corporatePortal.user.fetchContentAsync().get()
                    for(f in content.folders){
                        if(f.title == "Basemap"){
                            currFolder = f
                        }
                    }
                    map.saveAsAsync(corporatePortal, currFolder, portalItem.title,portalItem.tags,portalItem.description, data?.thumb?.toByteArray(), true).addDoneListener {
                        Log.d(TAG, "Saved")
                    }
                }catch (ex: Exception){
                    ex.printStackTrace()
                }
            }
        }
        corporatePortal.loadAsync()
    }

}