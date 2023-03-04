package com.androidspace.fusion.ui.maps

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.androidspace.fusion.BuildConfig
import com.androidspace.fusion.Constants
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.base.errors.ErrorBody
import com.androidspace.fusion.base.errors.ErrorData
import com.androidspace.fusion.ui.route.data.OnMap
import com.arcgismaps.data.*
import com.arcgismaps.geometry.GeometryEngine
import com.arcgismaps.geometry.GeometryType
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.layers.ArcGISMapImageLayer
import com.arcgismaps.mapping.layers.ArcGISVectorTiledLayer
import com.arcgismaps.mapping.layers.FeatureLayer
import com.arcgismaps.mapping.layers.Layer
import com.arcgismaps.mapping.layers.vectortiles.VectorTileCache
import com.arcgismaps.portal.PortalItem
import com.arcgismaps.tasks.exportvectortiles.ExportVectorTilesTask
import kotlinx.coroutines.launch
import java.io.File

class MapViewModel(override val app: Application) : BaseViewModel<Object>(app) {
    private val TAG = MapViewModel::class.java.simpleName
    var onMap: OnMap? = null
    private val map = ArcGISMap()
    //private val geodatabaseSyncTask: GeodatabaseSyncTask by lazy { GeodatabaseSyncTask(app.getString(R.string.wildfire_sync)) }
    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun createDatabase() = viewModelScope.launch {
        val geoDBFile =  File(app.getExternalFilesDir(null)?.absolutePath ?: "", File.separator+"artprt.geodatabase")
        if(geoDBFile.exists()){
            geoDBFile.delete()
        }
        val geoDB = Geodatabase.create(geoDBFile.absolutePath).onSuccess { db ->
            loadRegionsTable {
                it?.let {
                    viewModelScope.launch {
                        val tableDescription = TableDescription("Regions", SpatialReference(4326), GeometryType.Polygon)
                        tableDescription.apply {
                            hasAttachments = false
                            hasM = false
                            hasZ = false
                        }

                        val fDesks = ArrayList<FieldDescription>()
                        for(field in it.fields){
                            val fDesk = FieldDescription(field.name, field.fieldType)
                            Log.d(TAG, fDesk.name)
                            fDesks.add(fDesk)
                        }
                        tableDescription.fieldDescriptions.addAll(fDesks)
                        db.createTable(tableDescription).onSuccess { newTab ->
                            Log.d(TAG, "Regions table created")
                            loadFeatures(it, { list ->
                                list?.let { features ->
                                    viewModelScope.launch {
                                        newTab.addFeatures(features).onSuccess {
                                            Log.d(TAG, ""+features.count()+" features added")
                                        }.onFailure {
                                            Log.d(TAG, "No features...")
                                            it.printStackTrace()
                                        }
                                    }
                                }
                            })
                        }.onFailure {
                            Log.d(TAG, "Fail regions table create..."+", Message: "+it.message)
                            it.printStackTrace()
                        }
                    }
                }
            }
        }.onFailure {
            Log.d(TAG, "Faile geodatabase create...")
        }
    }

    fun loadRegionsTable(callback:(FeatureTable?)->Unit){
        viewModelScope.launch {
            val fileGeoPackage = File(app.getExternalFilesDir(null)?.absolutePath ?: "", File.separator+"UkrRegions.gpkg")
            Log.d(TAG, "File name:"+fileGeoPackage.absolutePath)
            val geoPackage = GeoPackage(fileGeoPackage.absolutePath)
            geoPackage.load().onSuccess {
                Log.d(TAG, "Loaded")
                val table = geoPackage.geoPackageFeatureTables.first()
                callback.invoke(table)
            }.onFailure {
                callback.invoke(null)
            }
        }
    }
    fun loadFeatures(table: FeatureTable, callback: (Iterable<Feature>?) -> Unit) = viewModelScope.launch {
        val param = QueryParameters()
        param.whereClause = "1=1"
        param.outSpatialReference = SpatialReference(4326)
        param.returnGeometry = true
        table.queryFeatures(param).onSuccess {
            callback.invoke(it)
        }.onFailure {
            Log.d(TAG, "Can't load features...")
            callback.invoke(null)
        }
    }

