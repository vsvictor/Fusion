package com.androidspace.fusion.ui.profile.offline

import android.app.Application
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.androidspace.fusion.BuildConfig
import com.androidspace.fusion.Constants
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.data.model.MapData
import com.androidspace.fusion.data.model.PointData
import com.androidspace.fusion.domain.usecase.UseCaseBasemap
import com.androidspace.fusion.ui.common.OnAddress
import com.androidspace.fusion.ui.common.OnMarker
import com.androidspace.fusion.ui.common.OnProgress
import com.androidspace.fusion.ui.profile.data.Offliner
import com.androidspace.fusion.ui.profile.data.OnOffline
import com.androidspace.fusion.ui.route.data.OnMap
import com.esri.arcgisruntime.ArcGISRuntimeException
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.PartCollection
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.PointCollection
import com.esri.arcgisruntime.geometry.Polygon
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalItem
import com.esri.arcgisruntime.tasks.geocode.LocatorTask
import com.esri.arcgisruntime.tasks.geocode.ReverseGeocodeParameters
import java.io.File

class OfflineMapViewModel(override val app: Application, private val useCaseBasemap: UseCaseBasemap) : BaseViewModel<Object>(app) {
    private val TAG = OfflineMapViewModel::class.java.simpleName
    var point: PointData? = null
    var address: String? = null
    var onMap: OnMap? = null
    var onProgress: OnProgress? = null
    var onMarker: OnMarker? = null
    var onAddress: OnAddress? = null
    var onOffline: OnOffline? = null
    private val locator: LocatorTask
    private var lock = false
    var reloaded = false
    private val portal: Portal
    private val portalItem: PortalItem
    var mapTopLeft: Point? = null
    var mapTopRight: Point? = null
    var mapBottomLeft: Point? = null
    var mapBottomRight: Point? = null
    var map: ArcGISMap? = null
    private var dirName: String = "temp"
    private lateinit var offliner: Offliner
    init {
        portal = Portal(BuildConfig.PORTAL_URL, false)
        portalItem = PortalItem(portal, useCaseBasemap.basemap)
        locator = LocatorTask("https://geocode-api.arcgis.com/arcgis/rest/services/World/GeocodeServer");
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navController?.navigate(R.id.offlineMapListFragment)
    }
    fun loadMap() {
        if(lock) return
        else lock = true
        onProgress?.onProgerssVisible(true)
        map = ArcGISMap(portalItem).apply {
            addLoadStatusChangedListener {
                val state = it.newLoadStatus
                Log.d(TAG, "Map:" + state)
                if(state == LoadStatus.FAILED_TO_LOAD){
                    lock = false
                    if(!reloaded){
                        reloaded = true
                        retryLoadAsync()
                    }
                }
                if (state == LoadStatus.LOADED) {
                    lock = false
                    onProgress?.onProgerssVisible(false)
                    map = this
                    loadData(this)
                }
            }
            loadAsync()
        }
    }
    fun loadData(map: ArcGISMap) {
        onMap?.onMapLoaded(map)
    }
    fun onFolderName(editable: Editable){
        dirName = editable.toString().trim()
    }
    fun onMyLocation(view: View){}
    fun onOffline(view: View){
        onOffline?.onOfflineShow()
    }
    fun createJob(view: View){
        val fName = app.getExternalFilesDir(null)?.path+ File.separator+dirName
        //onStartJobOffline(fName)
        onPreplanedJob(fName)
    }
    private fun onStartJobOffline(folderName: String){
        val arr = ArrayList<Point>().apply {
            mapTopLeft?.let {
                add(it)
            }
            mapTopRight?.let {
                add(it)
            }
            mapBottomRight?.let {
                add(it)
            }
            mapBottomLeft?.let {
                add(it)
            }
        }
        val points = PointCollection(arr)
        val coll = PartCollection(points)
        val geo = Polygon(coll)
        map?.let {
            offliner = Offliner(geo, it, folderName,100000.0, 10000.0)
            offliner.createMMPK({
                Log.d(TAG, "MMPK:"+folderName+", version:"+it.version)
                it.close()
                onOffline?.onSuccess()
            }, {
                onOffline?.onFail(it)
            }, {
                onOffline?.onProgress(it)
            })
        }
    }
    private fun onPreplanedJob(folderName: String){
        val arr = ArrayList<Point>().apply {
            mapTopLeft?.let {
                add(it)
            }
            mapTopRight?.let {
                add(it)
            }
            mapBottomRight?.let {
                add(it)
            }
            mapBottomLeft?.let {
                add(it)
            }
        }
        val points = PointCollection(arr)
        val coll = PartCollection(points)
        val geo = Polygon(coll)
        map?.let {
            offliner = Offliner(geo, it, folderName,100000.0, 10000.0)
            offliner.createPreplaned()
        }
    }
    fun cancelJob(view: View){
        offliner.cancel()
        onOffline?.onCancel()
    }

    fun onSearch(view: View){}
    fun onFilter(view: View){}

    fun getAddress(point: Point){
        val parameters = ReverseGeocodeParameters().apply {
            maxResults = 1
            outputLanguageCode = "UA"

        }
        val res = locator.reverseGeocodeAsync(point, parameters)
        res.addDoneListener {
            val result = res.get().first()
            var address = result.attributes["Address"].toString()
            if(TextUtils.isEmpty(address)){
                val country = result.attributes["CntryName"].toString()
                if(!TextUtils.isEmpty(country)){
                    address +=  country+", "
                }
                val region = result.attributes["Region"].toString()
                if(!TextUtils.isEmpty(region)){
                    address += region+", "
                }
                val city = result.attributes["City"].toString()
                if(!TextUtils.isEmpty(city)){
                    address += city
                }
            }
            onAddress?.onAddress(address)
        }
    }
}