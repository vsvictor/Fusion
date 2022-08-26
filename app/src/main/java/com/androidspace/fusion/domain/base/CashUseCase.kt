package com.androidspace.fusion.domain.base

import com.androidspace.fusion.base.errors.ErrorBody
import com.androidspace.fusion.base.errors.ErrorData
import com.androidspace.fusion.base.errors.ExtException
import com.androidspace.fusion.domain.BaseUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class CashUseCase<T, P>: BaseUseCase<T, P>() {
    abstract suspend fun load(coroutineScope: CoroutineScope, vararg args: Any): T?
    abstract suspend fun save(coroutineScope: CoroutineScope, data: P)
    override fun execute(coroutineScope: CoroutineScope, params: P, success: (T) -> Unit, fail:(ErrorData) -> Unit) {
        loader?.show(true)
        scope = coroutineScope
        job = coroutineScope.launch(launchDispatcher) {
            try {
                val res= onLaunch(params)
                res?.let {
                    withContext(Dispatchers.Main) {
                        loader?.show(false)
                        save(coroutineScope, params)
                        success.invoke(it)
                    }
                }?: kotlin.run {
                    loader?.show(false)
                    save(coroutineScope, params)
                    val cash = load(coroutineScope)
                    cash?.let {
                        withContext(Dispatchers.Main){
                            success?.invoke(it)
                        }
                    }?: kotlin.run {
                        throw ExtException(-1, ErrorBody("Undefined error"))
                    }
                }
            }catch (ex: Throwable){
                save(coroutineScope, params)
                val cash = load(coroutineScope)
                cash?.let {
                    withContext(Dispatchers.Main){
                        loader?.show(false)
                        success?.invoke(it)
                    }
                }?: kotlin.run {
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
    }
}