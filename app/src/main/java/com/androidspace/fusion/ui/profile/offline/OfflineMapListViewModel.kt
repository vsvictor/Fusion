package com.androidspace.fusion.ui.profile.offline

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.lifecycle.viewModelScope
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.data.model.RegionData
import com.androidspace.fusion.ui.profile.data.OnDownload
import com.androidspace.fusion.ui.profile.data.RegionsObservable
import com.androidspace.fusion.utils.downloader.BinaryFileDownloader
import com.androidspace.fusion.utils.downloader.BinaryFileWriter
import com.androidspace.fusion.utils.downloader.ProgressCallback
import com.androidspace.fusion.utils.findFileName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.io.File


class OfflineMapListViewModel(override val app: Application) : BaseViewModel<Object>(app) {

    private val TAG = OfflineMapListViewModel::class.java.simpleName
    var onDownload: OnDownload? = null
    val regions = RegionsObservable(ArrayList<RegionData>())

    override fun onBackPressed() {
        super.onBackPressed()
        navController?.navigate(R.id.navigation_profile)
    }

    /*fun loadadminRegions(){
        getRegionsUseCase.execute(viewModelScope, Unit,
            success = {
                val res = it
                for(i in 0..res.size-1){
                    val item = res.get(i)
                    if(!TextUtils.isEmpty(item.url)){
                        val fileName = ""+app.getExternalFilesDir(null)+File.separator+Uri.parse(item.url).findFileName()!!
                        fileName?.let {
                            val f = ""+app.getExternalFilesDir(null)+File.separator+Uri.parse(item.url).findFileName()!!
                            item.loaded = File(f).exists() && File(f).isDirectory
                            Log.d(TAG, it)
                        }
                    }
                }
                regions.regions = res

            },
            fail = {
                error.postValue(it)
            })
    }*/

    fun loadRegions(){
        val arr = ArrayList<RegionData>()
        val dirData = app.getExternalFilesDir(null)
        dirData?.let {
            if(it.exists() && it.isDirectory){
                val list = it.listFiles()
                list?.let {
                    for(d in it){
                        if(d.isDirectory){
                            arr.add(RegionData((arr.size+1).toLong(), d.name, loaded = true))
                        }
                    }
                }
            }
            regions.regions = arr
        }

    }

    fun onRegionDelete(data: RegionData){
        val fileName = ""+app.getExternalFilesDir(null)+File.separator+data.name//Uri.parse(data.url).findFileName()!!
        val f = File(fileName) //app.getFileStreamPath(fileName);
        Log.d(TAG, f.absolutePath)
        val r = f.deleteRecursively() //app.deleteFile(fileName)
        Log.d(TAG, "Deleted:"+r)
        val d = regions.regions.find { it.id == data.id }
        d?.let {
            (regions.regions as ArrayList<RegionData>).remove(it)
            regions.update()
        }
    }
    fun onRegion(data: RegionData){
        if(!data.loaded) {
            onDownload?.onDownloadFile(data)
        }else{
            navController?.clearBackStack(R.id.mobile_navigation)
            navController?.navigate(R.id.navigation_roads, Bundle().apply {
                putParcelable("mmpk", data)
            })
        }
    }

    fun onDownloadMMPK(url: String){
        val ioScope = CoroutineScope(Dispatchers.IO)
        ioScope.launch {
            downloadMMPK(url)
        }
    }


    suspend fun downloadMMPK(url: String?): Long {
        url?.let {
            val filename = ""+app.getExternalFilesDir(null)+File.separator+Uri.parse(it).findFileName()!!
            Log.d(TAG, filename)
            //val outStream = app.openFileOutput(filename, Context.MODE_PRIVATE)
            val outStream = File(filename).outputStream()
            val mmpkWriter = BinaryFileWriter(outStream, object: ProgressCallback {
                override fun onProgress(progress: Double) {
                    Log.d(TAG, ""+progress)
                    if(progress >=100){
                        val item = regions.regions.find { it.url == url }
                        item?.let {
                            it.loaded = true
                            regions.update()
                        }
                    }
                }
            })
            val client = OkHttpClient()
            val down = BinaryFileDownloader(client, mmpkWriter)
            down.download(it)
        }
        return 0
    }

    fun onNewRegion(view: View){
        navController?.navigate(R.id.offlineMaps)
    }

}