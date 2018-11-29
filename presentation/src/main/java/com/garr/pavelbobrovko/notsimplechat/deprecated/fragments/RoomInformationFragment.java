package com.garr.pavelbobrovko.notsimplechat.deprecated.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.garr.pavelbobrovko.notsimplechat.deprecated.APIConnectionService.CoordinatingService;
import com.pavelbobrovko.garr.domain.entity.RoomInfo;
import com.pavelbobrovko.garr.domain.entity.User;
import com.pavelbobrovko.garr.domain.utils.ConstantInterface;
import com.garr.pavelbobrovko.notsimplechat.deprecated.adapters.ListAdapter;
import com.garr.pavelbobrovko.notsimplechat.presentation.screen.main.MainActivity;
import com.garr.pavelbobrovko.notsimplechat.R;

import java.util.HashMap;

public class RoomInformationFragment extends Fragment implements View.OnClickListener,DataUpdator{

    private Context mCtx;
    private MainActivity mActivity;
    private CoordinatingService coordinatingService;
    private RoomInfo roomInfo;
    private RequestOptions glideOptions;
    private Handler handler;
    private ServiceBindedBroadcastReceiver sbbr;

    private ImageView ivRoomImage;
    private ImageButton ibGalery;
    private Button btnAddMember;
    private Button btnSaveRoomSettings;
    private EditText etRoomName;

    private RecyclerView rvMembers;

    private RecyclerView.Adapter listAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private HashMap<Long,User> roomMembersInfo;
    private Long[] roomMembers;
    private Long roomId = -1L;
    private Uri photoUrlLocal;
    private boolean isETFocused = false;

