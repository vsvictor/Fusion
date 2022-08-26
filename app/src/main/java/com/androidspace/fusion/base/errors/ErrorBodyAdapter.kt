package com.androidspace.fusion.base.errors

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class ErrorBodyAdapter : JsonDeserializer<ErrorBody> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext? ): ErrorBody {
        val message = json?.asJsonObject?.get("error")?.asString
        var mess = ""
        message?.let {
           mess = it
        }?: kotlin.run {
            val detail = json?.asJsonObject?.get("detail")?.asString
            detail?.let {
                mess = it
            }?: kotlin.run {
                mess = "Undefined error"
            }
        }
        var result = ErrorBody(mess)
/*
        val errs = json?.asJsonObject?.get("errors")?.asJsonObject

        errs?.let {
            for(key in errs?.keySet()!!){
                val text = errs.get(key)?.asString!!
                val p = Pair<String, String>(key, text)
                (result.errs as ArrayList<Pair<String, String>>).add(p)
            }
        }
*/

/*        val jsonData = json?.asJsonObject?.get("data")?.asJsonObject
        jsonData?.let {
            if(!it.isJsonNull){
                for(key in it.keySet()){
                    when(key){
                        "skill_id" -> {
                            val id = it.get(key).asLong
                            result.data = DataError(key, id)
                        }
                    }
                }
            }
        }*/
        return  result
    }
}