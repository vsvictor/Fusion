package com.androidspace.fusion.data.model.camera

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class ImageData(val id: Long, val uri: Uri = Uri.EMPTY, val name: String = "", val rotate: Int = 0, val type: MediaTypeData = MediaTypeData.PHOTO, val created: Date = Date(), val timing: Long = 0, var selected: Boolean = false) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readParcelable(Uri::class.java.classLoader)?: Uri.EMPTY,
        parcel.readString()?:"",
        parcel.readInt(),
        if(parcel.readInt() == 0) MediaTypeData.PHOTO else MediaTypeData.VIDEO,
        Date(parcel.readLong()),
        parcel.readLong(),
        parcel.readBoolean()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(uri, flags)
        parcel.writeString(name)
        parcel.writeInt(rotate)
        parcel.writeInt(if(type == MediaTypeData.PHOTO) 0 else 1)
        parcel.writeLong(created.time)
        parcel.writeLong(timing)
        parcel.writeBoolean(selected)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageData> {
        override fun createFromParcel(parcel: Parcel): ImageData {
            return ImageData(parcel)
        }

        override fun newArray(size: Int): Array<ImageData?> {
            return arrayOfNulls(size)
        }
    }
}