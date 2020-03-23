package com.example.ediaryphysicalactivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ediaryphysicalactivity.sHealthData.SHealthService;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataResolver.AggregateRequest;
import com.samsung.android.sdk.healthdata.HealthDataResolver.AggregateRequest.AggregateFunction;
import com.samsung.android.sdk.healthdata.HealthDataResolver.AggregateRequest.TimeGroupUnit;
import com.samsung.android.sdk.healthdata.HealthDataResolver.Filter;
import com.samsung.android.sdk.healthdata.HealthDataResolver.ReadRequest;
import com.samsung.android.sdk.healthdata.HealthDataResolver.SortOrder;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthDataUtil;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;

import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants.StepCount;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionKey;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionResult;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionType;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;


public class SHealthActivity extends AppCompatActivity {

    private static final String APP_TAG = "SHealth";

    private TextView tvSHealthData;
    private Button btnSHealthData;


    private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shealth);

        tvSHealthData = findViewById(R.id.tv_shealth);
        btnSHealthData = findViewById(R.id.btn_shealth);
        btnSHealthData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSHealthService();
            }
        });

    }


    private long getStartTimeOfToday() {
        Calendar today = Calendar.getInstance(TimeZone.getDefault());

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today.getTimeInMillis();
    }


    private void startSHealthService() {
        Log.i("SERVICE", "button clicked!");
        //startService(new Intent(this, SHealthService.class));

        Thread thread = new Thread() {
            @Override
            public void run() {
                startService(new Intent(getApplicationContext(), SHealthService.class));
            }
        };
        thread.start();

    }


}
