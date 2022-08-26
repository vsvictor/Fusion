package com.androidspace.fusion.data.model

import android.os.Parcel
import android.os.Parcelable
import com.esri.arcgisruntime.geometry.Point

data class PointData(val latitude: Double, val longitude: Double) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }
    constructor(point: Point):this(latitude = point.x, longitude = point.y)
    fun copy(): PointData{
        return PointData(latitude, longitude)
    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PointData> {
        override fun createFromParcel(parcel: Parcel): PointData {
            return PointData(parcel)
        }

        override fun newArray(size: Int): Array<PointData?> {
            return arrayOfNulls(size)
        }
    }
}
