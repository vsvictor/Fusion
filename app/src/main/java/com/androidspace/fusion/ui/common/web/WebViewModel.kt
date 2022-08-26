package com.androidspace.fusion.ui.common.web

import android.app.Application
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.data.model.URLString
import com.androidspace.fusion.ui.common.OnLoadURL

class WebViewModel(override val app: Application) : BaseViewModel<Object>(app), OnWebEvent{
    private val TAG = WebViewModel::class.java.simpleName
    var back = -1
    private var _url: String? = null
    var url get() = _url; set(value) {
        _url = value
        _url?.let {
            onLoadURL?.onLoadURL(URLString(it))
        }
    }
    var onLoadURL: OnLoadURL? = null
    var loaded = false
    override fun onBackPressed() {
        super.onBackPressed()
        if(back > 0) {
            navController?.navigate(back)
        }
    }

    override fun onPageLoaded() {
        loaded = true
    }
}