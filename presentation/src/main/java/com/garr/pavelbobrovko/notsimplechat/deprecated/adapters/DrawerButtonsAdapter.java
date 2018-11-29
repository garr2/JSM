package com.garr.pavelbobrovko.notsimplechat.deprecated.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.garr.pavelbobrovko.notsimplechat.R;

public class DrawerButtonsAdapter extends BaseAdapter {

    private LayoutInflater lInflater;
    private Context mCtx;

    private int[] images;

    public DrawerButtonsAdapter(Context _mCtx){
        mCtx=_mCtx;
        lInflater=(LayoutInflater)mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        images = new int[]{R.drawable.ic_action_account,R.drawable.ic_action_message,R.drawable.ic_action_friends
        ,R.drawable.ic_action_settings,R.drawable.ic_action_sign_out};
    }
    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;

        v = lInflater.inflate(R.layout.drawler_item,viewGroup,false);

        ImageView imageView = v.findViewById(R.id.ivItem);
        imageView.setImageResource(images[i]);
        return v;
    }
}
