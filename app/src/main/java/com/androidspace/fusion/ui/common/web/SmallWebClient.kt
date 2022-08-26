package com.androidspace.fusion.ui.common.web

import android.webkit.WebView
import android.webkit.WebViewClient

class SmallWebClient(val eventer: OnWebEvent?): WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        eventer?.onPageLoaded()
    }
}