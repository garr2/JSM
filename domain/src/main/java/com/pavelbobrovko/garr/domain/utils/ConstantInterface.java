package com.pavelbobrovko.garr.domain.utils;

/**
 * Created by garr on 21.05.2017.
 */

public interface ConstantInterface {

    String LOG_TAG="myLogs";

    String INSTANCE_TYPE_CREATE_NEW = "createNew";
    String INSTANCE_TYPE_UPDATE_CURRENT = "updateCurrent";

    //Fragment tags
    String LIST_FRAGMENT_TAG = "listFragment";
    String ROOM_FRAGMENT_TAG = "roomFragment";
    String USER_INFO_FRAGMENT_TAG = "userInfoFragment";
    String ROOM_INFO_FRAGMENT_TAG = "roomInfoFragment";
    String ROOM_ID_KEY = "room_id_key";
    String USER_ID_KEY = "user_id_key";
    String BUNDLE_KEY = "bundle_key";

    String INTENT_IMAGE = "intentImage";
    String ITENT_SIGN_IN = "intentSignIn"; //if 1 - register, if 2 - login in
    String ROOM_DATA_CHANGED= "com.garr.pavelbobrovko.notsimplechat.ROOM_DATA_CHANGED";
    String USER_REF_DATA_ADD = "com.garr.pavelbobrovko.notsimplechat.USER_REF_DATA_ADD";
    String USER_INFORMATION_UPDATED= "com.garr.pavelbobrovko.notsimplechat.USER_INFORMATION_UPDATED";
    String REGISTRATION_COMPLETE= "com.garr.pavelbobrovko.notsimplechat.REGISTRATION_COMPLETE";
    String SERVICE_REDY_TO_WORK = "com.garr.pavelbobrovko.notsimplechat.REDY_TO_WORK";
    String SERVICE_BINDED = "com.garr.pavelbobrovko.notsimplechat.SERVICE_BINDED";
    String ROOM_INFORMATION_IS_AVAILABLE = "com.garr.pavelbobrovko.notsimplechat.ROOM_INFO_AVAILABLE";
    String ROOM_SETTINGS_CHANGE = "com.garr.pavelbobrovko.notsimplechat.ROOM_SETTINGS_CHANGE";
    String ROOM_USER_LIST_CHANGE = "com.garr.pavelbobrovko.notsimplechat.ROOM_USER_LIST_CHANGE";
    String UPDATED_ROOM_ID = "com.garr.pavelbobrovko.notsimplechat.UPDATED_ROOM_ID";

    //ListAdapter constants
    int ROOM_LIST_TYPE = 1;
    int USER_LIST_TYPE = 2;

    //Room creation constants
    String CREATION_FINISHED = "com.garr.pavelbobrovko.notsimplechat.ROOM_CREATION_FINISHED";
    String RESULT_OF_READ_MESSAGES = "result_of_read_messages"; // 0=false, 1=true
    String RESULT_OF_READ_USERS = "result_of_read_users"; // 0=false, 1=true

    //Permission intent constants
    int PREMISSION_ACCESS_TO_INTERNET = 1;
    int PERMISSION_TO_READ_STORAGE_TAG = 2;
    String RESULT = "result";

    //Intent constants
    int RESUL_NEW_ID_CREATED = 5;

    //App preferences
    String NOTIFICATIONS = "notifications";
    String NOTIFICATION_SOUND = "notificationSound";

    //Notification constants
    String NOTIFICATION_ROOM = "notificationRoom";


    //Firebase constants
    //Storage constants
    String AVATAR_IMAGE_FOLDER = "avatar/";
    String ROOM_IMAGE_FOLDER = "roomImages/";
    String IMAGE_FOLDER = "images/";
    String SOUND_FOLDER= "sounds/";
    
    //Database constants
    String USERS_CHILD = "users";
    String USER_INFO_CHILD = "info";
    String USER_INFORMATION = "userInformation";
    String AVATAR_URL = "avatarURL";
    String ABOUT = "about";
    String FRIENDS_CHILD = "friends";
    String DISPLAY_NAME = "displayName" ;
    String PUBLIC_ROOM = "publicRoom";
    String PRIVATE_ROOM = "privateRoom";
    String ROOMS = "rooms";
    String MESSAGES = "messages";
    String ROOM_NAME = "nameRoom";
    String USER_IDS = "usersIDs";
    String USER_ID = "userID";
    String CURRENT_USER_ID = "currentUserID";
    String CURRENT_ROOM_ID = "currentRoomID";
    String ROOM_AVATAR_URL = "roomAvatarURL";
    String ROOM_SETTINGS = "romSettings";
    String MESSAGE = "message";
    String IMAGES = "images";
    String SOUNDS = "sounds";
    String TIME = "time";

}
