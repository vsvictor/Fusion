package com.androidspace.fusion.data.api

import com.androidspace.fusion.base.errors.ErrorBody
import com.androidspace.fusion.base.errors.ExtException
import com.androidspace.fusion.data.model.TokenResponseData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Response


class ApiHelperImpl(private val apiService: ApiInterface) : ApiHelper {
    private val TAG = ApiHelperImpl::class.java.simpleName
    private val gson by inject(Gson::class.java)

    override suspend fun refresh(): TokenResponseData? = apiService.refresh().run{try{handleError(this)}catch (ex: Throwable){throw ex}}

    private fun <T> handleError(res: Response<T>):T{
        val code = res.code()
        if(code >= 200 && code < 300) {
            return res.body()!!
        }
        else{
            throw parse(code, res.errorBody())
        }
    }
    private fun parse(code: Int, response: ResponseBody?): ExtException {
        response?.let {
            val docsType = object : TypeToken<ErrorBody>() {}.type
            val body  = gson.fromJson<ErrorBody>(response.string(), docsType)
            return ExtException(code, body)
        }?: kotlin.run {
            return ExtException(-1, ErrorBody("Undefined error"))
        }
    }
}