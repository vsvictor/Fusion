package com.androidspace.fusion.domain

import com.androidspace.fusion.base.errors.ErrorBody
import com.androidspace.fusion.base.errors.ErrorData
import com.androidspace.fusion.base.errors.ExtException
import com.androidspace.fusion.base.interfaces.OnLoader
import kotlinx.coroutines.*

abstract class BaseUseCase<T, P> : AbstractUseCase() {

    protected abstract suspend fun onLaunch(params: P): T?
    protected var loader: OnLoader? = null

    fun setOnLoaderListener(listener: OnLoader?){
        this.loader = listener
    }

    open fun execute(coroutineScope: CoroutineScope, params: P, success: (T) -> Unit, fail:(ErrorData) -> Unit) {
        loader?.show(true)
        scope = coroutineScope
        job = coroutineScope.launch(launchDispatcher) {
            try {
                val res= onLaunch(params)
                res?.let {
                    withContext(Dispatchers.Main) {
                        loader?.show(false)
                        success.invoke(it)
                    }
                }?: kotlin.run {
                    loader?.show(false)
                    throw ExtException(-1, ErrorBody("Undefined error"))
                }
            }catch (ex: Throwable){
                var err = ErrorData()
                if(ex is ExtException) err = ErrorData(ex.code, ex.err)
                else ex.message?.let {
                    err = ErrorData(-1, ErrorBody(it))
                }
                withContext(Dispatchers.Main) {
                    loader?.show(false)
                    fail.invoke(err)
                }
            }
        }
    }
    open suspend fun load(params: P):T?{
        loader?.show(true)
        val res = onLaunch(params)
        loader?.show(false)
        res?.let {
            return it
        }?: kotlin.run {
            return null
        }
    }
}