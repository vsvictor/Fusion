package com.androidspace.fusion.ui.profile

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.androidspace.fusion.Constants
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.data.model.RegionData
import com.androidspace.fusion.ui.route.data.OnMap
import com.androidspace.fusion.utils.findFileName
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.MobileMapPackage
import com.esri.arcgisruntime.mapping.view.MapView
import java.io.File

class RegionMapViewModel(override val app: Application) : BaseViewModel<Object>(app) {
    private val TAG = RegionMapViewModel::class.java.simpleName
    var region:  RegionData? = null
    var onMap: OnMap? = null
    override fun onBackPressed() {
        super.onBackPressed()
        navController?.navigate(R.id.regionListFragment)
    }

    fun loadMap(){
        region?.let {
            val i = 0
            val fileName = ""+app.getExternalFilesDir(null)+ File.separator+Uri.parse(it.url).findFileName()!!
            //val  fileName = it.url+".mmpk"
            Log.d(TAG, "File: "+fileName)
            val mmpk = MobileMapPackage(fileName)
            mmpk.addLoadStatusChangedListener() {
                if(mmpk.loadStatus == LoadStatus.LOADED){
                    Log.d(TAG, "Version:"+mmpk.version)
                    val mm  = mmpk.maps.get(0)
                    mm.apply {
                        addLoadStatusChangedListener {
                            Log.d(TAG, "Basemap:"+loadStatus.name)
                            if(loadStatus == LoadStatus.LOADED){
                                //mm.basemap = this
                                onMap?.onMapLoaded(mm)
                            }

                        }
                        loadAsync()
                    }
                    Log.d(TAG, "Maps count:"+mmpk.maps.size)
                }else if(mmpk.loadStatus == LoadStatus.FAILED_TO_LOAD) {
                    Log.d(TAG, mmpk.loadStatus.name+":"+mmpk.loadError)
                }
            }
            mmpk.loadAsync()
        }
    }
}