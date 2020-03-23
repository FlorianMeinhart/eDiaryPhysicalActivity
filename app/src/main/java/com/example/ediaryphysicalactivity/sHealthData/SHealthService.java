package com.example.ediaryphysicalactivity.sHealthData;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.ediaryphysicalactivity.AddEntryActivity;

public class SHealthService extends Service {

    private Context mContext;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Do something when service is started.
        Log.i("SERVICE", "started!");

        mContext = getApplicationContext();
        showToast("Hello, world!");

        Intent dialogIntent = new Intent(this, AddEntryActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);

        // Don't want this service to stay in memory, so stop it
        // immediately after doing what you wanted it to do.
        stopSelf();

        // Here you can return one of some different constants.
        // This one in particular means that if for some reason
        // this service is killed, we don't want to start it
        // again automatically
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        int restartTimeInSeconds = 10;
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (restartTimeInSeconds * 1000),
                PendingIntent.getService(this, 0, new Intent(this, SHealthService.class), 0)
        );
    }

    //Method to show toast
    void showToast(final String message) {
        if (mContext != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}