package com.androidspace.fusion.data.model.adapters

import com.androidspace.fusion.utils.asDate
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.util.*

class DateAdapter: JsonDeserializer<Date> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date? {
        json?.let {
            val ks = it.asString
            val tt = ks.replace("T", " ").split(".")[0]
            try{
                //val dd = ks.asDate("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val dd = tt.asDate("yyyy-MM-dd HH:mm:ss")
                return dd
            }catch (ex: Exception){
                return null
            }
        }?: kotlin.run {
            return null
        }
    }
}