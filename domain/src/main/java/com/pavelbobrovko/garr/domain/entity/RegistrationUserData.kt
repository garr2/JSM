package com.pavelbobrovko.garr.domain.entity

import android.os.Parcel
import android.os.Parcelable

data class RegistrationUserData(val userId: String = "", val email: String = ""
                                , var isEmailVerify: Boolean = false,var oauthIdToken: String = "", val user: User?): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readParcelable(User::class.java.classLoader)) {
    }

    constructor(str: String = "") : this(user = null) {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(email)
        parcel.writeByte(if (isEmailVerify) 1 else 0)
        parcel.writeString(oauthIdToken)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RegistrationUserData> {
        override fun createFromParcel(parcel: Parcel): RegistrationUserData {
            return RegistrationUserData(parcel)
        }

        override fun newArray(size: Int): Array<RegistrationUserData?> {
            return arrayOfNulls(size)
        }
    }
}