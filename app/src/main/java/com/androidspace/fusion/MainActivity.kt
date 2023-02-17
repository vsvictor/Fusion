package com.androidspace.fusion

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.androidspace.fusion.base.BaseActivity
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.base.interfaces.OnLanguageChanged
import com.androidspace.fusion.data.local.SettingsSharedPreferences
import com.androidspace.fusion.databinding.ActivityMainBinding
import com.androidspace.fusion.ui.common.OnBottomBarVisible
import com.androidspace.fusion.utils.transparentStatusBar
import org.koin.java.KoinJavaComponent

@Layout(R.layout.activity_main)
class MainActivity : BaseActivity<ActivityMainBinding, MainActViewModel>(), OnBottomBarVisible, OnLanguageChanged {
    private val TAG = MainActivity::class.java.simpleName
    private var actionBarHeight = 0
    private var bottomHeight = 0
    private var currentID = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ArcGISRuntimeEnvironment.setLicense(getString(R.string.arcgis_license))
        //ArcGISRuntimeEnvironment.setApiKey(getString(R.string.api))
        //AuthenticationManager.setTrustAllSigners(true)


        transparentStatusBar()
        val tv = TypedValue()
        if (this.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(
                tv.data,
                resources.displayMetrics
            )
        }
        navController = findNavController(R.id.nav_host_fragment)
        binding.navView.setupWithNavController(navController)
        binding.navView.setOnItemSelectedListener {
            if(it.itemId != currentID) {
                currentID = it.itemId
                navController.navigate(currentID)
            }
            true
        }
        bottomHeight = binding.navView.layoutParams.height
        val sh by KoinJavaComponent.inject(SettingsSharedPreferences::class.java)
        val startFragment = sh.prefs.getInt("start_from", -1)
        if(startFragment > 0){
            sh.prefs.edit().remove("start_from").commit()
            navController.navigate(startFragment)
        }
        onDeep(intent)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        onDeep(intent)
    }

    override fun onStart() {
        super.onStart()
/*        Intent(this, ZoneService::class.java).also { intent ->
            bindService(intent, connector, Context.BIND_AUTO_CREATE)
        }*/
        //ZoneService.startService(this, "Zone service started...")
    }

    override fun onPause() {
        //ZoneService.stopService(this)
        super.onPause()
    }

    private fun onDeep(deepIntent: Intent?){
/*        deepIntent?.data?.let {
            if(it.host == getString(R.string.dev_host) || it.host == getString(R.string.prod_host)){
                Log.d(TAG, "From onNewIntent")
                navController.navigate(R.id.diaFragment, Bundle().apply {
                    putInt("back", R.id.contactDataFragment)
                    putBoolean("token", true)
                })
            }
        }*/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
    override fun getNavHostFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
    }
    override fun onBottomBarVisible(visible: Boolean) {
        try {
            binding.navView.isVisible = visible
        }catch (ex: UninitializedPropertyAccessException){
            ex.printStackTrace()
        }
    }
    override fun navigationBarHeight(): Int {
        return navigationBarHeight()
    }

/*    override fun onStartZoneChecker(locationDataSource: LocationDataSource) {
        Intent(this, ZoneService::class.java).also { intent ->
            bindService(intent, object: ServiceConnection{
                override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                    zoneservice = (binder as ZoneService.LocalBinder).getService()
                    //ZoneService.locSource = locationDataSource
                    zoneservice?.startAll(locationDataSource)
                }
                override fun onServiceDisconnected(name: ComponentName?) {
                }
            }, Context.BIND_AUTO_CREATE or Context.BIND_INCLUDE_CAPABILITIES)
        }
    }

    override fun onStopZoneChecker() {
        //unbindService(connector)
    }*/

/*    val connector = object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            zoneservice = (binder as ZoneService.LocalBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }*/

}