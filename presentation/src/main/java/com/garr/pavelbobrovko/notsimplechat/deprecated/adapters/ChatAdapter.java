package com.garr.pavelbobrovko.notsimplechat.deprecated.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.pavelbobrovko.garr.domain.entity.Message;

import java.util.ArrayList;

/**
 * Created by garr on 21.05.2017.
 */

public class ChatAdapter extends BaseAdapter {

    //private ItemFactory factory;
    private ArrayList<Message> chatData;

    public ChatAdapter( ArrayList<Message> _chatData){
       // factory=ItemFactory.getInstance();
        chatData=_chatData;
    }

    @Override
    public int getCount() {
        return chatData.size();
    }

    @Override
    public Object getItem(int position) {
        return chatData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       View v = convertView;

       // return factory.getItem(v,getData(position),position,parent);
        return null;
    }

    private Message getData(int position){return ((Message)getItem(position));}

}
