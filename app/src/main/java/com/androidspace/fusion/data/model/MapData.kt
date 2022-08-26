package com.androidspace.fusion.data.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class MapData(val id: Int, val name: String, val itemID: String, var thumb: Bitmap? = null) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readParcelable(Bitmap::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(itemID)
        parcel.writeParcelable(thumb, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MapData> {
        override fun createFromParcel(parcel: Parcel): MapData {
            return MapData(parcel)
        }

        override fun newArray(size: Int): Array<MapData?> {
            return arrayOfNulls(size)
        }
    }
}