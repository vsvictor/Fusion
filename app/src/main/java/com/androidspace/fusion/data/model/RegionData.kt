package com.androidspace.fusion.data.model

import android.os.Parcel
import android.os.Parcelable
import com.esri.arcgisruntime.geometry.Geometry
import com.google.gson.annotations.SerializedName

data class RegionData(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("lat")
    val lat: Double = 0.0,
    @SerializedName("lng")
    val lng: Double = 0.0,
    @SerializedName("file_url")
    val url: String? = null,
    @SerializedName("size")
    val size: String? = null,
    val geometry: Geometry? = null,
    var loaded: Boolean = false) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()?:"",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString()?:"",
        Geometry.fromJson(parcel.readString()),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeString(url)
        parcel.writeString(size)
        parcel.writeString(geometry?.toJson())
        parcel.writeByte(if (loaded) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RegionData> {
        override fun createFromParcel(parcel: Parcel): RegionData {
            return RegionData(parcel)
        }

        override fun newArray(size: Int): Array<RegionData?> {
            return arrayOfNulls(size)
        }
    }
}