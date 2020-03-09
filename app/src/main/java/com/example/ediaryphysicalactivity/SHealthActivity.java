package com.example.ediaryphysicalactivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    private HealthDataStore mStore;
    Set<PermissionKey> mKeys = new HashSet<PermissionKey>();

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
                getSHealthData();
            }
        });

        connect();
    }


    private final HealthDataStore.ConnectionListener mCntListener =
        new HealthDataStore.ConnectionListener() {

            @Override
            public void onConnected() {
                Log.d(APP_TAG, "Health data service is connected.");
                tvSHealthData.setText("Health data service is connected.");

            }

            @Override
            public void onConnectionFailed(HealthConnectionErrorResult error) {
                Log.d(APP_TAG, "Health data service is not available.");
                tvSHealthData.setText("Health data service is not available.");

                if (error.getErrorCode() == HealthConnectionErrorResult.OLD_VERSION_PLATFORM) {
                    // Show a message to the user to update Samsung Health
                    tvSHealthData.setText("Health data service is not available. Update Samsung Health");
                }
            }

            @Override
            public void onDisconnected() {
                Log.d(APP_TAG, "Health data service is disconnected.");
                tvSHealthData.setText("Health data service is disconnected.");
            }
        };

    public HealthDataStore connect() {
        // Connect to the health data store
        mStore = new HealthDataStore(this, mCntListener);
        try {
            mStore.connectService();
        } catch (Exception e) {
            Log.d(APP_TAG, "Connection fails.");
            e.printStackTrace();
        }

        return mStore;
    }


    public void requestPermission() {

        // Acquire permission
        HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);
        mKeys.add(new PermissionKey(HealthConstants.Exercise.HEALTH_DATA_TYPE, PermissionType.READ));
        mKeys.add(new PermissionKey(HealthConstants.Exercise.HEALTH_DATA_TYPE, PermissionType.WRITE));
        mKeys.add(new PermissionKey(StepCount.HEALTH_DATA_TYPE, PermissionType.READ));

        try {
            pmsManager.requestPermissions(mKeys, SHealthActivity.this).setResultListener(mPermissionListener);
        } catch (Exception e) {
            Log.d(APP_TAG, "requestPermissions() fails");
        }
    }


    private final HealthResultHolder.ResultListener<PermissionResult> mPermissionListener =
        new HealthResultHolder.ResultListener<PermissionResult>() {

            @Override
            public void onResult(PermissionResult result) {

                Map<PermissionKey, Boolean> resultMap = result.getResultMap();

                if (resultMap.values().contains(Boolean.FALSE)) {
                    Log.d(APP_TAG, "All required permissions are not granted.");
                } else {
                    Log.d(APP_TAG, "All required permissions are granted.");
                    //Access health data
                }
            }
        };


    private long getStartTimeOfToday() {
        Calendar today = Calendar.getInstance(TimeZone.getDefault());

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today.getTimeInMillis();
    }

    private void readTodayStepCount() {
        // Set time range from start time of today to the current time
        long startTime = getStartTimeOfToday();
        long endTime = startTime + ONE_DAY_IN_MILLIS;

        HealthDataResolver.ReadRequest request = new ReadRequest.Builder()
                .setDataType(HealthConstants.StepCount.HEALTH_DATA_TYPE)
                .setProperties(new String[]{HealthConstants.StepCount.COUNT})
                .setLocalTimeRange(
                        HealthConstants.StepCount.START_TIME,
                        HealthConstants.StepCount.TIME_OFFSET,
                        startTime,
                        endTime)
                .build();

        HealthDataResolver resolver = new HealthDataResolver(mStore, null);

        try {
            resolver.read(request).setResultListener(mListener);
        } catch (Exception e) {
            Log.d(APP_TAG, "Getting step count fails.");
        }
    }

    private final HealthResultHolder.ResultListener<HealthDataResolver.ReadResult> mListener =
        new HealthResultHolder.ResultListener<HealthDataResolver.ReadResult>() {
            @Override
            public void onResult(HealthDataResolver.ReadResult result) {
                int count = 0;
                try {
                    Iterator<HealthData> iterator = result.iterator();

                    if (iterator.hasNext()) {
                        HealthData data = iterator.next();
                        count += data.getInt(HealthConstants.StepCount.COUNT);

                        Log.e(APP_TAG, count +"");

                    }
                } finally {
                    tvSHealthData.setText(String.valueOf(count));
                    result.close();
                }
            }
        };





    // Getting data from Samsung Health App
    private void getSHealthData() {

        //requestPermission();

        readTodayStepCount();

        //String sHealthData = "No data available.";
        //tvSHealthData.setText(sHealthData);
    }


}
