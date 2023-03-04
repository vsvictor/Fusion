package com.androidspace.fusion.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.androidspace.fusion.MainActivity
import com.androidspace.fusion.R
import com.arcgismaps.data.FeatureTable
import com.arcgismaps.data.ServiceFeatureTable
import com.arcgismaps.geotriggers.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*


class ZoneService: Service() {
    private val TAG = ZoneService::class.java.simpleName
    private val CHANNEL_ID = "ZoneService"
    private val binder: IBinder = LocalBinder()
    private val serviceURL = "https://main.infotech.guru/server/rest/services/Mine/MineDSNS_RO/FeatureServer/0"
    private val ioScope = CoroutineScope(Dispatchers.Default)
    private var counter = 0
    var timer = Timer()
    var monitor: GeotriggerMonitor? = null
    companion object {
        var isStarted:Boolean = false
        fun startZoneService(context: Context, message: String) {
            if(!isStarted) {
                isStarted = true
                val startIntent = Intent(context, ZoneService::class.java)
                startIntent.putExtra("inputExtra", message)
                ContextCompat.startForegroundService(context, startIntent)
            }
        }
        fun stopZoneService(context: Context) {
            val stopIntent = Intent(context, ZoneService::class.java)
            context.stopService(stopIntent)
            isStarted = false
        }
        var feed: LocationGeotriggerFeed? = null
        //var tab: FeatureTable? = null
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do heavy work on a background thread
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(input)
            .setSmallIcon(R.drawable.ic_service)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        //isStarted = true
        //ioScope.launch {
            startAll()
       // }
        return START_NOT_STICKY
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "ZoneService Service Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
    fun startAll() {
        val tab = ServiceFeatureTable(serviceURL).apply {
            //credential = UserCredential("viktor.dzhurliak", "Vestern74");
            //featureRequestMode = ServiceFeatureTable.FeatureRequestMode.ON_INTERACTION_NO_CACHE
        }

/*        Log.d(TAG, "Location status: "+feed?.locationDataSource?.status?.name)
        feed?.locationDataSource?.addStatusChangedListener {
            Log.d(TAG, "Location source: "+ it.status.name)
            if(it.status == LocationDataSource.Status.STARTED){
                monitor?.let {
                    it.startAsync()
                }?: kotlin.run {
                    Log.d(TAG, "Monitor not inited")
                }
            }
        }
        feed?.locationDataSource?.addErrorChangedListener {
            Log.d(TAG, "Location error:"+it.error.message)
        }
        feed?.locationDataSource?.addLocationChangedListener {
            //Log.d(TAG, it.location.position.asString())
        }
        tab.addLoadStatusChangedListener {
            Log.d(TAG, "Table:"+it.newLoadStatus.name+", Mode:"+tab.featureRequestMode.name)
        }*/
        startMonitor(tab!!)
        startTimer()
    }
    private fun startTimer(){
        timer = Timer()
        timer.schedule(object: TimerTask(){
            override fun run() {
                if(counter > 0){
                    vibro()
                }
            }
        },0,10000)
    }
    private fun startMonitor(table: FeatureTable){
        val fence = FeatureFenceParameters(table)
        val trigger = FenceGeotrigger((feed as GeotriggerFeed), FenceRuleType.EnterOrExit, fence)
        monitor = GeotriggerMonitor(trigger)
/*        monitor?.addGeotriggerMonitorWarningChangedEventListener {
            Log.d(TAG, "Warning: "+it.warning.message)
            it.warning.printStackTrace()
        }
        monitor?.addGeotriggerMonitorStatusChangedEventListener {
            Log.d(TAG, "Monitor status: "+it.status.name)
        }
        monitor?.addGeotriggerMonitorNotificationEventListener {
            Log.d(TAG, "Event")
            handleGeotriggerNotification(it.geotriggerNotificationInfo)
        }
        if(feed?.locationDataSource?.status == LocationDataSource.Status.STARTED){
            monitor?.startAsync()
        }else if(feed?.locationDataSource?.status == LocationDataSource.Status.STOPPED){
            feed?.locationDataSource?.startAsync()
        }*/

    }
    private fun handleGeotriggerNotification(geotriggerNotificationInfo: GeotriggerNotificationInfo) {
        val fenceGeotriggerNotificationInfo = geotriggerNotificationInfo as FenceGeotriggerNotificationInfo
        val fenceFeatureName = fenceGeotriggerNotificationInfo.message
        if (fenceGeotriggerNotificationInfo.fenceNotificationType == FenceNotificationType.Entered) {
            Log.d(TAG, "-----------------> Entered")
            counter ++
        } else if (fenceGeotriggerNotificationInfo.fenceNotificationType == FenceNotificationType.Exited) {
            Log.d(TAG, "Exited ----------------->")
            counter--
        }
        if(counter > 0){
            vibro()
        }
        //sendNotification(fenceGeotriggerNotificationInfo)
    }
    private fun vibro() {
        val v: Vibrator? = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v?.vibrate(500)
        }
    }
    override fun onBind(intent: Intent): IBinder {
/*        if(isStarted){
            stopChecker()
        }*/
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
/*        startChecker(getString(R.string.app_name))*/
        return super.onUnbind(intent)
    }

    internal inner class LocalBinder : Binder() {
        fun getService(): ZoneService = this@ZoneService
    }


}