    fun loadMap(){
        viewModelScope.launch {
            loadRegionsTable {
                it?.let {
                    val layer = FeatureLayer(it)
                    map.basemap.value?.baseLayers?.add(layer)
                    loadTiles()
                    onMap?.onMapLoaded(map, layer)
                }
            }
        }
    }
    fun loadTiles(){
        val dirTiles = File(app.getExternalFilesDir(null)?.absolutePath+File.separator+"tiles")
        viewModelScope.launch {
            dirTiles.list()?.let {
                it.forEach {
                    val curr = File(dirTiles.absolutePath+File.separator+it+File.separator+"tiles.vtpk")
                    val vLayer = ArcGISVectorTiledLayer(curr.absolutePath)
                    vLayer.load().onSuccess {
                        map.basemap.value?.baseLayers?.add(vLayer)
                    }.onFailure {
                        Log.d(TAG, "Fail load tiles")
                    }
                }
            }
        }

    }
    fun exportTiles(feature: Feature, scale: Double, onSuccess:(VectorTileCache?, String?)->Unit, onFail:(ErrorData)->Unit){
        viewModelScope.launch {
            val donor = PortalItem(Constants.portal, BuildConfig.DONOR_MAP_ID)
            val mapDonor = ArcGISMap(donor)
            mapDonor.load().onSuccess {
                Log.d(TAG, "Map URL:"+mapDonor.uri+", minSacle:"+mapDonor.minScale+", maxScale:"+mapDonor.maxScale)
                val vectorTiledLayer = mapDonor.basemap.value?.baseLayers?.get(0) as ArcGISVectorTiledLayer
                for(l in mapDonor.basemap.value!!.baseLayers){
                    Log.d(TAG, "Basemap layers:"+l.name)
                }
                internalExportTiles(vectorTiledLayer.uri.toString(), feature, scale, onSuccess, onFail)

            }.onFailure {
                Log.d(TAG, "Not loaded")
            }

        }
    }
    private fun internalExportTiles(url: String, feature: Feature, scale: Double, onSuccess:(VectorTileCache?, String?)->Unit, onFail:(ErrorData)->Unit){
        viewModelScope.launch {
            var exportDirectory:File
            val tilesDirectory = File(app.getExternalFilesDir(null)?.absolutePath, File.separator+"tiles")
            if(!tilesDirectory.exists()){
                tilesDirectory.mkdir()
            }
            exportDirectory = File(tilesDirectory.absolutePath, File.separator+feature.attributes.get("ADM1_EN"))
            if(!exportDirectory.exists()){
                exportDirectory.mkdir()
            }
            Log.d(TAG, "File:"+exportDirectory.absolutePath)
            feature.geometry?.let { area ->
                val deviat  = 200.0
                val geoFirst = GeometryEngine.generalize(area, deviat, true)
                val geo = GeometryEngine.simplify(geoFirst)
                val exportTilesTask = ExportVectorTilesTask(url)
                val paramResult = exportTilesTask.createDefaultExportVectorTilesParameters(geo, scale)
                val param = paramResult.getOrElse {
                    it.message?.let {
                        onMessage?.onMessage(it)
                        Log.d(TAG, "Parameters:"+it)
                    }
                    return@launch
                }
                val tilesFile = File(exportDirectory.absolutePath, File.separator+"tiles.vtpk")
                val styleDirectory = File(exportDirectory.absolutePath, File.separator+"style")
                val job = exportTilesTask.exportVectorTiles(param, tilesFile.absolutePath, styleDirectory.absolutePath).apply {
                    start()
                }
                with(viewModelScope){
                    launch {
                        job.progress.collect{
                            val progress = job.progress
                            Log.d(TAG, "Progress:"+progress.value.toString())
                        }
                    }
                    launch {
                        job.messages.collect{
                            Log.d(TAG, "Message:"+it.message)
                        }
                    }
                    job.result().onSuccess {
                        onSuccess.invoke(it.vectorTileCache, exportDirectory.absolutePath+File.separator+"style")
                    }.onFailure {
                        val body = ErrorBody("Fail load tiles")
                        var err = ErrorData(-2, body)
                        Log.d(TAG, err.body.message)
                        Log.d(TAG, ""+it.message)
                        exportDirectory.deleteRecursively()
                        onFail.invoke(err)
                    }
                }
            }
        }
    }

    fun loadTraffic(): Layer?{
        val trafficLayer = ArcGISMapImageLayer("https://traffic.arcgis.com/arcgis/rest/services/World/Traffic/MapServer")
        return  trafficLayer
    }

}