    private final int GALLERY_REQUEST_CODE = 1;
    private final String KEY_IS_ET_FOCUSED = "isETFocusedKey";

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        getObjects(context);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        getObjects(activity);
    }

    private void getObjects(Context context){
        mCtx = context;
        mActivity=(MainActivity)context;
        if(coordinatingService ==null) coordinatingService =mActivity.getBindedService();

        if (Build.VERSION.SDK_INT>=23)
            mActivity.checkPermission();
    }

    @Override
    public void onStart(){
        Log.d(ConstantInterface.LOG_TAG,"onStart");
        super.onStart();
        LocalBroadcastManager.getInstance(mCtx)
                .registerReceiver(sbbr,new IntentFilter(ConstantInterface.SERVICE_BINDED));

    }

    @Override
    public void onStop(){
        super.onStop();
        if (sbbr!=null)LocalBroadcastManager.getInstance(mCtx).unregisterReceiver(sbbr);
        photoUrlLocal = null;
        isETFocused = etRoomName.isFocused();
        clearFocus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState){
        Log.e(ConstantInterface.LOG_TAG,"onCreateView");

        View view = inflater.inflate(R.layout.room_information_fragment,parent, false);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        if (savedInstanceState!=null){
            roomId = savedInstanceState.getLong(ConstantInterface.BUNDLE_KEY);
            isETFocused = savedInstanceState.getBoolean(KEY_IS_ET_FOCUSED);
        }else roomId = getArguments().getLong(ConstantInterface.BUNDLE_KEY);


        getObjects(mCtx);

        ivRoomImage = view.findViewById(R.id.ivRoomImage);
        ibGalery = view.findViewById(R.id.ibGallery);
        ibGalery.setOnClickListener(this);
        btnAddMember = view.findViewById(R.id.btnAddUser);
        btnAddMember.setOnClickListener(this);
        btnSaveRoomSettings = view.findViewById(R.id.btnSaveRoomSettings);
        btnSaveRoomSettings.setOnClickListener(this);
        etRoomName = view.findViewById(R.id.etRoomName);
        rvMembers = view.findViewById(R.id.rvRoomMembers);

        glideOptions=new RequestOptions()
                .error(R.drawable.no_image)
                .override(100,100)
                .centerInside()
                .circleCrop();

        handler = new Handler();

        sbbr = new ServiceBindedBroadcastReceiver();

        if (coordinatingService !=null) {
            getDataAndDisplay();
        }

        if (isETFocused)etRoomName.setFocusable(isETFocused);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ConstantInterface.BUNDLE_KEY,roomId);
        outState.putBoolean(KEY_IS_ET_FOCUSED,isETFocused);
    }

    private void clearFocus(){
        etRoomName.clearFocus();
    }

    private void getDataAndDisplay(){
        if (roomId != -1){
            setDataLists();
            if (photoUrlLocal==null) {
                displayRoomSettings();
            }
        }else {
            Toast.makeText(mCtx,"Error roomId is null",Toast.LENGTH_SHORT).show();
        }
    }

    public void setRoomIdToDisplay(long _id){
        roomId =_id;
    }

    private void displayRoomSettings(){
        etRoomName.setText(roomInfo.getRoomName());

        Glide
                .with(mActivity)
                .asBitmap()
                .load(roomInfo.getRoomAvatarURL())
                .apply(glideOptions)
                .into(ivRoomImage);

        setAdapter();
    }

    private void setAdapter(){
        mLayoutManager = new LinearLayoutManager(mCtx);
        rvMembers.setLayoutManager(mLayoutManager);
        listAdapter=new ListAdapter(roomMembers,mActivity,ConstantInterface.USER_LIST_TYPE);
        rvMembers.setAdapter(listAdapter);
    }

    private void setDataLists(){
        roomMembers = coordinatingService.getRoomUsersList(roomId);
        roomInfo = coordinatingService.getRoomInfo(roomId);
        roomMembersInfo = coordinatingService.getUsersRef();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibGallery:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                break;
            case R.id.btnSaveRoomSettings:
                if (!TextUtils.isEmpty(etRoomName.getText().toString())
                        && !TextUtils.equals(etRoomName.getText().toString(),"null")){
                    if (photoUrlLocal!=null || !TextUtils.equals(roomInfo.getRoomName(),etRoomName.getText().toString())){
                        RoomInfo info = new RoomInfo();
                        info.setRoomName(etRoomName.getText().toString());
                        if (photoUrlLocal!=null) {
                            info.setRoomAvatarURL(photoUrlLocal.toString());
                            coordinatingService.uploadNewRoomImage(roomId,info,photoUrlLocal);
                            photoUrlLocal = null;
                        }else {
                            coordinatingService.updateRoomSettings(roomId,info);
                        }
                    }
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(ConstantInterface.LOG_TAG,"onActivityResult");
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == GALLERY_REQUEST_CODE){

                photoUrlLocal = intent.getData();
                Log.d(ConstantInterface.LOG_TAG,"onActivityResult.GALLERY_REQUEST_CODE Url is : " + photoUrlLocal);
                displayNewImage();

            }
        }
    }

    private void displayNewImage() {

        Glide
                .with(mActivity)
                .load(photoUrlLocal)
                .apply(glideOptions)

                .into(ivRoomImage);
    }

    public void updateAndDisplayNewRoomData(long _id){
        if (_id == roomId){
           roomMembers = coordinatingService.getRoomUsersList(roomId);
           postAndNotifyAdapter();
        }


    }

    private void postAndNotifyAdapter(){
        handler.post(ListAdapterUpdater);
    }

    private Runnable ListAdapterUpdater = new Runnable() {
        @Override
        public void run() {
            if (!rvMembers.isComputingLayout()){
                listAdapter.notifyDataSetChanged();
            }else postAndNotifyAdapter();
        }
    };

    @Override
    public void updateAndDisplayNewData(@Nullable long arg) {
        if (arg!=-1L || arg == roomId){
            roomMembers = coordinatingService.getRoomUsersList(roomId);
            postAndNotifyAdapter();
        }
    }

    private class ServiceBindedBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            coordinatingService = mActivity.getBindedService();
           getDataAndDisplay();
        }
    }
}
