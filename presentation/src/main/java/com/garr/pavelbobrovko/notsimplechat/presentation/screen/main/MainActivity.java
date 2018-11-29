package com.garr.pavelbobrovko.notsimplechat.presentation.screen.main;

import android.annotation.TargetApi;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.garr.pavelbobrovko.notsimplechat.databinding.ActivityMainBinding;
import com.garr.pavelbobrovko.notsimplechat.deprecated.APIConnectionService.CoordinatingService;
import com.garr.pavelbobrovko.notsimplechat.deprecated.AuthenticationModule;
import com.garr.pavelbobrovko.notsimplechat.presentation.screen.userSettings.UserSettingsActivity;
import com.pavelbobrovko.garr.domain.entity.Message;
import com.pavelbobrovko.garr.domain.entity.RegistrationUserData;
import com.pavelbobrovko.garr.domain.entity.User;
import com.pavelbobrovko.garr.domain.utils.ConstantInterface;
import com.garr.pavelbobrovko.notsimplechat.deprecated.Main_Toolbar;
import com.garr.pavelbobrovko.notsimplechat.deprecated.NotificationManager;
import com.garr.pavelbobrovko.notsimplechat.deprecated.OnDBReadCompleteListener;
import com.garr.pavelbobrovko.notsimplechat.R;
import com.garr.pavelbobrovko.notsimplechat.deprecated.adapters.DrawerButtonsAdapter;
import com.garr.pavelbobrovko.notsimplechat.deprecated.dataContainers.Room;
import com.garr.pavelbobrovko.notsimplechat.deprecated.fragments.FragmentSetter;
import com.garr.pavelbobrovko.notsimplechat.presentation.base.BaseMvvmActivity;
import com.garr.pavelbobrovko.notsimplechat.presentation.screen.login.LoginActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseMvvmActivity<MainViewModel, MainRouter, ActivityMainBinding> {

    private FrameLayout uiFragment;
    private Toolbar toolbar;
    private Main_Toolbar mainToolbar;
    private DrawerLayout dlMain;
    private ListView lvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    private ServiceConnection sConn;
    private CoordinatingService coordinatingService;
    private FragmentSetter fragmentSetter;
    private RoomCreatedReceiver rcr;
    private RoomSettingsUpdReceiver roomSettingsUpdReceiver;
    private RoomUserListUpdReceiver roomUserListUpdReceiver;
    private NotificationManager manager;
    private SharedPreferences sPref;
    private SharedPreferences.Editor ed;
    private Menu menu;
    private Intent notifIntent;
    private DrawerButtonsAdapter buttonsAdapter;
    private Handler h;
    private AuthenticationModule mAuthenticate;

    private ArrayList<Room> rooms;
    private Context mCtx=this;
    private long notificationRoom;
    private boolean isConnected = false;
    private boolean isAutorised = false;
    private boolean isSvaedState = false;
    private int currentFragmentID;
    private Integer fragmentID=-1; //1=listFragment 2=roomFragment 3=userInformationFragment
    private final int CREATE_ACOUNT= 25;
    private final int USER_SETTINGS = 26;

    private static final String KEY_FRAGMENT_ID = "FRAGMENT_ID";
    private static final String KEY_MAIN_TOOLBAR = "MAIN_TOOLBAR";
    private final String LIST_FRAGMENT_TAG = "listFragment";
    private final String ROOM_FRAGMENT_TAG = "roomFragment";
    private final String USER_INFO_FRAGMENT_TAG = "userInfoFragment";
    private final String ROOM_INFO_FRAGMENT_TAG = "roomInfoFragment";
    private final int USER_AVATAR_LOGO = 1;
    private final int ROOM_IMAGE_LOGO = 2;

    private final String LOCAL_ID = "localId";
    private final String PASS = "qwerty";

    private EncryptedPreferences ePreferences;

    private String localId;

    FirebaseFunctions functions;

    @NotNull
    @Override
    public MainViewModel provideViewModel() {
        return ViewModelProviders.of(this).get(MainViewModel.class);
    }

    @Override
    public int provideLayoutId() {
        return R.layout.activity_main;
    }

    @NotNull
    @Override
    public MainRouter provideRouter() {
        return new MainRouter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startLoginActivity();











        /*notifIntent=getIntent();
        notificationRoom=notifIntent.getLongExtra(ConstantInterface.NOTIFICATION_ROOM,-1L);


        if (savedInstanceState!=null){
            fragmentID=savedInstanceState.getInt(KEY_FRAGMENT_ID);
            mainToolbar = savedInstanceState.getParcelable(KEY_MAIN_TOOLBAR);
            isSvaedState = true;
        }

        mAuthenticate = AuthenticationModule.getInstance();

        if (mAuthenticate.authenticate()) {
            init();
        } else {
            startActivity(new Intent(this,LoginActivity.class));
        }*/

    }

    public void startLoginActivity(){
        startActivityForResult(new Intent(this,LoginActivity.class), CREATE_ACOUNT);
    }

    public void startUserSettingsActivity(String instanceType, RegistrationUserData data){
        startActivityForResult(UserSettingsActivity.Companion.getInstance(this,instanceType,data),USER_SETTINGS);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_FRAGMENT_ID,fragmentID);
        outState.putParcelable(KEY_MAIN_TOOLBAR,mainToolbar);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        /*int index = this.getFragmentManager().getBackStackEntryCount()-1;
        if (dlMain.isDrawerOpen(GravityCompat.START)){
            dlMain.closeDrawer(GravityCompat.START);
        }else if (index>0){
            if (fragmentID==2){
                fragmentID=1;
            }else if (fragmentID == 3){
                if (currentFragmentID==2){
                    fragmentID=2;
                    currentFragmentID=1;
                }else {
                    fragmentID = 1;
                    currentFragmentID = 0;
                }
            }if (fragmentID == 4){
                fragmentID = 2;
                currentFragmentID = 1;
            }
            mainToolbar.update(fragmentID);
            super.onBackPressed();
        }else finish();*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu=menu;
        sPref = getPreferences(MODE_PRIVATE);
        boolean notification = sPref.getBoolean(ConstantInterface.NOTIFICATIONS,true);
        if (notification){
            menu.getItem(2).setChecked(notification);
            boolean notifSound = sPref.getBoolean(ConstantInterface.NOTIFICATION_SOUND,true);
            menu.getItem(3).setChecked(notifSound);
        }else {
            menu.getItem(3).setCheckable(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()){
            case R.id.action_sign_in:
                startActivityForResult(new Intent(mCtx,LoginActivity.class)
                        .putExtra(ConstantInterface.ITENT_SIGN_IN,1),CREATE_ACOUNT);
                return true;
            case R.id.action_settings:

                coordinatingService.getIDValue(new OnDBReadCompleteListener<Long>() {
                    @Override
                    public void onComplete(Long type) {
                        if(null != type) {

                            setFragment(ConstantInterface.USER_INFO_FRAGMENT_TAG, type);
                        }
                    }

                    @Override
                    public void onFailtrue(Exception e) {

                    }
                });

                break;
            case R.id.action_sign_out:
                mAuthenticate.signOut();
                setVisible(true);
                //rvChatList.removeAllViewsInLayout();
                startActivityForResult(new Intent(mCtx,LoginActivity.class),CREATE_ACOUNT);
                break;
            case R.id.action_notifications_sound:
                boolean notifSoundPref;
                sPref = getPreferences(MODE_PRIVATE);
                notifSoundPref= sPref.getBoolean(ConstantInterface.NOTIFICATION_SOUND,true);
                if (!notifSoundPref){
                    notifSoundPref=true;
                }else if (notifSoundPref){
                    notifSoundPref=false;
                }
                item.setChecked(notifSoundPref);
                ed= sPref.edit();
                ed.putBoolean(ConstantInterface.NOTIFICATION_SOUND,notifSoundPref);
                ed.commit();
                break;
            case R.id.action_notifications:
                boolean notifPref;
                sPref = getPreferences(MODE_PRIVATE);
                notifPref= sPref.getBoolean(ConstantInterface.NOTIFICATIONS,true);
                if (!notifPref){
                    notifPref=true;
                }else if (notifPref){
                    notifPref=false;
                }
                item.setChecked(notifPref);
                menu.getItem(3).setCheckable(notifPref);
                ed= sPref.edit();
                ed.putBoolean(ConstantInterface.NOTIFICATIONS,notifPref);
                ed.commit();
                break;

        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void init(){
        h = new Handler();

        //initService();
    }

    private void initService(){
        if (!isConnected) {
            Intent intent=new Intent(this,CoordinatingService.class);
            startService(intent);
            serviceConnection();
            bindService(intent,sConn,BIND_AUTO_CREATE);
        }
    }

    private void initFragmens(){
        Log.d(ConstantInterface.LOG_TAG,"initFragments");
        fragmentSetter = new FragmentSetter(this);
        /*FragmentManager fm = getFragmentManager();
        listFragment = (ListFragment) fm.findFragmentByTag(LIST_FRAGMENT_TAG);
        roomFragment = (RoomFragment) fm.findFragmentByTag(ROOM_FRAGMENT_TAG);
        userInfoFragment = (UserInformationFragment) fm.findFragmentByTag(USER_INFO_FRAGMENT_TAG);
        roomInfoFragment = (RoomInformationFragment) fm.findFragmentByTag(ROOM_INFO_FRAGMENT_TAG);

        if (listFragment==null) {
            listFragment = new ListFragment();
        }
        if (roomFragment==null) {
            roomFragment = new RoomFragment();
        }
        if (userInfoFragment ==null){
            userInfoFragment = new UserInformationFragment();
        }

        if (roomInfoFragment == null){
            roomInfoFragment = new RoomInformationFragment();
        }*/
        uiFragment=findViewById(R.id.uiFragment);
    }

    private void initDrawler(){
        /*lvDrawer= findViewById(R.id.lvDrawer);
        dlMain=findViewById(R.id.dlMain);

        buttonsAdapter = new DrawerButtonsAdapter(this);

        lvDrawer.setAdapter(buttonsAdapter);
        lvDrawer.setOnItemClickListener(clickListener);

        drawerToggle = new ActionBarDrawerToggle(this,dlMain,toolbar,R.string.drawer_open,
                R.string.drawer_close);

        dlMain.addDrawerListener(drawerToggle);
        drawerToggle.syncState();*/
    }

    private void initToolbar(){
       /* if (mainToolbar == null) {
            mainToolbar = new Main_Toolbar(this);
        } else {
            mainToolbar.setmActivityAndResumeState(this);
        }
        toolbar = mainToolbar.getToolbar();*/
    }

    private void serviceConnection(){
        sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                coordinatingService =((CoordinatingService.ServiceBinder)service).getService();

                Log.d(ConstantInterface.LOG_TAG,"Service BINDED");
                isConnected=true;

                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(new Intent(ConstantInterface.SERVICE_BINDED));
                if (notificationRoom > 0)openRoom(notificationRoom);
                if(coordinatingService.isServiceReady()) beginningOfWork();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isConnected=false;
            }
        };
    }

    private ListView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            switch (position){
                case 0:
                    coordinatingService.getIDValue(new OnDBReadCompleteListener<Long>() {
                        @Override
                        public void onComplete(Long type) {
                            if(null!= type){
                                setFragment(ConstantInterface.USER_INFO_FRAGMENT_TAG,type);
                            }
                        }

                        @Override
                        public void onFailtrue(Exception e) {

                        }
                    });
                    break;
                case 1:
                    setFragment(ConstantInterface.LIST_FRAGMENT_TAG,1L);
                    break;
                case 2:
                    openFriendsList();
                    break;
                case 3:
                    openAppSettings();
                    break;
                case 4:
                    mAuthenticate.signOut();
                    startActivity(new Intent(mCtx,LoginActivity.class));
                    break;
            }
            dlMain.closeDrawer(lvDrawer);
        }
    };

    private void openAppSettings() {

    }

    private void openFriendsList() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == CREATE_ACOUNT){
            switch (resultCode){
                case RESULT_OK:
                    startUserSettingsActivity(ConstantInterface.INSTANCE_TYPE_CREATE_NEW
                            ,(RegistrationUserData) data.getParcelableExtra(ConstantInterface.RESULT));
                    break;
                case RESULT_CANCELED:
                    break;
            }
        }
        switch (resultCode){
            /*case RESULT_OK:
                isAutorised = true;

                //fragmentID=1;
                if (coordinatingService !=null) {
                    initFragmens();
                    initToolbar();
                    if (coordinatingService.getDisplayName()==null){
                        //fragmentID=3;
                        setFragment(userInfoFragment,USER_INFO_FRAGMENT_TAG,3);
                        userInfoFragment.setIdToDisplay(-1L);

                    }else {
                        setFragment(listFragment,LIST_FRAGMENT_TAG,1);
                    }
                } else {
                    initService();
                }
                break;*/
            case ConstantInterface.RESUL_NEW_ID_CREATED:
                initFragmens();
                initToolbar();

                fragmentID=3;

                coordinatingService.getIDValue(new OnDBReadCompleteListener<Long>() {
                    @Override
                    public void onComplete(Long type) {
                        setFragment(ConstantInterface.USER_INFO_FRAGMENT_TAG,type);
                    }

                    @Override
                    public void onFailtrue(Exception e) {

                    }
                });
                break;
            case RESULT_CANCELED:
                Toast.makeText(this, "Something wrong with authentication",
                        Toast.LENGTH_LONG).show();
                finish();
                break;
        }



    }

    public void setCurrentFragment(){
        Log.d(ConstantInterface.LOG_TAG,"fragmentID: " + fragmentID);
        if (currentFragmentID==-1)currentFragmentID=1;

        Log.d(ConstantInterface.LOG_TAG,"currentFragmentID: " + currentFragmentID);
               switch (currentFragmentID){
                   case 1:
                       setFragment(LIST_FRAGMENT_TAG,-1L);
                       break;
                   case 2:
                       setFragment(ROOM_FRAGMENT_TAG,-1L);
                       break;
               }

    }

    private void setFragment(String tag,long arg){
        fragmentSetter.setWithArgument(tag,arg);
        if (fragmentID!=null) currentFragmentID=fragmentID;
        fragmentID=fragmentSetter.getFragmentID();
        mainToolbar.update(fragmentID);

        /*if (fragment==listFragment){
            fragmentID=_fragmentID;
        }else if (fragment==roomFragment){
            fragmentID=_fragmentID;
        }*/
        int index = this.getFragmentManager().getBackStackEntryCount()-1;
        Log.e(ConstantInterface.LOG_TAG,"BackStack size is: " + index);
    }

    public CoordinatingService getBindedService(){
        //if ( == null)initService();
        return coordinatingService;
    }

    /*public UserInformationFragment getUserInfoFragment (){
        if (userInfoFragment == null) initFragmens();
        return userInfoFragment;
    }*/

    public void setUserInfoFragment(long _id){
        setFragment(ConstantInterface.USER_INFO_FRAGMENT_TAG,_id);
    }

    public void setRoomInfoFragment(final long _id){
        setFragment(ConstantInterface.ROOM_INFO_FRAGMENT_TAG,_id);
    }

    public Integer getFragmentID(){return fragmentID;}

    public long getRoomId(){return fragmentSetter.getRoomId();}

    @TargetApi(23)
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat
                .checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this
                    , android.Manifest.permission.READ_EXTERNAL_STORAGE) && !ActivityCompat
                    .shouldShowRequestPermissionRationale(this
                            , android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(this,new String[]
                                {android.Manifest.permission.READ_EXTERNAL_STORAGE
                                        , android.Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        ,ConstantInterface.PERMISSION_TO_READ_STORAGE_TAG);
            }else; //changeFragment();
        }else; //changeFragment();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode==ConstantInterface.PERMISSION_TO_READ_STORAGE_TAG){
            if (grantResults.length>0) {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED
                        && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                }else {
                    Toast.makeText(this,"there is no necessary permission",Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(this,"there is no necessary permission",Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    @Override
    protected void onDestroy(){
       if (isConnected)unbindService(sConn);

       //unregisterReceiver(signInReceiver);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (manager!=null)manager.isAppFocused(false);

        LocalBroadcastManager.getInstance(mCtx).unregisterReceiver(rcr);
        LocalBroadcastManager.getInstance(mCtx).unregisterReceiver(roomUserListUpdReceiver);
        LocalBroadcastManager.getInstance(mCtx).unregisterReceiver(roomSettingsUpdReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (manager!=null) manager.isAppFocused(true);
        rcr=new RoomCreatedReceiver();
        LocalBroadcastManager.getInstance(mCtx)
        .registerReceiver(rcr,new IntentFilter(ConstantInterface.CREATION_FINISHED));

        roomSettingsUpdReceiver = new RoomSettingsUpdReceiver();
        LocalBroadcastManager.getInstance(mCtx)
        .registerReceiver(roomSettingsUpdReceiver, new IntentFilter(ConstantInterface.ROOM_SETTINGS_CHANGE));

        roomUserListUpdReceiver = new RoomUserListUpdReceiver();
        LocalBroadcastManager.getInstance(mCtx)
        .registerReceiver(roomUserListUpdReceiver, new IntentFilter(ConstantInterface.ROOM_USER_LIST_CHANGE));
    }

    private void beginningOfWork(){
        manager= coordinatingService.getNotificationManager();
        rooms= coordinatingService.getRooms();
        initFragmens();
        initToolbar();
        initDrawler();
        if (isSvaedState && coordinatingService.getDisplayName()!=null) return;
        Log.d(ConstantInterface.LOG_TAG,"fragmentID: " + fragmentID + " currnetragmentID: " + currentFragmentID);
        if (coordinatingService.getDisplayName()==null){
            Log.d(ConstantInterface.LOG_TAG,"DisplayName null");
            setFragment(ConstantInterface.USER_INFO_FRAGMENT_TAG,-1L);

        }else if (fragmentID == currentFragmentID){
            setFragment(ConstantInterface.LIST_FRAGMENT_TAG,-1L);
        }else switch (fragmentID){
            case 1:
                setFragment(ConstantInterface.LIST_FRAGMENT_TAG,-1L);
                break;
            case 2:
                setFragment(ConstantInterface.ROOM_FRAGMENT_TAG,-1L);
                break;
            case 3:
                setFragment(ConstantInterface.USER_INFO_FRAGMENT_TAG,-1L);
                break;
            case 4:
                setFragment(ConstantInterface.ROOM_INFO_FRAGMENT_TAG,-1L);
                break;
            default:
                setFragment(ConstantInterface.LIST_FRAGMENT_TAG,-1L);
                break;

        }
    }

    public void openRoom(long idOfRoom) {
        setFragment(ConstantInterface.ROOM_FRAGMENT_TAG,idOfRoom);
        /*if (rooms==null)rooms= coordinatingService.getRooms();


        int position = -1;
        //Log.e(ConstantInterface.LOG_TAG,"  fds" + rooms.size());
        if (rooms.size()!=0) {
            for (int i=0;i<=rooms.size();i++){
                Room room=rooms.getInsatnce(i);
                if (idOfRoom == room.getRoomId()){
                    position=i;
                    break;
                }
            }
        }

        if (position!=-1){


        }else{
            Log.d(ConstantInterface.LOG_TAG,"MainActivity.openRoom , create new Room");
            Room room=new Room(this,idOfRoom,ConstantInterface.PUBLIC_ROOM, coordinatingService);
            rooms.add(room);
            //roomFragment.setRoom(room);
        }
*/
    }



    public class RoomSettingsUpdReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(ConstantInterface.UPDATED_ROOM_ID,-1);
            mainToolbar.updateAndDisplayNewRoomData(-1);
            fragmentSetter.updateAndDisplayNewData(ConstantInterface.LIST_FRAGMENT_TAG,-1L);
        }
    }

    public class RoomUserListUpdReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(ConstantInterface.UPDATED_ROOM_ID,-1L);
            mainToolbar.updateAndDisplayNewRoomData(id);
            fragmentSetter.updateAndDisplayNewData(ConstantInterface.ROOM_INFO_FRAGMENT_TAG,id);
        }
    }

    public class RoomCreatedReceiver extends BroadcastReceiver{

        private int readMessages=-1;
        private int readUsers=-1;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (-1!=intent.getIntExtra(ConstantInterface.RESULT_OF_READ_MESSAGES,-1))
            readMessages=intent.getIntExtra(ConstantInterface.RESULT_OF_READ_MESSAGES,-1);

            if (-1!=intent.getIntExtra(ConstantInterface.RESULT_OF_READ_USERS,-1))
            readUsers=intent.getIntExtra(ConstantInterface.RESULT_OF_READ_USERS,-1);

            Log.d(ConstantInterface.LOG_TAG,"RoomCreatedReceiver \n readMessages = "
                    + readMessages + " readUsers = " + readUsers);

            if (readUsers==1 && readMessages==1){
                setFragment(ConstantInterface.ROOM_FRAGMENT_TAG,-1L);
                readUsers=-1;
                readMessages=-1;
                return;
            }

            if (readMessages==0 || readUsers==0){
                Toast.makeText(context,"Read database failed",Toast.LENGTH_SHORT).show();
                readUsers=-1;
                readMessages=-1;
            }

            if(intent.getIntExtra(ConstantInterface.SERVICE_REDY_TO_WORK,-1)==1){
                Log.d(ConstantInterface.LOG_TAG,"ServiceReadyToWork");
                beginningOfWork();
            }

        }
    }

    public class SignInReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            /*if (intent.getBooleanExtra(ConstantInterface.RESULT,false)){
                Log.d(ConstantInterface.LOG_TAG,"SignInReceiver");
                setFragment(listFragment,LIST_FRAGMENT_TAG,1);
            }else{
                Toast.makeText(getBaseContext(),"Database Error",Toast.LENGTH_SHORT).show();
                finish();
            }*/
        }
    }

    /*@Override
    public void startImageActivity(Bitmap bitmap) {
        Intent intent=new Intent(this,ImageActivity.class);
        //intent.putExtra(ConstantInterface.INTENT_IMAGE,bitmap);
        startActivity(intent);
    }*/
}
