package com.androidspace.fusion.data.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class MediaData(
    @SerializedName("id")
    val id: Long = 1,
    @SerializedName("url")
    var url: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("size")
    val size: Long = 0,
    @SerializedName("type")
    var type: String = "",
    var timing: Long = 0,
    var bitmap: Bitmap? = null,
    var md5: String? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readLong(),
        parcel.readString()?:"",
        parcel.readLong()

    ) {
    }

    var thumb get() = url; set(value) {
        url = value
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(url)
        parcel.writeString(name)
        parcel.writeLong(size)
        parcel.writeString(type)
        parcel.writeLong(timing)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MediaData> {
        override fun createFromParcel(parcel: Parcel): MediaData {
            return MediaData(parcel)
        }

        override fun newArray(size: Int): Array<MediaData?> {
            return arrayOfNulls(size)
        }
    }
}