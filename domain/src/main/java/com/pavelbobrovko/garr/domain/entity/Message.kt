package com.pavelbobrovko.garr.domain.entity

import android.os.Parcel
import android.os.Parcelable
import com.pavelbobrovko.garr.domain.utils.ConstantInterface
import java.util.HashMap

data class Message(val str: String = ""): Parcelable, DomainEntity {

    var userId: Long = -1
    lateinit var message: String
    lateinit var images: String
    lateinit var sounds: String
    var time: Long = -1

    constructor(userId: Long, message: String = ""
                , images: String = "", sounds: String = ""
                , time: Long) : this() {
        this.userId = userId
        this.message = message
        this.images = images
        this.sounds = sounds
        this.time = time
    }

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readLong()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(userId)
        parcel.writeString(message)
        parcel.writeString(images)
        parcel.writeString(sounds)
        parcel.writeLong(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }

    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result[ConstantInterface.USER_ID] = userId
        result[ConstantInterface.MESSAGE] = message
        result[ConstantInterface.IMAGES] = images
        result[ConstantInterface.SOUNDS] = sounds
        result[ConstantInterface.TIME] = time

        return result
    }
}