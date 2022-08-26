package com.androidspace.fusion.data.model.adapters

import android.text.TextUtils
import com.androidspace.fusion.data.model.RegionData
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class RegionAdapter: JsonDeserializer<RegionData> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): RegionData? {
        json?.let {
            var id: Long = 0
            val jsonID = it.asJsonObject.get("id")
            jsonID?.let {
                if(!it.isJsonNull){
                    id = it.asLong
                }
            }
            var name: String = ""
            val jsonName = it.asJsonObject.get("name")
            jsonName?.let {
                if(!it.isJsonNull){
                    name = it.asString
                }
            }
            var lat = 0.0
            val jsonLat = it.asJsonObject.get("lat")
            jsonLat?.let {
                if(!it.isJsonNull){
                    lat = it.asDouble
                }
            }
            var lng = 0.0
            val jsonLng = it.asJsonObject.get("lng")
            jsonLng?.let {
                if(!it.isJsonNull){
                    lng = it.asDouble
                }
            }
            var url: String? = null
            val jsonURL = it.asJsonObject.get("file_url")
            jsonURL?.let {
                if(!it.isJsonNull){
                    if(!TextUtils.isEmpty(it.asString)){
                        url = it.asString
                    }
                }
            }
            var size = "0 KB"
            val jsonSize = it.asJsonObject.get("size")
            jsonSize?.let {
                if(!it.isJsonNull){
                    if(!TextUtils.isEmpty(it.asString)) {
                        size = it.asString
                    }
                }
            }
            return RegionData(id, name, lat, lng, url, size, null, false)
        }
        return null
    }
}