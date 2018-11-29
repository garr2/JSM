package com.garr.pavelbobrovko.notsimplechat.deprecated.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.garr.pavelbobrovko.notsimplechat.deprecated.APIConnectionService.CoordinatingService;
import com.pavelbobrovko.garr.domain.entity.RoomInfo;
import com.pavelbobrovko.garr.domain.entity.User;
import com.pavelbobrovko.garr.domain.utils.ConstantInterface;
import com.garr.pavelbobrovko.notsimplechat.presentation.screen.main.MainActivity;
import com.garr.pavelbobrovko.notsimplechat.R;

import java.util.HashMap;

/**
 * Created by garr on 24.12.2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Long[] listData;
    private MainActivity mainActivity;
    //private ListFragment listFragment;
    private HashMap<Long,RoomInfo> roomInfoList;
    private HashMap<Long,User> userInfoList;
    private CoordinatingService coordinatingService;
    private RequestOptions glideOptions;
    private long DATA_TYPE;

    public ListAdapter(Long[] _listData, MainActivity _mainActivity,final long _DATA_TYPE){
        mainActivity=_mainActivity;
        coordinatingService = mainActivity.getBindedService();
        listData=_listData;

        roomInfoList = coordinatingService.getRoomInfoList();
        userInfoList = coordinatingService.getUsersRef();

        Log.d (ConstantInterface.LOG_TAG,"ListAadapter userInfoList.size = " + userInfoList.size() + " listData.length = " + listData.length);


        glideOptions=new RequestOptions()
                .error(R.drawable.no_image)
                .override(120,120)
                .centerInside()
                .circleCrop();
        DATA_TYPE = _DATA_TYPE;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.header_of_chat_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

       ViewHolder vh = new ViewHolder(v);
       return vh;
    }

int counter = 1;
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.clearHeader();
        Log.d(ConstantInterface.LOG_TAG,"ListAdapter.onBindViewHolder count: " + counter++);

        if (DATA_TYPE == ConstantInterface.ROOM_LIST_TYPE){
            displayRoomTypeData(position,holder);
        }else if (DATA_TYPE == ConstantInterface.USER_LIST_TYPE){
            displayUserTypeData(position,holder);
        }

        holder.llListItem.setTag(position);
        holder.llListItem.setOnClickListener(onItemClick);
    }

    @Override
    public int getItemCount() {
        return listData.length;
    }

    private void displayRoomTypeData(int position, ViewHolder holder){
        final long roomID = listData[position];
        final RoomInfo roomInfo = roomInfoList.get(roomID);
        Log.d (ConstantInterface.LOG_TAG,"roomInfoList.size = " + roomInfoList.size());
        if (roomInfo == null){
            Log.d(ConstantInterface.LOG_TAG,"ListAdapter roomInfo null");
            //listFragment.getRoomInfo(roomID);
            coordinatingService.getRoomInfo(roomID);
        }else {
            Log.d(ConstantInterface.LOG_TAG,"ListAdapter roomInfo not null");
                if (roomInfo.getRoomName() != null) {
                    holder.tvName.setText(roomInfo.getRoomName());

                } else holder.tvName.setText(listData[position].toString());


                if (roomInfo.getRoomAvatarURL() != null) {
                    Glide
                            .with((Context) mainActivity)
                            .load(roomInfo.getRoomAvatarURL())
                            .apply(glideOptions)
                            .into(holder.ivAvatar);
                } else holder.ivAvatar.setImageResource(R.drawable.no_image);


                //holder.tvName.setText(listData[position].toString());
                //holder.ivAvatar.setImageResource(R.drawable.no_image);
        }
    }

    public void setDATA_TYPE (int _DATA_TYPE){
        DATA_TYPE = _DATA_TYPE;
    }

    private void displayUserTypeData(int position, ViewHolder holder){
        final long userID = listData[position];
        final User user = userInfoList.get(userID);
        Log.d (ConstantInterface.LOG_TAG,"userInfoList.size = " + userInfoList.size());
        if (user == null){
            Log.d(ConstantInterface.LOG_TAG,"ListAdapter user null");
            //listFragment.getRoomInfo(roomID);
            coordinatingService.getUserInformation(userID,null);
        }else {
            Log.d(ConstantInterface.LOG_TAG,"ListAdapter user name = " + user.getDisplayName() + " userAvatar = " + user.getAvatarURL());
            if (user.getDisplayName() != null) {
                holder.tvName.setText(user.getDisplayName());

            } else holder.tvName.setText(listData[position].toString());


            if (user.getAvatarURL() != null) {
                Glide
                        .with((Context) mainActivity)
                        .load(user.getAvatarURL())
                        .apply(glideOptions)
                        .into(holder.ivAvatar);
            } else holder.ivAvatar.setImageResource(R.drawable.no_image);

            //holder.tvName.setText(listData[position].toString());
            //holder.ivAvatar.setImageResource(R.drawable.no_image);
        }
    }

    private View.OnClickListener onItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           int position =(Integer) view.getTag();
           Long itemID= listData[position];
           mainActivity.openRoom(itemID);
        }
    };

    /*private RoomInfo getRoomInfo(Long roomID){

        for (HashMap map : roomInfoList){
            long key =(long) map.keySet().toArray()[0];
            if (roomID==key){
                RoomInfo roomInfo =(RoomInfo) map.getInsatnce(RoomInfo.class);
                Log.d(ConstantInterface.LOG_TAG,"roomInfo size: " + dataInfoList.size() + "key: " + key );
                return roomInfo;
            }
        }
        return null;
    }*/

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivAvatar;
        public TextView tvName;
        public TextView tvTime;
        public LinearLayout llListItem;

        private Context mCtx;

        public ViewHolder(View itemView) {
            super(itemView);

            if (mCtx==null)mCtx=itemView.getContext();

            ivAvatar=itemView.findViewById(R.id.ivAvatar);
            tvName=itemView.findViewById(R.id.tvName);
            tvTime=itemView.findViewById(R.id.tvTime);
            llListItem=itemView.findViewById(R.id.llChatItem);
            Log.d(ConstantInterface.LOG_TAG,"ListAdapter.ViewHolder");
        }

        public void clearHeader(){
            tvName.setText("");
            tvTime.setText("");
            ivAvatar.setImageBitmap(null);
        }
    }
}
