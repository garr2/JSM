package com.garr.pavelbobrovko.notsimplechat.deprecated.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

import com.garr.pavelbobrovko.notsimplechat.deprecated.APIConnectionService.CoordinatingService;
import com.pavelbobrovko.garr.domain.entity.Message;
import com.pavelbobrovko.garr.domain.utils.ConstantInterface;
import com.garr.pavelbobrovko.notsimplechat.presentation.screen.main.MainActivity;
import com.garr.pavelbobrovko.notsimplechat.deprecated.OnDBReadCompleteListener;
import com.garr.pavelbobrovko.notsimplechat.R;
import com.garr.pavelbobrovko.notsimplechat.deprecated.adapters.RecyclerAdapter;
import com.garr.pavelbobrovko.notsimplechat.deprecated.dataContainers.Room;

/**
 * Created by garr on 18.12.2017.
 */

public class RoomFragment extends Fragment implements View.OnClickListener,DataUpdator{

    private Context mCtx;
    private MainActivity mActivity;
    private Room room;
    private RoomDataUpdateReceiver roomDataUpdReceiver;
    private UsersRefUpdateReceiver usersRefUpdReceiver;
    private View v;
    private CoordinatingService coordinatingService;

    private ImageButton ibAdd;
    private Button btnSend;
    private EditText etMessage;

    private RecyclerView rvChatList;
    private RecyclerView.Adapter chatAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private long userID;
    private boolean isETFocused = false;

    private final String KEY_IS_ET_FOCUSED = "isETFocusedKey";

    public RoomFragment(){
        this.setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity){
        Log.d(ConstantInterface.LOG_TAG,"OnAttach");
        getObjects(activity);
        super.onAttach(activity);
    }

    @Override
    public void onAttach(Context context){
        getObjects(context);
        super.onAttach(context);
    }

    @Override
    public void onStart(){
        Log.d(ConstantInterface.LOG_TAG,"onStart");
        super.onStart();

        if (isETFocused)etMessage.setFocusable(isETFocused);
    }

    @Override
    public void onStop(){
        super.onStop();

        isETFocused = etMessage.isFocused();
        clearFocus();
    }

    private void getObjects(Context context){
        mCtx=context;
        mActivity=(MainActivity) mCtx;
        if(coordinatingService ==null) coordinatingService =mActivity.getBindedService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState){
        Log.d(ConstantInterface.LOG_TAG,"onCreateView");

        long roomId;
        if (savedInstanceState!=null){
            Log.d(ConstantInterface.LIST_FRAGMENT_TAG,"RoomFragment.onCreateView Bundle is not null");
            isETFocused = savedInstanceState.getBoolean(KEY_IS_ET_FOCUSED);
            roomId = savedInstanceState.getLong(ConstantInterface.BUNDLE_KEY);
            room = coordinatingService.getRoom(roomId);
        }else {
            roomId = getArguments().getLong(ConstantInterface.BUNDLE_KEY);
            room = coordinatingService.getRoom(roomId);
        }

        v = inflater.inflate(R.layout.room_fragment,parent,false);

        ibAdd=v.findViewById(R.id.ibAdd);
        ibAdd.setOnClickListener(this);
        btnSend=v.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        etMessage=v.findViewById(R.id.etMessage);
        rvChatList=v.findViewById(R.id.rvChatList);

        coordinatingService.getIDValue(new OnDBReadCompleteListener<Long>() {
            @Override
            public void onComplete(Long type) {
                userID = type;
            }

            @Override
            public void onFailtrue(Exception e) {

            }
        });

        roomDataUpdReceiver =new RoomDataUpdateReceiver();
        LocalBroadcastManager.getInstance(mCtx)
                .registerReceiver(roomDataUpdReceiver,new IntentFilter(ConstantInterface.ROOM_DATA_CHANGED));

        usersRefUpdReceiver = new UsersRefUpdateReceiver();
        LocalBroadcastManager.getInstance(mCtx)
                .registerReceiver(usersRefUpdReceiver, new IntentFilter(ConstantInterface.USER_REF_DATA_ADD));

        mLayoutManager = new LinearLayoutManager(mCtx);
        rvChatList.setLayoutManager(mLayoutManager);
        chatAdapter=new RecyclerAdapter(mCtx,room, coordinatingService);
        rvChatList.setAdapter(chatAdapter);
        //НАДО ДОБАВИТЬ if()
        if (rvChatList.getAdapter().getItemCount()>0)
        rvChatList.smoothScrollToPosition(rvChatList.getAdapter().getItemCount() - 1);

        return v;
    }

    @Override
    public void onClick(View view) {
        if (view==btnSend) {
            if (!TextUtils.isEmpty(etMessage.getText().toString())) {
                Message message = new Message(userID, etMessage.getText().toString(), "", "",-1L);
                room.sendMessage(message);
                etMessage.setText("");
                //etMessage.clearFocus();
                //Temp function!
                //InputMethodManager imm = (InputMethodManager) mCtx.getSystemService(Context.INPUT_METHOD_SERVICE);
               // imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


            }
        }
    }

    @Override
    public void onDestroy() {
        if (roomDataUpdReceiver != null)LocalBroadcastManager.getInstance(mCtx)
                .unregisterReceiver(roomDataUpdReceiver);
        if (usersRefUpdReceiver != null)LocalBroadcastManager.getInstance(mCtx)
                .unregisterReceiver(usersRefUpdReceiver);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_ET_FOCUSED,isETFocused);
        outState.putLong(ConstantInterface.BUNDLE_KEY,room.getRoomId());
    }

    private void clearFocus(){
        etMessage.clearFocus();
    }

    public void setRoom(Room _room){
        room=_room;
    }

    public long getRoomId(){ return room.getRoomId();}

    @Override
    public void updateAndDisplayNewData(@Nullable long arg) {

    }

    public class RoomDataUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
           long roomID= intent.getLongExtra(ConstantInterface.ROOM_NAME,-1);
            if (roomID == room.getRoomId()){
                chatAdapter.notifyDataSetChanged();
                rvChatList.smoothScrollToPosition(rvChatList.getAdapter().getItemCount() - 1);
                //slScroll.fullScroll(ScrollView.FOCUS_DOWN);
                Log.d(ConstantInterface.LOG_TAG,"Data changed");
            }
        }
    }

    private class UsersRefUpdateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            chatAdapter.notifyDataSetChanged();
        }
    }

}
