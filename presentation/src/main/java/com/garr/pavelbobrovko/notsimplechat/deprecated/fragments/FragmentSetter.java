package com.garr.pavelbobrovko.notsimplechat.deprecated.fragments;

import android.app.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.pavelbobrovko.garr.domain.utils.ConstantInterface;
import com.garr.pavelbobrovko.notsimplechat.R;

public class FragmentSetter {

    private ListFragment listFragment;
    private RoomFragment roomFragment;

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private Activity activity;

    private int currentFragmentID;
    private int fragmentID = -1;
    private long currenListFragmentArg = -1 ,currentRoomFragmentID = -1
            ,currentUserInfoID = -1,currentRoomInfoID = -1;
    private long fListDeffArg = 1, fRoomDeffArg = 0;

    private String visibleFragmentTag;

    private DataUpdator updator;

    public FragmentSetter(Context _mCtx){

        activity =(AppCompatActivity) _mCtx;
        fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
    }

    public void set(String tag){
       /* if (tag == ConstantInterface.ROOM_FRAGMENT_TAG){
            RoomFragment roomFragment = new RoomFragment();
            setFragment(roomFragment,ConstantInterface.ROOM_FRAGMENT_TAG,2);
        }*/
    }

    public void setWithArgument(String tag, long arg){
        Bundle bundle = new Bundle();
        bundle.putLong(ConstantInterface.BUNDLE_KEY,arg);

        switch (tag){
            case ConstantInterface.LIST_FRAGMENT_TAG:
                if (arg == -1L){
                    bundle.putLong( ConstantInterface.BUNDLE_KEY, fListDeffArg);
                }
                Fragment listFragment = new ListFragment();
                listFragment.setArguments(bundle);
                currenListFragmentArg = arg;
                updator =(DataUpdator) listFragment;
                setFragment(listFragment,ConstantInterface.LIST_FRAGMENT_TAG,1);
                break;
            case ConstantInterface.USER_INFO_FRAGMENT_TAG:
                Fragment userInformationFragment = new UserInformationFragment();
                userInformationFragment.setArguments(bundle);
                currentUserInfoID = arg;
                updator =(DataUpdator) userInformationFragment;
                setFragment(userInformationFragment,ConstantInterface.USER_INFO_FRAGMENT_TAG,3);
                break;
            case ConstantInterface.ROOM_INFO_FRAGMENT_TAG:
                Fragment roomInformationFragment = new RoomInformationFragment();
                roomInformationFragment.setArguments(bundle);
                currentRoomInfoID = arg;
                updator =(DataUpdator) roomInformationFragment;
                setFragment(roomInformationFragment,ConstantInterface.ROOM_INFO_FRAGMENT_TAG,4);
                break;
            case ConstantInterface.ROOM_FRAGMENT_TAG:
                if (arg == -1L){
                    bundle.putLong( ConstantInterface.BUNDLE_KEY, fRoomDeffArg);
                }
                Fragment roomFragment = new RoomFragment();
                roomFragment.setArguments(bundle);
                currentRoomFragmentID = arg;
                updator =(DataUpdator) roomFragment;
                setFragment(roomFragment,tag,2);
                break;
                default:
                    set(tag);
                    break;
        }
    }

    public int getFragmentID(){return fragmentID;}

    public int getCurrentragmentID(){return currentFragmentID;}

    private void setFragment(Fragment fragment, String tag, int _fragmentID){
        Log.d(ConstantInterface.LOG_TAG,"_fragmentID is: " + _fragmentID);
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.uiFragment,fragment,tag);
        if (_fragmentID!=fragmentID) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        if (fragmentID!=-1L) currentFragmentID=fragmentID;
        fragmentID=_fragmentID;
        //mainToolbar.update(fragmentID);

        /*if (fragment==listFragment){
            fragmentID=_fragmentID;
        }else if (fragment==roomFragment){
            fragmentID=_fragmentID;
        }*/
        int index = activity.getFragmentManager().getBackStackEntryCount()-1;
        Log.e(ConstantInterface.LOG_TAG,"BackStack size is: " + index);
    }

    public void updateAndDisplayNewData(String tag,long _id){
        if (visibleFragmentTag != null || visibleFragmentTag == tag){
            updator.updateAndDisplayNewData(_id);
        }
    }

    public long getRoomId(){return currentRoomFragmentID;}
}
