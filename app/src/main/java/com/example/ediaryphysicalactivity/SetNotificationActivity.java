package com.example.ediaryphysicalactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SetNotificationActivity extends AppCompatActivity {

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    Button btnSetNotification;
    TextView textViewNotofiyTime;
    final Calendar myCalendar = Calendar.getInstance() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_notification);

        textViewNotofiyTime = findViewById(R.id.text_view_notify_time);

        btnSetNotification = findViewById(R.id.button_set_notification);
        btnSetNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNotification();
            }
        });
    }

    private void scheduleNotification (Notification notification , long delay) {

        Intent notificationIntent = new Intent( this, MyNotificationPublisher.class ) ;
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, 1 ) ;
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent.FLAG_UPDATE_CURRENT );

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification (String content) {

        Intent resultIntent = new Intent(this, AddEntryActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, default_notification_channel_id);
        builder.setContentTitle("Notification from eDiary");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground );
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.logo_lbi_transparent);

        //builder.setAutoCancel(false);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        return builder.build();
    }


    private void setNotification() {
        String myFormat = "dd.MM.yyyy HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault()) ;

        Calendar c = Calendar.getInstance(); //gives u calendar with current time
        c.add(Calendar.SECOND, 5); //add 5 seconds to calendar

        Date date = c.getTime();
        textViewNotofiyTime.setText(sdf.format(date.getTime())) ;

        String notificationMessage = "Time to add new entry!";

        Log.i("Msg:", date.getTime()+"");

        //scheduleNotification(getNotification(notificationMessage), date.getTime());
        scheduleNotification(getNotification(notificationMessage), 5000);
    }
}
