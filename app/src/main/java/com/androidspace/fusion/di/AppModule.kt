package com.androidspace.fusion.di

import android.content.Context
import com.androidspace.fusion.BuildConfig
import com.androidspace.fusion.base.errors.ErrorBody
import com.androidspace.fusion.base.errors.ErrorBodyAdapter
import com.androidspace.fusion.data.api.ApiHelper
import com.androidspace.fusion.data.api.ApiHelperImpl
import com.androidspace.fusion.data.api.ApiInterface
import com.androidspace.fusion.data.local.SettingsSharedPreferences
import com.androidspace.fusion.data.model.URLString
import com.androidspace.fusion.data.model.adapters.DateAdapter
import com.androidspace.fusion.data.model.adapters.URLStringAdapter
import com.androidspace.fusion.domain.interceptors.AuthInterceptor
import com.androidspace.fusion.domain.interceptors.CustomHttpLoggingInterceptor
import com.androidspace.fusion.domain.interceptors.LanguageInterceptor
import com.androidspace.fusion.utils.NetworkHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

val appModule = module {
    single { provideOkHttpClient(get(), get()) }
    single { provideGSON() }
    single { provideRetrofit(get(), get(), BuildConfig.BASE_URL) }
    single { provideApiService(get()) }
    single { provideNetworkHelper(androidContext()) }
    single { getSharedPreferences(androidContext()) }

    single<ApiHelper> {
        return@single ApiHelperImpl(get())
    }
}

private fun getSharedPreferences(context: Context) = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

private fun provideNetworkHelper(context: Context) = NetworkHelper(context)


private fun provideOkHttpClient(sh: SettingsSharedPreferences, context: Context): OkHttpClient {
    //val apiKey = context.getString(R.string.api_key)
    val authInterceptor = AuthInterceptor(sh)
    val languageInterceptor = LanguageInterceptor(sh)
    if (BuildConfig.DEBUG) {
        val loggingInterceptor = CustomHttpLoggingInterceptor()
        loggingInterceptor.apply { loggingInterceptor.level = CustomHttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder()
            .cache(null)
            .readTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .connectTimeout(300, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(languageInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    } else return OkHttpClient
        .Builder()
        .cache(null)
        .readTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
        .connectTimeout(300, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .addInterceptor(languageInterceptor)
        .build()
}

private fun provideGSON(): Gson {
    return GsonBuilder()
        .registerTypeAdapter(ErrorBody::class.java, ErrorBodyAdapter())
        .registerTypeAdapter(URLString::class.java, URLStringAdapter())
        .registerTypeAdapter(Date::class.java, DateAdapter())
        .setLenient()
        .create()
}

private fun provideRetrofit(
    okHttpClient: OkHttpClient,
    gson: Gson,
    BASE_URL: String
): Retrofit =
    Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

private fun provideApiService(retrofit: Retrofit): ApiInterface =
    retrofit.create(ApiInterface::class.java)