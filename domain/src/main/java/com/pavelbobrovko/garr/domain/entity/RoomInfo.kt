package com.pavelbobrovko.garr.domain.entity

import android.os.Parcel
import android.os.Parcelable
import com.pavelbobrovko.garr.domain.utils.ConstantInterface
import java.util.HashMap

data class RoomInfo(val str: String = ""): Parcelable, DomainEntity {

    lateinit var roomName: String
    lateinit var roomAvatarURL: String

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!) {
    }

    constructor(roomName: String, roomAvatarURL: String = "") : this() {
        this.roomName = roomName
        this.roomAvatarURL = roomAvatarURL
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(roomName)
        parcel.writeString(roomAvatarURL)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoomInfo> {
        override fun createFromParcel(parcel: Parcel): RoomInfo {
            return RoomInfo(parcel)
        }

        override fun newArray(size: Int): Array<RoomInfo?> {
            return arrayOfNulls(size)
        }
    }

    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result[ConstantInterface.ROOM_NAME] = roomName
        result[ConstantInterface.ROOM_AVATAR_URL] = roomAvatarURL

        return result
    }
}