package com.androidspace.fusion.domain.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.HttpUrl




class ChangeAttrInterceptor(): Interceptor {
    private val TAG = ChangeAttrInterceptor::class.java.simpleName
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d(TAG, TAG)
        val req = chain.request()
        val taskURL = req.url.toString().contains("/api/v1/public/change_user_attr/?change_att")
        if(taskURL){
            val first = req.url.queryParameter("change_attr")
            val second = req.url.queryParameter("attr_value")
            val url: HttpUrl = chain.request().url
                .newBuilder()
                .removeAllQueryParameters("attr_value")
                .addQueryParameter(first!!, second)
                .build()
            val newReq = req.newBuilder().url(url).build()
            return chain.proceed(newReq)
        }else{
            //val newReq = req.newBuilder().header("Content-Language", sh.language).build()
            val newReq = req.newBuilder().header("Content-Language", "ua").build()
            return chain.proceed(newReq)
        }
    }
}
