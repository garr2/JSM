package com.garr.pavelbobrovko.notsimplechat.services

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.IBinder
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.v4.app.NotificationCompat
import com.garr.pavelbobrovko.notsimplechat.presentation.screen.main.MainActivity
import com.google.firebase.storage.FirebaseStorage
import com.pavelbobrovko.garr.domain.entity.RoomInfo
import com.pavelbobrovko.garr.domain.entity.User
import com.pavelbobrovko.garr.domain.utils.ConstantInterface
import com.pavelbobrovko.garr.domain.utils.NotificationManagerConst

class NotificationService: Service() {

    companion object {
        private const val MESSAGE_NOTIF_ID = 1
        private const val USER_NOTIF_ID = 2
    }

    private val sPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    private val sBinder = ServiceBinder()

    var isAppFocused = false
    private val displayId = 0L
    private var messageCounter = 0
    private var userCounter = 0

    override fun onBind(p0: Intent?): IBinder? {
        isAppFocused = true
       return sBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isAppFocused = false
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)

    }

    private fun setNotification(map: Map<String, Any>){
        val noifPref = sPref.getBoolean(ConstantInterface.NOTIFICATIONS, true)
        if (!isActivityFocused() && noifPref){

            val type = map[NotificationManagerConst.INPUT_NOTIF_TYPE] as String
            val user = map[NotificationManagerConst.INPUT_USER] as User
            val id = map[NotificationManagerConst.INPUT_ID] as Long
            val builder = initBuider()

            if ( type == NotificationManagerConst.NEW_MESSAGE_TYPE){
                val privacy = map[NotificationManagerConst.INPUT_PRIVACY] as String

                newMessageNotification(builder,user, privacy, id)
            }else{
                newUserNotification(builder,user,id)
            }
        }
    }

    private fun isActivityFocused(): Boolean{
        if (isAppFocused){
            messageCounter = 0
            userCounter = 0
        }
        return isAppFocused
    }

    private fun initBuider(): NotificationCompat.Builder{
        val builder  = NotificationCompat.Builder(this)
        return builder
    }

    private fun newMessageNotification(builder: NotificationCompat.Builder,
                                       user: User, privacy: String, roomId: Long){


        //FIXME getting roomInfo information by roomId
        builder.setContentTitle(roomId.toString())//FIXIT
                .setContentText(generateNewMessageString(user,privacy, RoomInfo()))

        val isSoundOn = sPref.getBoolean(ConstantInterface.NOTIFICATION_SOUND,true)

        if (isSoundOn) {
            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
        }

        val intent = Intent(this, MainActivity::class.java)
                .putExtra(ConstantInterface.NOTIFICATION_ROOM, roomId)
        val pIntent = PendingIntent.getActivity(this, MESSAGE_NOTIF_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pIntent)
                .setAutoCancel(true)

        val notification = builder.build()

        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(MESSAGE_NOTIF_ID, notification)
    }

    private fun newUserNotification(builder: NotificationCompat.Builder, user: User, id: Long){
        builder.setContentTitle(user.displayName)
                .setContentText(generateNewUserString(user))
        val intent = Intent(this, MainActivity::class.java)
                .putExtra(ConstantInterface.NOTIFICATION_ROOM, id)
        val pIntent = PendingIntent.getActivity(this, USER_NOTIF_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pIntent)
                .setAutoCancel(true)

        val notification = builder.build()

        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(USER_NOTIF_ID, notification)
    }

    private fun generateNewMessageString(user: User, privacy: String, roomInfo: RoomInfo?): String{
        if (messageCounter<2) {
            if (privacy == ConstantInterface.PUBLIC_ROOM){
                return "${user.displayName} оставил сообщение в беседе ${roomInfo?.roomName}."
            }else return "${user.displayName} оставил ваи личное собщение."
        }else return "У вас $messageCounter непрочитанных собщений."
    }

    private fun generateNewUserString(user: User): String{
        return "${user.displayName} хочет добавить вас в друзья."
    }

    inner class ServiceBinder : Binder() {
        val service: NotificationService = this@NotificationService

    }
}