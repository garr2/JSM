package com.garr.pavelbobrovko.notsimplechat.deprecated;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.garr.pavelbobrovko.notsimplechat.deprecated.APIConnectionService.CoordinatingService;
import com.garr.pavelbobrovko.notsimplechat.R;
import com.pavelbobrovko.garr.domain.entity.Message;
import com.pavelbobrovko.garr.domain.entity.User;
import com.garr.pavelbobrovko.notsimplechat.presentation.screen.main.MainActivity;
import com.pavelbobrovko.garr.domain.utils.ConstantInterface;

import java.util.HashMap;

/**
 * Created by garr on 26.03.2018.
 */

public class NotificationManager {

    private boolean isFocused = false;
    private CoordinatingService coordinatingService;
    private Context mCtx;
    private HashMap usersRef;
    //private User userInfo;
    private SharedPreferences sPref;

    private final int PUBLIC_NOTIF_ID=1;
    private boolean isConnected=false;
    private int counter = 0;
    private long displayId = -1L;

    public NotificationManager(CoordinatingService _service){
        coordinatingService = _service;
        mCtx=_service;
        usersRef= coordinatingService.getUsersRef();
        sPref = PreferenceManager.getDefaultSharedPreferences(mCtx);
        coordinatingService.getIDValue(new OnDBReadCompleteListener<Long>() {
            @Override
            public void onComplete(Long type) {
                displayId = type;
            }

            @Override
            public void onFailtrue(Exception e) {

            }
        });
    }

    public void isAppFocused(boolean focused) {
        if (focused)resetCounter();
        isFocused=focused;
    }

    public void setNotification(Message message, String nameRoom, String privacy) {
        boolean noifPref = sPref.getBoolean(ConstantInterface.NOTIFICATIONS,true);
        if (message.getUserId() != displayId) {
            if (!isFocused && noifPref) {
                counter++;
                switch (privacy) {
                    case ConstantInterface.PUBLIC_ROOM:
                        setPublicNotifcation(message, nameRoom);
                        break;
                    case ConstantInterface.PRIVATE_ROOM:
                        setPrivateNotification();
                        break;
                }
            }
        }
    }

    private void setPrivateNotification() {

    }

    private void setPublicNotifcation(Message message, String nameRoom) {
        User info=getUserInfo(message);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mCtx)
                        .setSmallIcon(R.mipmap.ic_launcher);
        if (counter<2){
             builder.setContentTitle(nameRoom)
                    .setContentText(info.getDisplayName() + " оставил(а) новое собщение.");
        }else {
            builder.setContentTitle(nameRoom)
                    .setContentText("У вас " + counter + " не прочитанных собщений");
        }


        boolean notifSound = sPref.getBoolean(ConstantInterface.NOTIFICATION_SOUND,true);

        if (notifSound) {
                builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        }

        Intent intent = new Intent(mCtx,MainActivity.class)
                .putExtra(ConstantInterface.NOTIFICATION_ROOM,nameRoom);
        PendingIntent pIntent= PendingIntent.getActivity(mCtx,PUBLIC_NOTIF_ID,intent
                ,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pIntent)
                .setAutoCancel(true);

        Notification notification = builder.build();

        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) mCtx.getSystemService(mCtx.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(PUBLIC_NOTIF_ID, notification);
    }

    private User getUserInfo(final Message message) {
        final User[] userInfo = new User[1];
        userInfo[0] =(User) usersRef.get(message.getUserId());

        if (usersRef.get(message.getUserId()) != null){
            return (User) usersRef.get(message.getUserId());
        }else {
            coordinatingService.getUserInformation(message.getUserId(), new OnDBReadCompleteListener<User>() {
                @Override
                public void onComplete(User type) {
                    userInfo[0] = type;
                }

                @Override
                public void onFailtrue(Exception e) {

                }
            });
        }

        return userInfo[0];
    }

    private void resetCounter(){counter=0;}
}
