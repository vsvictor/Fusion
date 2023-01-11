package com.androidspace.fusion.ui.navigation

import android.app.Application
import android.os.Handler
import android.util.Log
import androidx.lifecycle.ViewModel
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.ui.route.data.OnMap
import com.esri.arcgisruntime.data.TileCache
import com.esri.arcgisruntime.data.VectorTileCache
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import java.io.File
import java.io.FileFilter

class NavigationViewModel(override val app: Application) : BaseViewModel<Object>(app) {
    private val TAG = NavigationViewModel::class.java.simpleName
    var onMap: OnMap? = null
    val baseMap = Basemap()
    lateinit var iterator: Iterator<File>
    fun loadLocalMap() {
        val mapDir = app.getExternalFilesDir(null)
        val arr = mapDir?.listFiles(FileFilter { it.name.contains(".vtpk") })
        arr?.let {
            iterator = it.iterator()
            if (iterator.hasNext()) {
                val first = iterator.next()
                loadFromFile(first)
            }
        }
    }

    fun loadFromFile(file: File) {
        val fileName = file.absolutePath
        Log.d(TAG, fileName)
        val tiles = VectorTileCache(fileName)
        tiles.addDoneLoadingListener {
            val newTiledLayer = ArcGISVectorTiledLayer(tiles)
            newTiledLayer.isVisible = true
            baseMap.baseLayers.add(newTiledLayer)
            if (iterator.hasNext()) {
                loadFromFile(iterator.next())
            } else {
                val map = ArcGISMap(baseMap)
                onMap?.onMapLoaded(map)
            }
        }
        tiles.loadAsync()
    }
}