package com.garr.pavelbobrovko.notsimplechat.deprecated.APIConnectionService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.garr.pavelbobrovko.notsimplechat.deprecated.AuthenticationModule;
import com.pavelbobrovko.garr.domain.entity.RoomInfo;
import com.pavelbobrovko.garr.domain.entity.User;
import com.pavelbobrovko.garr.domain.utils.ConstantInterface;
import com.garr.pavelbobrovko.notsimplechat.deprecated.NotificationManager;
import com.garr.pavelbobrovko.notsimplechat.deprecated.OnCompleteListener;
import com.garr.pavelbobrovko.notsimplechat.deprecated.OnDBReadCompleteListener;
import com.garr.pavelbobrovko.notsimplechat.deprecated.dataContainers.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public abstract class InitService extends Service {

    protected AuthenticationModule mAuthentication;

    protected DatabaseReference chatReference;
    protected FirebaseStorage chatStorage;
    private StorageReference firebaseReference;
    private NotificationManager notificationManager;

    protected boolean isAuthenticate = false;
    protected boolean isServiceReady = false;
    private String displayEMail;
    protected long USER_ID = -1L;

    protected ArrayList<Room> rooms;
    protected HashMap<Long,User> usersRef;
    protected HashMap<Long,RoomInfo> roomInfoList;
    protected Long[] userRoomsPublic, userRoomsPrivate, userRoomsAll;
    protected Long[] userFriends;
    protected User user;

    protected String displayName;
    protected Uri avatarUrl;

    @Override
    public void onCreate(){
        super.onCreate();

        mAuthentication = AuthenticationModule.getInstance();

        isAuthenticate = mAuthentication.isAutorised();
        displayEMail = mAuthentication.getDisplayEMail();

        if (isAuthenticate)init();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void init(){
        initReferences();
        initCollections();
        getUserId(new OnDBReadCompleteListener<Long>() {
            @Override
            public void onComplete(Long type) {
                initUserInfo();
            }

            @Override
            public void onFailtrue(Exception e) {

            }
        });
    }

    private void initCollections(){
        rooms = new ArrayList<>();
        usersRef = new HashMap<>();
        roomInfoList = new HashMap<>();
    }

    private void initReferences(){
        chatStorage= FirebaseStorage.getInstance();
        firebaseReference= chatStorage.getReference();
        chatReference= FirebaseDatabase.getInstance().getReference();
        notificationManager=new NotificationManager(getFullService());
    }

    protected void getUserId(final OnDBReadCompleteListener listener){

        FirebaseDatabase.getInstance().getReference().child(ConstantInterface.USER_IDS).child(displayEMail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            USER_ID=dataSnapshot.getValue(Long.class);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            USER_ID = -1;
                        }
                        Log.d(ConstantInterface.LOG_TAG,"SCS.getUserID id is: " + USER_ID);
                        if (USER_ID == -1){
                            mAuthentication.signOut();
                        }

                        if (listener!= null) listener.onComplete(USER_ID);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mAuthentication.signOut();
                    }
                });
    }

    protected void initUserInfo(){
        if (!isAuthenticate)return;

        Log.d(ConstantInterface.LOG_TAG,"initUserInfo eMail: " + displayEMail);
        final User[] user = new User[1];
        getUserInformation(USER_ID, new OnDBReadCompleteListener<User>() {
            @Override
            public void onComplete(User type) {
                if (usersRef == null) new HashMap<>();
                usersRef.put(USER_ID,type);

                user[0] = type;

                displayName = user[0].getDisplayName();
                if (TextUtils.equals(displayName,"null")){
                    displayName=null;
                }

                if (user[0].getAvatarURL() != null) {
                    avatarUrl =Uri.parse( user[0].getAvatarURL());
                }

                setListenerOfUserRooms(new OnCompleteListener() {
                    boolean isPublicRoom = false;
                    boolean isPrivateRoom = false;

                    @Override
                    public void onComplete(Long... array) {
                        Log.d(ConstantInterface.LOG_TAG,"setListenerOfUserRooms.onComplete: " + array.length);
                        if (array==userRoomsPublic)isPublicRoom=true;
                        if (array==userRoomsPrivate)isPrivateRoom=true;
                        if (isPublicRoom&&isPrivateRoom){
                            isPrivateRoom=false;
                            isPublicRoom=false;
                            concatUserRooms();
                            initUserRooms();
                            LocalBroadcastManager.getInstance(getApplicationContext())
                                    .sendBroadcast(new Intent(ConstantInterface.CREATION_FINISHED)
                                            .putExtra(ConstantInterface.SERVICE_REDY_TO_WORK,1));
                            isServiceReady = true;
                        }
                    }

                    @Override
                    public void onFailtrue(Exception e) {

                    }
                });
                Log.d(ConstantInterface.LOG_TAG,"getUserFriends");
                getUserFriends();
            }

            @Override
            public void onFailtrue(Exception e) {

            }
        });
    }

    public void getUserInformation(final long _userId, @Nullable final OnDBReadCompleteListener<User> listener){
        if(user==null)user=new User();
        String id = String.valueOf(_userId);
        Log.d(ConstantInterface.LOG_TAG,"getUserInformation, id: " + id);
        chatReference.child(ConstantInterface.USER_INFORMATION).child(id).child(ConstantInterface.USER_INFO_CHILD)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user=dataSnapshot.getValue(User.class);
                        if (user != null) {
                            Log.d(ConstantInterface.LOG_TAG,user.getAbout() + user.getDisplayName() + user.getAvatarURL());
                        }else{
                            if (_userId == USER_ID) {
                                user=new User();
                                user.displayName = mAuthentication.getDisplayName();
                                user.avatarURL = mAuthentication.getPhotoUrl().toString();
                                Log.d(ConstantInterface.LOG_TAG,"user is null");
                            } else {
                                user=new User();
                                user.displayName = "noName";
                            }
                        }

                        usersRef.put(_userId,user);
                        LocalBroadcastManager.getInstance(getApplicationContext())
                                .sendBroadcast(new Intent(ConstantInterface.USER_REF_DATA_ADD));

                        if (listener!=null)listener.onComplete(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (listener!=null)listener.onFailtrue(databaseError.toException());
                        Log.d(ConstantInterface.LOG_TAG,"Database ERROR " + databaseError);
                    }
                });
        //return user;
    }

    private void setListenerOfUserRooms(@Nullable final OnCompleteListener listener){
        userRoomsPrivate=new Long[]{};

        Log.d(ConstantInterface.LOG_TAG,"setListenerOfUserRooms");
        chatReference.child(ConstantInterface.USER_INFORMATION).child(String.valueOf(USER_ID))
                .child(ConstantInterface.ROOMS).child(ConstantInterface.PUBLIC_ROOM)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userRoomsPublic=new Long[(int)dataSnapshot.getChildrenCount()];
                        int counter=0;
                        for (DataSnapshot data:dataSnapshot.getChildren()){
                            userRoomsPublic[counter++]= Objects.requireNonNull(data.getValue(Long.class));
                        }

                        if (userRoomsPublic.length==0) {
                            addToDefaultRoom();
                            userRoomsPublic = new Long[]{0L};
                        }
                        if (listener!=null)listener.onComplete(userRoomsPublic);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //throw new DatabaseException("Database read is Cancelled",databaseError.toException());
                    }
                });

        chatReference.child(ConstantInterface.USER_INFORMATION).child(String.valueOf(USER_ID))
                .child(ConstantInterface.ROOMS).child(ConstantInterface.PRIVATE_ROOM)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userRoomsPrivate=new Long[(int)dataSnapshot.getChildrenCount()];
                        int counter=0;
                        for (DataSnapshot data:dataSnapshot.getChildren()){
                            userRoomsPrivate[counter++]=data.getValue(Long.class);
                        }
                        if (listener!=null)listener.onComplete(userRoomsPrivate);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }

    private void concatUserRooms(){
        Log.d(ConstantInterface.LOG_TAG,"setAllUserRooms");
        userRoomsAll=new Long[userRoomsPublic.length + userRoomsPrivate.length];
        System.arraycopy(userRoomsPublic,0,userRoomsAll,0,userRoomsPublic.length);
        System.arraycopy(userRoomsPrivate,0,userRoomsAll,userRoomsPublic.length,userRoomsPrivate.length);
    }

    private void initUserRooms() {
        Log.d(ConstantInterface.LOG_TAG,"initUserRooms");
        if (rooms==null)rooms=new ArrayList<>();

        //if (userRoomsPublic==null)setListenerOfUserRooms(null);
        int counter = 0;
        for (Long idRoom : userRoomsPublic){
            Log.d(ConstantInterface.LOG_TAG,"instance public room, count = " + counter++);
            Room room = new Room(getApplicationContext(),idRoom,ConstantInterface.PUBLIC_ROOM,getFullService());
            rooms.add(room);
        }

        if (userRoomsPrivate!=null){
            for (Long idRoom : userRoomsPrivate){
                Log.d(ConstantInterface.LOG_TAG,"instance private room, count = " + counter++);
                Room room = new Room(getApplicationContext(),idRoom,ConstantInterface.PRIVATE_ROOM,getFullService());
                rooms.add(room);
            }
        }
    }

    public Long[] getUserFriends() throws DatabaseException {

        chatReference.child(ConstantInterface.USER_INFORMATION).child(String.valueOf(USER_ID))
                .child(ConstantInterface.FRIENDS_CHILD)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userFriends=new Long[(int)dataSnapshot.getChildrenCount()];
                        int counter=0;
                        for (DataSnapshot data:dataSnapshot.getChildren()){
                            userFriends[counter++]= data.getValue(Long.class);

                        }

                        initUsersRef();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // throw new DatabaseException("Database read is Cancelled!",databaseError.toException());
                    }
                });
        return userFriends;
    }

    @SuppressLint("UseSparseArrays")
    private void initUsersRef(){
        if (usersRef==null)usersRef = new HashMap<>();
        if (userFriends!=null&&userFriends.length>0){
            for (final Long friend : userFriends){

                getUserInformation(friend, new OnDBReadCompleteListener<User>() {
                    @Override
                    public void onComplete(User type) {
                        User user= type;
                        usersRef.put(friend,user);
                    }

                    @Override
                    public void onFailtrue(Exception e) {

                    }
                });
            }
        }
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public HashMap<Long, User> getUsersRef() {
        return usersRef;
    }

    public HashMap<Long, RoomInfo> getRoomInfoList() {
        return roomInfoList;
    }

    public Long[] getUserRoomsPublic() {
        return userRoomsPublic;
    }

    public Long[] getUserRoomsPrivate() {
        return userRoomsPrivate;
    }

    public Long[] getUserRoomsAll() {
        return userRoomsAll;
    }

    public User getUser() {
        return user;
    }

    public boolean isServiceReady(){
        return isServiceReady;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Uri getUserAvatarUrl() {
        return avatarUrl;
    }

    public NotificationManager getNotificationManager(){
        if (notificationManager==null)notificationManager=new NotificationManager(getFullService());
        return notificationManager;
    }

    protected abstract CoordinatingService getFullService();

    protected abstract void addToDefaultRoom();

}
