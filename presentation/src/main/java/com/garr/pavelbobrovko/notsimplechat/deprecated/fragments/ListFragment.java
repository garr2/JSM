package com.garr.pavelbobrovko.notsimplechat.deprecated.fragments;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.garr.pavelbobrovko.notsimplechat.deprecated.APIConnectionService.CoordinatingService;
import com.pavelbobrovko.garr.domain.utils.ConstantInterface;
import com.garr.pavelbobrovko.notsimplechat.deprecated.adapters.ListAdapter;
import com.garr.pavelbobrovko.notsimplechat.presentation.screen.main.MainActivity;
import com.garr.pavelbobrovko.notsimplechat.R;

/**
 * Created by garr on 23.12.2017.
 */

public class ListFragment extends Fragment implements View.OnClickListener,DataUpdator {

    private Context mCtx;
    private Handler handler;
    private MainActivity mActivity;
    private RoomInfoReceiver receiver;
    private ServiceBindedBroadcastReceiver sbbr;
    private CoordinatingService coordinatingService;
    private View v=null;
   // private HashMap<Long,RoomInfo> roomInfoList;

    private RecyclerView rvList;

    private RecyclerView.Adapter listAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Long[] userRooms,userFriends;
    private long listType;

    public ListFragment(){
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

    private void getObjects(Context context){
        mCtx=context;
        mActivity=(MainActivity) mCtx;
        coordinatingService =mActivity.getBindedService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState){

        if (savedInstanceState!=null){
            listType = savedInstanceState.getLong(ConstantInterface.BUNDLE_KEY);
        }else listType = getArguments().getLong(ConstantInterface.BUNDLE_KEY);
        v = inflater.inflate(R.layout.list_fragment,parent,false);

        rvList =v.findViewById(R.id.rvList);

        handler = new Handler();

        receiver = new RoomInfoReceiver();
        LocalBroadcastManager.getInstance(mCtx)
                .registerReceiver(receiver,new IntentFilter(ConstantInterface.ROOM_INFORMATION_IS_AVAILABLE));

        sbbr = new ServiceBindedBroadcastReceiver();
        LocalBroadcastManager.getInstance(mCtx)
                .registerReceiver(sbbr,new IntentFilter(ConstantInterface.SERVICE_BINDED));

        if (coordinatingService != null) {
            setUserLists();
            setAdapter();
        }

        return v;
    }

    @Override
    public void onDestroy(){
       if (receiver != null)LocalBroadcastManager.getInstance(mCtx).unregisterReceiver(receiver);
       if (sbbr != null)LocalBroadcastManager.getInstance(mCtx).unregisterReceiver(sbbr);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ConstantInterface.BUNDLE_KEY,listType);
    }

    private void setUserLists(){
            //roomInfoList = coordinatingService.getRoomInfoList();
            userRooms= coordinatingService.getUserRoomsAll();
            userFriends= coordinatingService.getUserFriends();
    }

    private void setAdapter(){
        mLayoutManager = new LinearLayoutManager(mCtx);
        rvList.setLayoutManager(mLayoutManager);
        if (listType == -1L)listType = ConstantInterface.ROOM_LIST_TYPE;
        listAdapter=new ListAdapter(userRooms,mActivity,listType);
        rvList.setAdapter(listAdapter);
    }
int counter = 1;
    public void getRoomInfo(final long _roomID){
        Log.d(ConstantInterface.LOG_TAG,"ListFragment.getRoomInfo count: " + counter++);
        coordinatingService.getRoomInfo(_roomID);
    }

    private void postAndNotifyUserLists(){handler.post(userListsUpdater);}

    private Runnable userListsUpdater = new Runnable() {
        @Override
        public void run() {
            if (mActivity.getBindedService() != null){
                coordinatingService = mActivity.getBindedService();
                setUserLists();
            }else handler.postDelayed(userListsUpdater,100);
        }
    };

    private void postAndNotifyAdapter(){
        handler.post(ListAdapterUpdater);
    }

    private Runnable ListAdapterUpdater = new Runnable() {
        @Override
        public void run() {
            if (!rvList.isComputingLayout()){
                listAdapter.notifyDataSetChanged();
            }else postAndNotifyAdapter();
        }
    };

    @Override
    public void onClick(View view) {

    }

    @Override
    public void updateAndDisplayNewData(@Nullable long arg) {
        postAndNotifyAdapter();
    }

    private class RoomInfoReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            postAndNotifyAdapter();
        }
    }

    private class ServiceBindedBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            coordinatingService = mActivity.getBindedService();
            setUserLists();
            setAdapter();
        }
    }
}
