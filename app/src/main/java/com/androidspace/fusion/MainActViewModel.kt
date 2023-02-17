package com.androidspace.fusion

import android.app.Application
import android.util.Log
import com.androidspace.fusion.base.BaseViewModel
import com.arcgismaps.portal.Portal

//import com.esri.arcgisruntime.portal.Portal

class MainActViewModel(override val app: Application): BaseViewModel<Any>(app) {
    private val TAG = MainActViewModel::class.java.simpleName
/*    private var _portal: Portal? = null
    var portal get() = _portal; set(value) {
        _portal = value
        _portal?.let {
            it.addCredentialChangedListener {
                Log.d(TAG, it.credential.toJson())
            }
            it.addDoneLoadingListener {
                navController?.navigate(R.id.navigation_profile)
            }
            it.loadAsync()
        }

    }*/
}