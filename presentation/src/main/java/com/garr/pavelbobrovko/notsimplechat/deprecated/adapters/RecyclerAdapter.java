package com.garr.pavelbobrovko.notsimplechat.deprecated.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
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
import com.pavelbobrovko.garr.domain.entity.Message;
import com.pavelbobrovko.garr.domain.entity.User;
import com.pavelbobrovko.garr.domain.utils.ConstantInterface;
import com.garr.pavelbobrovko.notsimplechat.deprecated.ImageActivity;
import com.garr.pavelbobrovko.notsimplechat.presentation.screen.main.MainActivity;
import com.garr.pavelbobrovko.notsimplechat.deprecated.OnDBReadCompleteListener;
import com.garr.pavelbobrovko.notsimplechat.R;
import com.garr.pavelbobrovko.notsimplechat.deprecated.dataContainers.Room;



import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by garr on 14.10.2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private final int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;
    private final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;

    private Room room;
    private MainActivity mActivity;
    private Context mCtx;
    private CoordinatingService coordinatingService;
    private ArrayList<Message> arrayData;
    private HashMap usersRef;

    private LinearLayout.LayoutParams lTextParams;
    private LinearLayout.LayoutParams lImageParams;
    //private User displayUser;
    //private User tempUser;
    //private FullUserInfo user=new FullUserInfo();
    private RequestOptions glideOptions;

    private final long MINUTES=60000;
    private final long HOURS=3600000;
    private final long DAY_LENGTH=86400000;

    public RecyclerAdapter(Context _mCtx, Room _room, CoordinatingService _coordinatingService) {
        Log.d(ConstantInterface.LOG_TAG,"RecyclerAdapter");
        room=_room;
        coordinatingService = _coordinatingService;
        //displayUser =coordinatingService.getDisplayUser();
        arrayData=room.getArrayData();
        usersRef= coordinatingService.getUsersRef();
        mCtx=_mCtx;
        mActivity = (MainActivity)mCtx;
        Log.d(ConstantInterface.LOG_TAG,"RecyclerAdapter " + "data size: " + arrayData.size() + " ref size: " + usersRef.size()) ;

        lTextParams =new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
        lTextParams.setMargins(10,10,0,0);

        lImageParams=new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
        lImageParams.setMargins(10,0,10,0);
        lImageParams.topMargin-=80;
        lImageParams.bottomMargin-=80;

        glideOptions=new RequestOptions()
                .error(R.drawable.no_image)
                .override(120,120)
                .circleCrop()
                .centerInside();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(ConstantInterface.LOG_TAG,"onCreateViewHolder");
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.header_of_chat_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(ConstantInterface.LOG_TAG,"onBindViewHolder, position: " + position);
        Message data=arrayData.get(position);
        User user=getUserInfo(data.getUserId());
        holder.clearHeader();

        if(user==null){
            holder.tvName.setText("User name not found");
            holder.ivAvatar.setImageResource(R.drawable.no_image);
        }else {
            holder.tvName.setText(user.getDisplayName());

            if (user.getAvatarURL()!= null) {
                //Log.d(ConstantInterface.LOG_TAG,"User avatar is not null");

                Glide
                        .with(mCtx)
                        .load(user.getAvatarURL())
                        .apply(glideOptions)
                        .into(holder.ivAvatar);
            /*if (user.getAvatar()!= null){
                Log.d(ConstantInterface.LOG_TAG,"User ImageView is not null");
                holder.ivAvatar=user.getAvatar();
            }else {
                Log.d(ConstantInterface.LOG_TAG,"User ImageView is null");
                user.setAvatar(room.getUserAvatar(holder.ivAvatar,user.getAvatarURL()));

            }*/
                //coordinatingService.getUserAvatar(holder.ivAvatar,user.getAvatarURL());
            } else {
                //Log.d(ConstantInterface.LOG_TAG,"User avatar is null");
                holder.ivAvatar.setImageResource(R.drawable.no_image);}
        }


        holder.tvTime.setText(longToTime(data.getTime()));
        holder.llMultimediaLayout.removeAllViews();

        if (data.getMessage()!=null){

            TextView message = holder.addTextView();
            message.setTextSize(22f);
            message.setText(data.getMessage());
            Linkify.addLinks(message,Linkify.ALL);
            /*if (TextUtils.equals(user.getDisplayName(),displayUser.getDisplayName())){
                message.setBackgroundResource(R.color.colorAccent);
            }*/
            holder.llMultimediaLayout.addView(message,lTextParams);
        }

        holder.vAvatarListener.setTag(data.getUserId());
        holder.vAvatarListener.setOnClickListener(onAvatarClickListener);

        /*if (data.images!=null&& !TextUtils.isEmpty(data.images)) {
            String[] splitedImages=data.images.split(";");

            for (String image : splitedImages){
                ImageView ivImage=holder.addImageView();
                ivImage.setImageBitmap(coordinatingService.getDownloadedBitmap(image));
                ivImage.setOnClickListener(onImageClick);
                holder.llMultimediaLayout.addView(ivImage, lImageParams);
            }
        }*/

        //logMemory();

    }

    private User getUserInfo(final long userID) {
        User user =(User) usersRef.get(userID);
        if (user!= null)return user;

        user=getNewUser(userID);

       // usersRef.add(newUser);
        return user;
    }

    private User getNewUser(final long userID){
        final User[] user = new User[1];
        coordinatingService.getUserInformation(userID, new OnDBReadCompleteListener<User>() {
            @Override
            public void onComplete(User type) {
                 user[0] = type;
                if (user[0] ==null){
                    user[0] =new User();
                    user[0].setDisplayName("noName");
                }
            }

            @Override
            public void onFailtrue(Exception e) {
                e.printStackTrace();
            }
        });


        return user[0];
    }

    private void logMemory() {
        Log.i(ConstantInterface.LOG_TAG, String.format("Total memory = %s",
                (int) (Runtime.getRuntime().totalMemory() / 1024)));
    }

    private View.OnClickListener onImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent =new Intent(mCtx,ImageActivity.class);
            ImageView iv =(ImageView) v;
            Bitmap bitmap = ((BitmapDrawable)iv.getDrawable()).getBitmap();
            //mainListener.startImageActivity(bitmap);
            //intent.putExtra(ConstantInterface.INTENT_IMAGE,bitmap);
            Activity activity=(Activity) mCtx;
            activity.startActivity(intent);
        }
    };

    private View.OnClickListener onAvatarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            long id =(long) view.getTag();
            mActivity.setUserInfoFragment(id);

        }
    };

    @Override
    public int getItemCount() {
        return arrayData.size();
    }

    private String longToTime(long difTime){
        Log.d(ConstantInterface.LOG_TAG,"difTime = " + difTime);
        String strTime;
        long _difTime=difTime%DAY_LENGTH;
        long hourDiff=_difTime/HOURS+3;     //Получаем часы
        long minDiff=_difTime%HOURS;      //Получаем минуты в милисекундах остатком деления
        minDiff/=MINUTES;            //Получаем минуты
        if (minDiff<=9){  //составляем строку для Toast
            strTime=String.valueOf(hourDiff + ":0" + minDiff);
        }else {
            strTime=String.valueOf(hourDiff + ":" + minDiff);}
        return strTime;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivAvatar;
        public TextView tvName;
        public TextView tvTime;
        public LinearLayout llMultimediaLayout;
        public View vAvatarListener;

        private Context mCtx;

        public ViewHolder(View itemView) {
            super(itemView);

            if (mCtx==null)mCtx=itemView.getContext();

            ivAvatar=itemView.findViewById(R.id.ivAvatar);
            tvName=itemView.findViewById(R.id.tvName);
            tvTime=itemView.findViewById(R.id.tvTime);
            llMultimediaLayout=itemView.findViewById(R.id.llMultimediaLayout);
            vAvatarListener = itemView.findViewById(R.id.vAvatarListener);
        }

        public TextView addTextView(){
            return new TextView(mCtx);
        }

        public ImageView addImageView(){
            return new ImageView(mCtx);
        }

        public View addSoundItem(){
            return new View(mCtx);
        }

        public void clearHeader(){
            tvName.setText("");
            tvTime.setText("");
            ivAvatar.setImageBitmap(null);
        }
    }
}
