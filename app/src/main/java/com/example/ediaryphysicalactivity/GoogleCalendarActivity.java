package com.example.ediaryphysicalactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static java.lang.System.currentTimeMillis;

public class GoogleCalendarActivity extends AppCompatActivity {

    private static final int CALENDAR_READ_PERMISSION_CODE = 2345;
    private static final int CALENDAR_WRITE_PERMISSION_CODE = 6789;

    private TextView textViewInfo;
    private Button btnGetInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_calendar);

        textViewInfo = findViewById(R.id.text_view_google_calendar_info);

        btnGetInfo = findViewById(R.id.button_calendar_info);
        btnGetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addEvent();

                //showCalendarRecords();
                showCalendars();
                showCalendarEvents();

            }
        });

        checkPermission(Manifest.permission.READ_CALENDAR, CALENDAR_READ_PERMISSION_CODE);
        checkPermission(Manifest.permission.WRITE_CALENDAR, CALENDAR_WRITE_PERMISSION_CODE);

    }


    private void showCalendarEvents() {
        String[] projection = new String[] {
                CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.EVENT_LOCATION };

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY,0);
        startTime.set(Calendar.MINUTE,0);
        startTime.set(Calendar.SECOND, 0);

        Calendar endTime= Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY,0);
        endTime.set(Calendar.MINUTE,0);
        endTime.set(Calendar.SECOND, 0);
        endTime.add(Calendar.DATE, 1);

        String selection = "(( " + CalendarContract.Events.DTSTART + " >= "
                + startTime.getTimeInMillis() + " ) AND ( "
                + CalendarContract.Events.DTSTART + " <= "
                + endTime.getTimeInMillis()
                + " ) AND ( deleted != 1 ))";


        // Checking if permission is not granted --> run query
        if (ContextCompat
                .checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_DENIED) {

            Cursor cursor = getApplicationContext()
                    .getContentResolver()
                    .query(CalendarContract.Events.CONTENT_URI,
                            projection,
                            selection,
                            null,
                            null);

            String event;
            String eventDate;

            DateFormat dateFormatEvent = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            Date dateTypeEvent;

            List<String> events = new ArrayList<>();
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    event = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE));
                    eventDate = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTSTART));

                    Log.i("EVENT", "-----------------------------------------------------------------");

                    Log.i("Title", event);

                    dateTypeEvent = new Date(Long.parseLong(eventDate, 10) );
                    Log.i("Date", dateFormatEvent.format(dateTypeEvent)+"\n");

                    events.add(event);

                } while (cursor.moveToNext());
            }
        }
    }


    private  void showCalendarRecords() {

        // Projection array. Creating indices for this array instead of doing
        // dynamic lookups improves performance.
        final String[] EVENT_PROJECTION = new String[] {
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        final int PROJECTION_DISPLAY_NAME_INDEX = 2;
        final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

        Cursor cur = null;


        String accountName = "florian.meinhart@salzburgresearch.at";
        String accountType = "com.google";
        //String accountOwner = "florian.meinhart@salzburgresearch.at";

        // Run query
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        Log.i("URI", CalendarContract.Calendars.CONTENT_URI +"");
        //String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
        //        + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
        //        + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
        //String[] selectionArgs = new String[] {accountName, accountType, accountOwner};
        String[] selectionArgs = new String[] {accountName, accountType};


        // Checking if permission is not granted --> run query
        if (ContextCompat
                .checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_DENIED) {

            // Submit the query and get a Cursor object back.
            Log.i("CALENDAR:", "READ PERMISSION OK.");

            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);


            // Use the cursor to step through the returned records
            while (cur.moveToNext()) {
                long resCalID = 0;
                String resDisplayName = null;
                String resAccountName = null;
                String resOwnerName = null;

                // Get the field values
                resCalID = cur.getLong(PROJECTION_ID_INDEX);
                resDisplayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                resAccountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                resOwnerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

                // Do something with the values...
                Log.i("CALENDAR:", "ID: " + resCalID);
                Log.i("CALENDAR:", "Display Name: " + resDisplayName);
                Log.i("CALENDAR:", "Account Name: " + resAccountName);
                Log.i("CALENDAR:", "Owner Name: " + resOwnerName);

            }
        }
    }


    private  void showCalendars() {

        final String[] EVENT_PROJECTION = new String[] {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.IS_PRIMARY
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        final int PROJECTION_DISPLAY_NAME_INDEX = 2;
        final int PROJECTION_ACCOUNT_TYPE_INDEX = 3;
        final int PROJECTION_CALENDAR_ACCESS_LEVEL_TYPE_INDEX = 4;
        final int PROJECTION_CALENDAR_IS_PRIMARY_INDEX = 5;

        Cursor cur = null;


        // Run query
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;

        // Checking if permission is not granted --> run query
        if (ContextCompat
                .checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_DENIED) {
            // Submit the query and get a Cursor object back.
            Log.i("CALENDAR:", "READ PERMISSION OK.");
            cur = cr.query(uri, EVENT_PROJECTION, "", null, null);


            // Use the cursor to step through the returned records
            while (cur.moveToNext()) {
                long resCalID = 0;
                String resDisplayName = null;
                String resAccountName = null;
                String resAccountType = null;
                String resCalAccessLevel = null;
                Integer resIsPrimary = 0;

                // Get the field values
                resCalID = cur.getLong(PROJECTION_ID_INDEX);
                resDisplayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                resAccountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                resAccountType = cur.getString(PROJECTION_ACCOUNT_TYPE_INDEX);
                resCalAccessLevel = cur.getString(PROJECTION_CALENDAR_ACCESS_LEVEL_TYPE_INDEX);
                resIsPrimary = cur.getInt(PROJECTION_CALENDAR_IS_PRIMARY_INDEX);

                // Do something with the values...
                Log.i("CALENDAR", "ID: " + resCalID);
                Log.i("Info", "Display Name: " + resDisplayName);
                Log.i("Info", "Account Name: " + resAccountName);
                Log.i("Info", "Account Type: " + resAccountType);
                Log.i("Info", "Calendar Access Level: " + resCalAccessLevel);
                Log.i("Info", "Is Primary: " + resIsPrimary);

            }
        }
    }


    private void addEvent () {
        long calID = 9;

        /*
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2012, 9, 14, 7, 30);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2012, 9, 14, 8, 45);
        endMillis = endTime.getTimeInMillis();
        */

        TimeZone tz = TimeZone.getDefault();
        long startMillis = currentTimeMillis();
        long endMillis = startMillis + 60*60*1000;

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "Test Android Calendar Provider");
        values.put(CalendarContract.Events.DESCRIPTION, "Just a test...");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());


        if (ContextCompat
                .checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_DENIED) {

            Log.i("CALENDAR:", "WRITE PERMISSION OK.");

            // Insert new event.
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            // get the event ID that is the last element in the Uri
            long eventID = Long.parseLong(uri.getLastPathSegment());
            // ... do something with event ID

            Log.i("CALENDAR:", "Event added. ID: " + eventID);
            textViewInfo.setText("Event added. ID: " + eventID);

        }
    }






    // Function to check and request permission
    private void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                this,
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { permission },
                    requestCode);
        } else {
            Toast.makeText(this,
                    permission+" \nPermission already granted",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}
