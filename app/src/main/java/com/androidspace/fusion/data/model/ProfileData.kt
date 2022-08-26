package com.androidspace.fusion.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ProfileData(
    @SerializedName("id")
    val id: Long = -1,
    @SerializedName("first_name")
    val firstName: String = "",
    @SerializedName("last_name")
    val lastName: String = "",
    @SerializedName("middle_name")
    val middleName: String = "",
    @SerializedName("inn")
    val ipn: String = "",
    @SerializedName("phone")
    val phone: String = "",
    @SerializedName("email")
    var email: String? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(middleName)
        parcel.writeString(ipn)
        parcel.writeString(phone)
        parcel.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProfileData> {
        override fun createFromParcel(parcel: Parcel): ProfileData {
            return ProfileData(parcel)
        }

        override fun newArray(size: Int): Array<ProfileData?> {
            return arrayOfNulls(size)
        }
    }
}
