package com.androidspace.fusion.base

import android.app.Application
import android.content.Intent
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.androidspace.fusion.base.errors.ErrorData
import com.androidspace.fusion.base.interfaces.OnExit
import com.androidspace.fusion.base.interfaces.OnInitViewModel
import com.androidspace.fusion.base.interfaces.OnLoader
import com.androidspace.fusion.base.interfaces.OnMessage
import java.lang.reflect.ParameterizedType


abstract class BaseViewModel<DT>(open val app: Application) : AndroidViewModel(app), LifecycleObserver{
    private val vmClass = (javaClass.genericSuperclass as ParameterizedType?)!!.actualTypeArguments[0] as Class<DT>

    val error = MutableLiveData<ErrorData>()
    protected var loader: OnLoader? = null
    var onInitViewModel: OnInitViewModel? = null
    var onExit: OnExit? = null
    var onMessage: OnMessage? = null
    protected open var navController: NavController? = null
    protected lateinit var lifecycleOwner: LifecycleOwner

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreate(owner: LifecycleOwner){
        lifecycleOwner = owner
        onInitViewModel?.onInitViewModel()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume(){

    }
    open fun load(inputIntent: Intent){
    }

    fun clearError(){
        error.postValue(null)
    }

    fun initNavController(nav : NavController? = null){
        this.navController = nav
    }
    open fun onBackPressed(){}

    fun setOnLoaderListener(listener: OnLoader){
        this.loader = listener
    }
    fun onCloseActivity(view: View){
        onExit?.onCloseActivity()
    }

    open fun clear(){}

    fun onBackClick(view: View) {
        onBackPressed()
    }
}