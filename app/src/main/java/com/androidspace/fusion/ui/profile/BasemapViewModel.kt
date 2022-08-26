package com.androidspace.fusion.ui.profile

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import com.androidspace.fusion.BuildConfig
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.data.model.MapData
import com.androidspace.fusion.domain.usecase.UseCaseBasemap
import com.androidspace.fusion.ui.common.OnProgress
import com.androidspace.fusion.ui.profile.data.MapObservable
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalFolder
import com.esri.arcgisruntime.portal.PortalItem
import com.esri.arcgisruntime.security.UserCredential
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class BasemapViewModel(override val app: Application, private val useCaseBasemap: UseCaseBasemap) : BaseViewModel<Object>(app) {
    private val TAG = BasemapViewModel::class.java.simpleName
    var onProgress: OnProgress? = null
    var portal: Portal = Portal(BuildConfig.PORTAL_URL, false)
    val maps = MapObservable(ArrayList<MapData>())
    var baseMapFolder: PortalFolder? = null
    override fun onBackPressed() {
        super.onBackPressed()
        navController?.navigate(R.id.navigation_profile)
    }
    fun loadMaps(){
        onProgress?.onProgerssVisible(true)
        val list = ArrayList<MapData>()
        portal.addDoneLoadingListener {
            loadData(0, list)
        }
        portal.loadAsync()
    }
    fun loadData(id: Int, list: ArrayList<MapData>){
        val i = id+1
        val item = PortalItem(portal,mapIDList.get(id))
        item.addDoneLoadingListener {
            val bytes = item.fetchThumbnailAsync().get()
            val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            Log.d(TAG, item.title+", ID:"+id+", filename:"+item.thumbnailFileName)
            val map = MapData(id, item.title, item.itemId, bitmap)
            list.add(map)
            if(i<mapIDList.size) {
                loadData(i, list)
            }else{
                onProgress?.onProgerssVisible(false)
                maps.maps = list
            }
        }
        item.loadAsync()
    }
    fun onMap(data: MapData){
        useCaseBasemap.basemap = data.itemID
        navController?.navigate(R.id.navigation_roads, Bundle().apply {
            putParcelable("basemap", data)
        })
    }
    override fun clear(){
        maps.maps = ArrayList<MapData>()
    }
    val mapIDList = arrayListOf<String>("99acd9ee33634d23b5dc441bce53bb47", "a8c0f17299e7499e851ff9bf30f87c5d", "ad4491af079746f1b173c407bc4c29c3",
    "eee73ee6c03a45888c50275c29ab816c","7b8432157b03412bb9fef262f8deb134", "9a49739b90e24d15a7992c0888b635ca",
    "35a648a10bf0413b96aef534577fc60d","c29ba68a05e84cc3915f18871d6240c5","8caf57cf89f54dcb89c19e03defa82eb",
    "18d8d06b282048e8b65f74219b20d1dc","042b0af1e69d4e74840b787d18f3c41e","b0a51a9cc88d45e9b82ebe14746734ae",
    "32a54d0244ba42329cf73f4c61bb0ddc","1b75a459f4d343bfad2d564da7d3c4b9","da3fbdf233314dccb00b9d346e921be7",
    "b6bc5f40d5d944d1986f0f0aadfb5cb7","a0e5b15330e44e7da671989fceb38ec8","2b303db00a85476a82a2aa3f35a91642",
    "3a11f1bcc9cb4e768e54b6e5ad8191f9","3361b5d3b8d445d89fbd02f1bf28d69f","6c484c16c93c4bdc87feaa5c6b56dcc3", "01c27c8d5f6e4570a361fb8e761e0729")
}