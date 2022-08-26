package com.androidspace.fusion.domain.interceptors

import android.util.Log
import com.androidspace.fusion.data.local.SettingsSharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class LanguageInterceptor(private val sh: SettingsSharedPreferences): Interceptor {
    private val TAG = LanguageInterceptor::class.java.simpleName
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        //val newReq = req.newBuilder().header("Content-Language", sh.language).build()
        val newReq = req.newBuilder().header("Content-Language", "ua").build()
        return chain.proceed(newReq)
    }
}
