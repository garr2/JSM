package com.garr.pavelbobrovko.notsimplechat.deprecated;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.garr.pavelbobrovko.notsimplechat.deprecated.APIConnectionService.CoordinatingService;

public class ChatReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,CoordinatingService.class));
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
