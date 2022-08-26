package com.androidspace.fusion.ui.splash

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.domain.usecase.UseCaseFirstStart
import com.androidspace.fusion.domain.usecase.UseCaseTokens

class SplashViewModel(override val app: Application,
                      private val useCaseFirstStart: UseCaseFirstStart,
                      private val useCaseTokens: UseCaseTokens) : BaseViewModel<Object>(app) {

    var next = -1 //R.id.mapFragment
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        //useCaseFirstStart.firstStart = true
    }

    fun startApp(){
        if(useCaseTokens.isLoggined){
            navController?.navigate(next)
        }else{
            if(useCaseFirstStart.firstStart) {
                //navController?.navigate(R.id.onboardFragment)
            }else {
                navController?.navigate(next)
            }
        }
    }
}