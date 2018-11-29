package com.pavelbobrovko.garr.domain.entity

import android.os.Parcel
import android.os.Parcelable
import java.util.HashMap


data class User(val str: String = "") : Parcelable, DomainEntity {

    lateinit var displayName: String
    lateinit var about: String
    lateinit var avatarURL: String

    constructor(parcel: Parcel) : this(
            parcel.readString()!!) {
        displayName = parcel.readString()!!
        about = parcel.readString()!!
        avatarURL = parcel.readString()!!
    }

    constructor(displayName: String = "", avatarURL: String = "", about: String = ""
                , localId: String = "", email: String = "") : this() {
        this.displayName = displayName
        this.avatarURL = avatarURL
        this.about = about
    }


    /*fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result[DISPLAY_NAME] = displayName
        result[AVATAR_URL] = avatarURL
        result[ABOUT] = about

        return result
    }*/
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(str)
        parcel.writeString(displayName)
        parcel.writeString(about)
        parcel.writeString(avatarURL)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}