package com.example.ediaryphysicalactivity.sHealthData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SHealthReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentRec = new Intent(context, SHealthService.class);
        context.startService(intentRec);
    }
}
