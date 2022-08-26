package com.androidspace.fusion.data.model.adapters

import com.androidspace.fusion.data.model.URLString
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class URLStringAdapter: JsonDeserializer<URLString> {
    override fun deserialize(json: JsonElement?,typeOfT: Type?,context: JsonDeserializationContext?): URLString? {
        var res = ""
        json?.let {
            val url = ""
            val jsonURL = it.asJsonObject.get("url")
            jsonURL?.let {
                if(!it.isJsonNull){
                    res = it.asString
                }
            }

        }
        return URLString(res)
    }
}