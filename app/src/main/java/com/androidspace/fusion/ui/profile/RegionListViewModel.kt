package com.androidspace.fusion.ui.profile

import android.app.Application
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.androidspace.fusion.Constants
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.data.model.RegionData
import com.androidspace.fusion.domain.usecase.UseCaseBasemap
import com.androidspace.fusion.ui.common.OnProgress
import com.androidspace.fusion.ui.profile.data.Offliner
import com.androidspace.fusion.ui.profile.data.OnDownload
import com.androidspace.fusion.ui.profile.data.RegionsObservable
import com.androidspace.fusion.ui.route.data.OnMap
import com.androidspace.fusion.utils.Zip
import com.androidspace.fusion.utils.downloader.BinaryFileDownloader
import com.androidspace.fusion.utils.downloader.BinaryFileWriter
import com.androidspace.fusion.utils.downloader.ProgressCallback
import com.androidspace.fusion.utils.findFileName
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.portal.PortalItem
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.io.File


class RegionListViewModel(override val app: Application, private val useCaseBasemap: UseCaseBasemap) : BaseViewModel<Object>(app) {

    private val TAG = RegionListViewModel::class.java.simpleName
    var onDownload: OnDownload? = null
    var onProgress: OnProgress? = null
    var onMap: OnMap? = null
    val regions = RegionsObservable(ArrayList<RegionData>())
    private lateinit var loadedMap: ArcGISMap
    override fun onBackPressed() {
        super.onBackPressed()
        navController?.navigate(R.id.navigation_profile)
    }

    fun loadMap() {
        onProgress?.onProgerssVisible(true)
        ArcGISMap(PortalItem(Constants.portal, useCaseBasemap.basemap)).apply {
            this.
            addLoadStatusChangedListener {
                val state = it.newLoadStatus
                Log.d(TAG, "Map:" + state)
                if(state == LoadStatus.FAILED_TO_LOAD){
                    onProgress?.onProgerssVisible(false)
                }
                if (state == LoadStatus.LOADED) {
                    onProgress?.onProgerssVisible(false)
                    loadedMap = this
                    onMap?.onMapLoaded(this)
                }
            }
            loadAsync()
        }
    }

    fun loadRegions(){
        onProgress?.onProgerssVisible(true)
        val table = ServiceFeatureTable(Constants.regions)
        val queryParameters = QueryParameters()
        queryParameters.whereClause = "1=1"
        queryParameters.isReturnGeometry = true
        val futures = table.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
        futures.addDoneListener {
            try {
                val result = futures.get()
                val iterator: Iterator<Feature> = result.iterator()
                var counter = 0
                val arr = ArrayList<RegionData>()
                while (iterator.hasNext()) {
                    val feature =  iterator.next()
                    val id = feature.attributes["FID"] as Long
                    val n1 = feature.attributes["NAME_1"]?:""
                    val n2 = feature.attributes["TYPE_1"]?:""
                    val sep = if(n2 == "область") " " else ", "
                    val fileName = feature.attributes["NL_NAME_1"]?:""
                    val geo = feature.geometry
                    val reg = RegionData(id, n1.toString()+sep+n2.toString(),0.0, 0.0,fileName.toString(),"0 Mb", geo, false)
                    val f = ""+app.getExternalFilesDir(null)+File.separator+Uri.parse(reg.url).findFileName()!!
                    reg.loaded = File(f).exists() && File(f).isDirectory
                    Log.d(TAG, f)
                    arr.add(reg)
                    counter++
                }
                onProgress?.onProgerssVisible(false)
                arr.sortBy { it.name }
                regions.regions = arr
                Log.d(TAG, "Count: "+counter+", Regions count:"+arr.size);
            } catch (e: Exception) {
                onProgress?.onProgerssVisible(false)
                Log.d(TAG, e.localizedMessage)
            }
        }
    }
    fun onRegionDelete(data: RegionData){
        val fileName = ""+app.getExternalFilesDir(null)+File.separator+Uri.parse(data.url).findFileName()!!+".mmpk"
        val f = File(fileName) //app.getFileStreamPath(fileName);
        Log.d(TAG, f.absolutePath)
        val r = f.delete() //app.deleteFile(fileName)
        Log.d(TAG, "Deleted:"+r)
        val d = regions.regions.find { it.id == data.id }
        d?.let {
            d.loaded = false
            regions.update()
        }
    }
    fun onRegion(data: RegionData){
        if(!data.loaded) {
            onDownload?.onDownloadFile(data)
            //offlineMap(data)
        }else{
            navController?.navigate(R.id.regionMapFragment, Bundle().apply {
                putParcelable("region", data)
            })
        }
    }

/*    fun onDownloadMMPK(url: String){
        val ioScope = CoroutineScope(Dispatchers.IO)
        ioScope.launch {
            downloadMMPK(url)
        }
    }*/

    fun offlineMap(data: RegionData){
        onProgress?.onProgerssVisible(true)
        val folderName = app.getExternalFilesDir(null)?.absolutePath+ File.separator+ Uri.parse(data.url)
        //val filename = data.url+".mmpk"
        Log.d(TAG, folderName)
        data.geometry?.let {
            val off = Offliner(it, loadedMap, folderName, 100000.0, 10000.0)
            off.createMMPK({
                Toast.makeText(app,"Success", Toast.LENGTH_LONG).show()
                Log.d(TAG, "MMPK:"+folderName+", version:"+it.version)
                it.close()
/*                val fName = folderName
                Zip.zipFolder(fName, folderName+".mmpk")*/
                val item = regions.regions.find { it.url == data.url }
                item?.let {
                    it.loaded = true
                    regions.update()
                    onProgress?.onProgerssVisible(false)
                }
            },{}, {})
        }
    }
    override fun clear() {
        super.clear()
        regions.regions = ArrayList<RegionData>()
    }

}