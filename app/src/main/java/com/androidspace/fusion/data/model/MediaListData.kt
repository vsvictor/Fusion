package com.androidspace.fusion.data.model

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils

data class MediaListData(var list: List<MediaData>) : Parcelable {
    fun add(data: MediaData){
        (list as ArrayList<MediaData>).add(data)
    }
    fun clear(){
        (list as ArrayList<MediaData>).clear()
    }
    val last: Int get() {
        var res = -1
        for(item in list){
            if(!TextUtils.isEmpty(item.url)) res++
        }
        return res
    }
    constructor(parcel: Parcel) : this(parcel.createTypedArrayList(MediaData)?:ArrayList<MediaData>()) {
    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(list)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MediaListData> {
        override fun createFromParcel(parcel: Parcel): MediaListData {
            return MediaListData(parcel)
        }

        override fun newArray(size: Int): Array<MediaListData?> {
            return arrayOfNulls(size)
        }
    }
}