package com.garr.pavelbobrovko.notsimplechat.deprecated.dataContainers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.garr.pavelbobrovko.notsimplechat.deprecated.APIConnectionService.CoordinatingService;
import com.garr.pavelbobrovko.notsimplechat.deprecated.NotificationManager;
import com.garr.pavelbobrovko.notsimplechat.deprecated.OnDBReadCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pavelbobrovko.garr.domain.entity.Message;
import com.pavelbobrovko.garr.domain.entity.RoomInfo;
import com.pavelbobrovko.garr.domain.utils.ConstantInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by garr on 18.12.2017.
 */

public class Room {

    private ArrayList<Message> arrayData;
    private Long[] usersList;
    private HashMap<Long,RoomInfo> roomInfoList;
    private Context mCtx;
    private NotificationManager manager;
    private RoomInfo roomInfo;

    private long idRoom, userID;
    private String nameRoom;
    private String privacy;
    private String displayName;
    //private String eMail;
    private boolean isCreatedMessages=true;
    private boolean isGettingRoomInfo = true;

    private final long MINUTES=60000;
    private final long HOURS=3600000;
    private final long DAY_LENGTH=86400000;


    private DatabaseReference roomDB_Reference;
    private FirebaseStorage chatStorage;
    private StorageReference firebaseReference;

    private CoordinatingService coordinatingService;


    public Room(Context _mCtx, Long _idRoom,String _privacy,CoordinatingService _scs){
        Log.d(ConstantInterface.LOG_TAG,"Room constructor ID: " + _idRoom + " privacy " + _privacy);
        mCtx=_mCtx;
        coordinatingService =_scs;
        idRoom=_idRoom;
        privacy=_privacy;
        manager= coordinatingService.getNotificationManager();
        displayName= coordinatingService.getDisplayName();
        coordinatingService.getIDValue(new OnDBReadCompleteListener<Long>() {
            @Override
            public void onComplete(Long type) {
                userID = type;
            }

            @Override
            public void onFailtrue(Exception e) {

            }
        });
        roomInfoList = coordinatingService.getRoomInfoList();
        arrayData=new ArrayList<>();
        roomInfo = new RoomInfo();
        roomDB_Reference = FirebaseDatabase.getInstance().getReference();
        chatStorage=FirebaseStorage.getInstance();
        firebaseReference= chatStorage.getReference();
        roomDB_Reference.child(ConstantInterface.ROOMS).child(privacy).child(String.valueOf(idRoom))
                .child(ConstantInterface.MESSAGES).addValueEventListener(mesages);
        roomDB_Reference.child(ConstantInterface.ROOMS).child(privacy).child(String.valueOf(idRoom))
                .child(ConstantInterface.USERS_CHILD).addValueEventListener(users);
        instRoomSettings();
    }

    private ValueEventListener mesages = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            arrayData.clear();
            for (DataSnapshot data : dataSnapshot.getChildren()){
                arrayData.add(data.getValue(Message.class));

                //Log.d(ConstantInterface.LOG_TAG, "room: " + nameRoom + " Message size "  + arrayData.size());

            }

            if (!isCreatedMessages) {
                LocalBroadcastManager.getInstance(mCtx)
                        .sendBroadcast(new Intent(ConstantInterface.ROOM_DATA_CHANGED)
                        .putExtra(ConstantInterface.ROOM_NAME,idRoom));
                    manager.setNotification(arrayData.get(arrayData.size()-1),nameRoom,privacy);

            } else {
                LocalBroadcastManager.getInstance(mCtx)
                        .sendBroadcast(new Intent(ConstantInterface.CREATION_FINISHED)
                        .putExtra(ConstantInterface.RESULT_OF_READ_MESSAGES,1));
                isCreatedMessages=false;
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d(ConstantInterface.LOG_TAG,"database error");
            if (isCreatedMessages){
                LocalBroadcastManager.getInstance(mCtx)
                        .sendBroadcast(new Intent(ConstantInterface.CREATION_FINISHED)
                        .putExtra(ConstantInterface.RESULT_OF_READ_MESSAGES,0));
                isCreatedMessages=false;
            }
        }
    };

    private ValueEventListener users = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            usersList = new Long[(int)dataSnapshot.getChildrenCount()];

            int counter=0;
            for (DataSnapshot data:dataSnapshot.getChildren()){
                usersList[counter++]= Objects.requireNonNull(data.getValue(Long.class));
            }

            LocalBroadcastManager.getInstance(mCtx)
                    .sendBroadcast(new Intent(ConstantInterface.ROOM_USER_LIST_CHANGE)
                    .putExtra(ConstantInterface.UPDATED_ROOM_ID,idRoom));

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void instRoomSettings(){
        isGettingRoomInfo = true;
        roomDB_Reference.child(ConstantInterface.ROOMS).child(privacy).child(String.valueOf(idRoom))
                .child(ConstantInterface.ROOM_SETTINGS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RoomInfo _roomInfo = dataSnapshot.getValue(RoomInfo.class);
                Log.d(ConstantInterface.LOG_TAG,"roomInfo is received id " + idRoom);
                if (_roomInfo == null){
                    Log.d(ConstantInterface.LOG_TAG,"ROOM_IFO IS NULL");
                }else{
                    roomInfo = _roomInfo;
                    Log.d(ConstantInterface.LOG_TAG,"Room name " + roomInfo.getRoomName());

                   roomInfoList.put(idRoom,_roomInfo);
                    LocalBroadcastManager.getInstance(mCtx)
                            .sendBroadcast(new Intent(ConstantInterface.ROOM_INFORMATION_IS_AVAILABLE));
                }
                isGettingRoomInfo = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isGettingRoomInfo = false;
            }
        });
    }

    public void updateRoomSettings(final RoomInfo info){
        roomDB_Reference.child(ConstantInterface.ROOMS).child(privacy).child(String.valueOf(idRoom))
                .child(ConstantInterface.ROOM_SETTINGS).setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    roomInfo = info;
                    roomInfoList.put(idRoom,roomInfo);
                    LocalBroadcastManager.getInstance(mCtx)
                            .sendBroadcast(new Intent(ConstantInterface.ROOM_SETTINGS_CHANGE)
                            .putExtra(ConstantInterface.UPDATED_ROOM_ID,idRoom));
                    Log.d(ConstantInterface.LOG_TAG,"Room settings change");
                    Toast.makeText(mCtx,"Room settings change",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public Long[] getRoomUserList(){return usersList;}

    public long getRoomUsersListSize(){return usersList.length;}



    public Long getRoomId(){return idRoom;}

    private void setNameRoom(String _nameRoom) {
        nameRoom = _nameRoom;
    }

    public RoomInfo getRoomInfo() {
        if (!isGettingRoomInfo) {
            instRoomSettings();
        }
        return roomInfo;
    }

    public boolean sendMessage(final Message message) {
        //data.displayName=displayName;
        message.setUserId(userID);
        message.setTime(getTime());
        roomDB_Reference.child(ConstantInterface.ROOMS).child(privacy).child(String.valueOf(idRoom))
                .child(ConstantInterface.MESSAGES).push().setValue(message);

        return false;
    }

    private long getTime(){
        return System.currentTimeMillis();
    }


    public int getMessagesSize() {
        return arrayData.size();
    }

    public String getDisplayName() {
        return displayName;
    }

    public ArrayList<Message> getArrayData() {
        return arrayData;
    }

}
