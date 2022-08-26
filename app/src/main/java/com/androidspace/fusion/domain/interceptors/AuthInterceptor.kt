package com.androidspace.fusion.domain.interceptors

import android.util.Log
import com.androidspace.fusion.data.local.SettingsSharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sh: SettingsSharedPreferences): Interceptor {
    private val TAG = AuthInterceptor::class.java.simpleName
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d(TAG, TAG)
        val access = sh.accessToken
        val refresh = sh.refreshToken
        //val private = sh.privateToken
        val req = chain.request()
        val isRefresh = req.url.toString().contains("refresh-token")
        if(isRefresh){
            val newReq = req.newBuilder().header("Refresh-token", refresh).build();
            val res = chain.proceed(newReq)
            return res
        }else{
            val auth = "Token "+access
            Log.d("AUTH", auth)
            val newReq = req.newBuilder().header("authorization", auth).build();
            return chain.proceed(newReq)
        }
    }
}
