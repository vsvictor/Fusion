package com.androidspace.fusion

import android.app.Application
import com.androidspace.fusion.di.appModule
import com.androidspace.fusion.di.domainModule
import com.androidspace.fusion.di.mainModule
import com.androidspace.fusion.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FusionApp: Application() {
    private val TAG = FusionApp::class.java.simpleName
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FusionApp)
            modules(
                listOf(
                    appModule,
                    repositoryModule,
                    domainModule,
                    mainModule
                )
            )
        }
    }
}