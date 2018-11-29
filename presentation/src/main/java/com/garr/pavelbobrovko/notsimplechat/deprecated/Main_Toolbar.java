package com.garr.pavelbobrovko.notsimplechat.deprecated;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.garr.pavelbobrovko.notsimplechat.deprecated.APIConnectionService.CoordinatingService;
import com.garr.pavelbobrovko.notsimplechat.R;
import com.pavelbobrovko.garr.domain.entity.RoomInfo;
import com.garr.pavelbobrovko.notsimplechat.presentation.screen.main.MainActivity;
import com.pavelbobrovko.garr.domain.utils.ConstantInterface;

public class Main_Toolbar implements Parcelable{

    private MainActivity mActivity;
    private CoordinatingService coordinatingService;
   // private Integer fragmentID;
    private Toolbar mainToolbar;
    private RequestOptions glideOptions;
    private int currentFragmentId;
    private long roomId = -1L;
    private long userListSize;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private ImageView ivToolbarImage;
    private ConstraintLayout clToolbatLayout;

    public Main_Toolbar (MainActivity _mActivity){
        mActivity = _mActivity;
        //fragmentID = _fragmentID;
        coordinatingService = mActivity.getBindedService();
        init();
    }

    public Main_Toolbar (@NonNull Parcel in){
        currentFragmentId = in.readInt();
        roomId = in.readLong();
        userListSize = in.readLong();
    }



    public void setmActivityAndResumeState(MainActivity _mActivity){
        mActivity = _mActivity;
        coordinatingService = mActivity.getBindedService();
        init();
        updateToolbar(currentFragmentId);
    }

    private void init (){
        mainToolbar=mActivity.findViewById(R.id.toolbar);
        mActivity.setSupportActionBar(mainToolbar);//!!!!

        tvTitle = mActivity.findViewById(R.id.tvToolbarTitle);
        tvSubtitle = mActivity.findViewById(R.id.tvToolbarSubtitle);
        ivToolbarImage = mActivity.findViewById(R.id.ivToolbarImage);
        clToolbatLayout = mActivity.findViewById(R.id.clToolbarLayout);
        clToolbatLayout.setOnClickListener(toolbarListener);

        glideOptions=new RequestOptions()
                .error(R.drawable.no_image)
                .override(50,50)
                .centerInside()
                .circleCrop();
    }

    public Toolbar getToolbar(){return mainToolbar;}

    public void update(int fragmentID){
        Log.d(ConstantInterface.LOG_TAG,"MainToolbar.update");
        currentFragmentId = fragmentID;
        updateToolbar(fragmentID);
    }

    public void updateAndDisplayNewRoomData(@Nullable final long _id){

        if (roomId == _id){
           userListSize = coordinatingService.getRoomUsersListSize(roomId);
        }
        if (currentFragmentId == 2){
            setRoomType();
        }
    }

    private void updateToolbar(int fragmentID){
        Log.d(ConstantInterface.LOG_TAG,"MainToolbar.updateToolbar fragmentId = " + fragmentID);
        if (fragmentID!=-1){
            switch (fragmentID){
                case 1:
                case 3:
                    setDisplayNameType();
                    break;
                case 2:
                case 4:
                    setRoomType();
                    break;
            }
        }else Log.d(ConstantInterface.LOG_TAG,"MainToolbar fragmentID is null");
    }

    private void setDisplayNameType(){
        Log.d(ConstantInterface.LOG_TAG,"MainToolbar.setDisplayNameType");
        String displayName = coordinatingService.getDisplayName();
        Log.d(ConstantInterface.LOG_TAG,"MainToolbar displayName is: " + displayName);
        tvTitle.setText(coordinatingService.getDisplayName());
        tvSubtitle.setVisibility(View.GONE);

        Glide
                .with(mActivity)
                .load(coordinatingService.getUserAvatarUrl())
                .apply(glideOptions)
                .into(ivToolbarImage);
    }

    private void setRoomType(){
        roomId = mActivity.getRoomId();
        RoomInfo info = coordinatingService.getRoomInfo(roomId);
        userListSize = coordinatingService.getRoomUsersListSize(roomId);
        tvTitle.setText(info.getRoomName());
        tvTitle.setTag(roomId);
        tvSubtitle.setVisibility(View.VISIBLE);
        tvSubtitle.setText(String.valueOf(userListSize) + "учасник");

        Glide
                .with(mActivity)
                .load(info.getRoomAvatarURL())
                .apply(glideOptions)
                .into(ivToolbarImage);
    }

    private View.OnClickListener toolbarListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(ConstantInterface.LOG_TAG,"Main_ToolBar.toolbarListener");
           int fragmentID = mActivity.getFragmentID();
           switch (fragmentID){
               case 1:
                   coordinatingService.getIDValue(new OnDBReadCompleteListener<Long>() {
                       @Override
                       public void onComplete(Long type) {
                           mActivity.setUserInfoFragment(type);
                       }

                       @Override
                       public void onFailtrue(Exception e) {

                       }
                   });

                   break;
               case 2:
                   mActivity.setRoomInfoFragment((long)tvTitle.getTag());
                   break;
           }
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(currentFragmentId);
        parcel.writeLong(roomId);
        parcel.writeLong(userListSize);
    }

    public static final Creator<Main_Toolbar> CREATOR = new Creator<Main_Toolbar>() {
        @Override
        public Main_Toolbar createFromParcel(Parcel in) {
            return new Main_Toolbar(in);
        }

        @Override
        public Main_Toolbar[] newArray(int size) {
            return new Main_Toolbar[size];
        }
    };
}
