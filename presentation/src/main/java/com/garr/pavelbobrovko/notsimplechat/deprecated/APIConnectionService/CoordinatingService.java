package com.garr.pavelbobrovko.notsimplechat.deprecated.APIConnectionService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pavelbobrovko.garr.domain.entity.RoomInfo;
import com.pavelbobrovko.garr.domain.entity.User;
import com.pavelbobrovko.garr.domain.utils.ConstantInterface;
import com.garr.pavelbobrovko.notsimplechat.deprecated.NotificationManager;
import com.garr.pavelbobrovko.notsimplechat.deprecated.OnDBReadCompleteListener;
import com.garr.pavelbobrovko.notsimplechat.deprecated.dataContainers.Room;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class CoordinatingService extends InitService {

    private RegistrationReceiver registrationReceiver;
    private NotificationManager notificationManager;

    private ServiceBinder sBinder = new ServiceBinder();

    @Override
    public void onCreate(){
        super.onCreate();
        notificationManager=new NotificationManager(this);

        registrationReceiver=new RegistrationReceiver();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(registrationReceiver,new IntentFilter(ConstantInterface.REGISTRATION_COMPLETE));
    }

    @Override
    public void onDestroy(){
        if (registrationReceiver!=null)unregisterReceiver(registrationReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sBinder;
    }

    @Override
    protected CoordinatingService getFullService() {
        return this;
    }

    @Override
    protected void addToDefaultRoom() {
        addToRoom(0L,ConstantInterface.PUBLIC_ROOM);
    }

    public void addToRoom(final Long roomId, final String privacy){
        if (roomId == 0L ) {
            checkRoomID(roomId, new OnDBReadCompleteListener<Boolean>() {
                @Override
                public void onComplete(Boolean type) {
                    boolean result = type;
                    if (result) {
                        chatReference.child(ConstantInterface.USER_INFORMATION).child(String.valueOf(USER_ID))
                                .child(ConstantInterface.ROOMS).child(privacy).push().setValue(roomId);
                        chatReference.child(ConstantInterface.ROOMS).child(privacy)
                                .child(String.valueOf(roomId)).child(ConstantInterface.USERS_CHILD).push().setValue(USER_ID);
                    } else {
                        RoomInfo newRoom = new RoomInfo("Chat", "");
                        createRoom(newRoom, ConstantInterface.PUBLIC_ROOM);
                    }
                }

                @Override
                public void onFailtrue(Exception e) {

                }
            });
        }else {
            chatReference.child(ConstantInterface.USER_INFORMATION).child(String.valueOf(USER_ID))
                    .child(ConstantInterface.ROOMS).child(privacy).push().setValue(roomId);
            chatReference.child(ConstantInterface.ROOMS).child(privacy)
                    .child(String.valueOf(roomId)).child(ConstantInterface.USERS_CHILD).push().setValue(USER_ID);
        }
    }

    private void checkRoomID(long roomID, final OnDBReadCompleteListener<Boolean> listener){
        chatReference.child(ConstantInterface.ROOMS).child(ConstantInterface.PUBLIC_ROOM)
                .child(String.valueOf(roomID)).child(ConstantInterface.ROOM_SETTINGS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        RoomInfo info = dataSnapshot.getValue(RoomInfo.class);
                        if (info == null){
                            if (listener!=null)listener.onComplete(false);
                        }else {
                            if (listener!=null)listener.onComplete(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void createRoom(final RoomInfo info, final String privacy){
        final long[] id = new long[1];
        getCurrentRoomID(new OnDBReadCompleteListener<Long>() {
            @Override
            public void onComplete(Long type) {
                id[0] = type;

                chatReference.child(ConstantInterface.CURRENT_ROOM_ID).setValue(id[0]+1);
                chatReference.child(ConstantInterface.ROOMS).child(privacy).child(String.valueOf(id[0]))
                        .child(ConstantInterface.ROOM_SETTINGS).setValue(info)
                        .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                addToRoom(id[0],privacy);
                            }
                        });
            }

            @Override
            public void onFailtrue(Exception e) {

            }
        });


    }

    private void getCurrentRoomID(final OnDBReadCompleteListener listener){
        chatReference.child(ConstantInterface.CURRENT_ROOM_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long currentID;
                try {
                    currentID = Objects.requireNonNull((long)dataSnapshot.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                    currentID = 0L;
                }
                listener.onComplete(currentID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Room getRoom(long roomID){
        Room room = null;
        //Log.e(ConstantInterface.LOG_TAG,"  fds" + rooms.size());
        if (rooms.size()!=0) {
            for (int i=0;i<=rooms.size();i++){
                room=rooms.get(i);
                if (roomID== room.getRoomId()){
                    break;
                }
            }
        }

        if (room == null){
            Log.d(ConstantInterface.LOG_TAG,"MainActivity.openRoom , create new Room");
            room=new Room(this,roomID,ConstantInterface.PUBLIC_ROOM, this);
            rooms.add(room);
        }
        return room;
    }

    public RoomInfo getRoomInfo (long roomID){
        RoomInfo info = new RoomInfo();
        for (Room room : rooms){
            if (room.getRoomId() == roomID){

                info = room.getRoomInfo();
                break;
            }
        }
        return info;
    }

    public long getRoomUsersListSize(long roomId){
        long size = -1L;

        for (Room room : rooms){
            if (room.getRoomId() == roomId){

                size = room.getRoomUsersListSize();
                break;
            }
        }
        return size;
    }

    public Long[] getRoomUsersList (long roomId){
        Long[]usersList = new Long[0];

        for (Room room : rooms){
            if (room.getRoomId() == roomId){

                usersList = room.getRoomUserList();
                break;
            }
        }
        return usersList;
    }

    public void updateRoomSettings(final long _id, RoomInfo _info){
        for (Room room : rooms){
            if (room.getRoomId() == _id){
                room.updateRoomSettings(_info);
                break;
            }
        }
    }

    public void uploadNewRoomImage(final long _id, final RoomInfo info, Uri imageUrlLocal){
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
        mStorageReference=mStorageReference.child(ConstantInterface.ROOM_IMAGE_FOLDER
                + imageUrlLocal.getLastPathSegment());
        mStorageReference.putFile(imageUrlLocal).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                info.setRoomAvatarURL(taskSnapshot.getUploadSessionUri().toString());
                updateRoomSettings(_id,info);
            }
        });
    }

    public void uploadAvatarAndChangeUser (final boolean isUpdate, Uri localPhoto, String imageTile
            , final Long userID, final String displayName, final String aboutUser, final Uri _photoUrl){


        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
        mStorageReference=mStorageReference.child(ConstantInterface.AVATAR_IMAGE_FOLDER + imageTile + ".jpg");
        mStorageReference.putFile(localPhoto).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                avatarUrl = taskSnapshot.getUploadSessionUri();
                Log.d(ConstantInterface.LOG_TAG,"Image loaded URL is: " + avatarUrl);
                changeUserInformation(isUpdate,userID,displayName,aboutUser,avatarUrl);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(ConstantInterface.LOG_TAG,"Image update failed  " + e);
                avatarUrl=_photoUrl;
                changeUserInformation(isUpdate,userID,displayName,aboutUser,_photoUrl);

            }
        });

    }

    public void changeUserInformation(final boolean isUpdate, final Long userID, String displayName, String aboutUser, Uri _photoUrl){
        if (USER_ID == -1) {
            getUserId(null);
        }
        Log.d(ConstantInterface.LOG_TAG,"changeUserInformation");
        Log.d(ConstantInterface.LOG_TAG,"userID: " + userID);

        User user = new User();
        user.displayName = displayName;
        user.about = aboutUser;
        user.avatarURL = _photoUrl.toString();
        chatReference.child(ConstantInterface.USER_INFORMATION).child(String.valueOf(userID)).child(ConstantInterface.USER_INFO_CHILD)
                .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(ConstantInterface.LOG_TAG,"changeUserInformation.onSuccess");
                initUserInfo();

                Intent intent =new Intent (ConstantInterface.USER_INFORMATION_UPDATED);
                intent.putExtra(ConstantInterface.RESULT,true);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);


            }
        });

    }

    public void getIDValue(@Nullable OnDBReadCompleteListener listener) {
        if (USER_ID == -1) {
            getUserId(listener);
        } else if (listener != null) {
            Log.d(ConstantInterface.LOG_TAG, "SCS.getIdValue USER_ID = " + USER_ID);
            listener.onComplete(USER_ID);
        }
    }

    private void authentication(){
       isAuthenticate = mAuthentication.authenticate();
       init();
    }

    public NotificationManager getNotificationManager(){
        if (notificationManager==null)notificationManager=new NotificationManager(this);
        return notificationManager;}

    public class RegistrationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(ConstantInterface.LOG_TAG,"ACS.RegistrationReceiver.onReceive");
            authentication();
        }
    }

    public class ServiceBinder extends Binder {
       public CoordinatingService getService (){
            return CoordinatingService.this;
        }
    }
}
