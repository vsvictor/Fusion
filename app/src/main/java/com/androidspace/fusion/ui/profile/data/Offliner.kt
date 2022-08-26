package com.androidspace.fusion.ui.profile.data

import android.util.Log
import com.esri.arcgisruntime.concurrent.Job
import com.esri.arcgisruntime.geometry.Geometry
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.MobileMapPackage
import com.esri.arcgisruntime.tasks.offlinemap.GenerateOfflineMapJob
import com.esri.arcgisruntime.tasks.offlinemap.GenerateOfflineMapParameterOverrides
import com.esri.arcgisruntime.tasks.offlinemap.GenerateOfflineMapParameters
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapTask
import java.io.File

class Offliner(val geo: Geometry, val map: ArcGISMap, val fileName: String, val miScale: Double, val maScale: Double) {
    private val TAG = Offliner::class.java.simpleName
    private lateinit var job: GenerateOfflineMapJob
    private var lastMessage: String = ""

    fun createMMPK(success: (result: MobileMapPackage) -> Unit, fail:(message: String)->Unit, progressListener:(prog: Int)->Unit){
        val generateOfflineMapParameters = GenerateOfflineMapParameters(geo, miScale, maScale).apply { isContinueOnErrors = true }
        val offlineMapTask = OfflineMapTask(map)
        job = offlineMapTask.generateOfflineMap(generateOfflineMapParameters, fileName)
        val p = job.apply {
            addProgressChangedListener {
                Log.d(TAG, "Progress:"+progress)
                progressListener.invoke(progress)
            }
            addJobMessageAddedListener {
                lastMessage = it.message.message
                Log.d(TAG, lastMessage+", source:"+it.message.source.name)

            }
            addJobDoneListener {
                Log.d(TAG, status.name)
                if (status == Job.Status.SUCCEEDED) {
                    val result = result
                    Log.d(TAG, "Success")
                    success.invoke(result.mobileMapPackage)
                } else if(status == Job.Status.FAILED){
                    fail.invoke(lastMessage)
                    Log.d(TAG, "Fail:"+lastMessage)
                }
            }
            start()
        }
    }
    fun createPreplaned(success: (result: MobileMapPackage) -> Unit, fail:(message: String)->Unit, progressListener:(prog: Int)->Unit){
        val offlineMapDirectory = File(fileName)
        offlineMapDirectory.also {
            when {
                it.mkdirs() -> Log.d(TAG, "Created directory for offline map in " + it.path)
                it.exists() -> Log.d(TAG, "Offline map directory already exists at " + it.path)
                else -> Log.e(TAG, "Error creating offline map directory at: " + it.path)
            }
        }
        val names: MutableList<String> = ArrayList()
        val offlineMapTask = OfflineMapTask(map)
        val prePlanned = offlineMapTask.preplannedMapAreasAsync
        prePlanned.addDoneListener {
            val areas = prePlanned.get().onEach { area ->
                names.add(area.portalItem.title)
                area.loadAsync()
                area.addDoneLoadingListener {

                }
            }
        }
    }
    fun cancel(){
       job.cancelAsync()
    }
}