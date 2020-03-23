package com.example.ediaryphysicalactivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthConstants.Exercise;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthDataUtil;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class SHealthActivityStepCount extends AppCompatActivity {

    private static final String APP_TAG = SHealthActivityStepCount.class.getSimpleName();
    private static SHealthActivityStepCount mInstance = null;
    private HealthDataStore mStore;
    private HealthConnectionErrorResult mConnError;
    private Set<HealthPermissionManager.PermissionKey> mKeySet;

    SimpleDateFormat dateFormatter;

    private TextView tvSHealthStepCount;
    private TextView tvSHealthInfo;
    private Button btnSHealthStepCount;
    private NumberPicker npDaysStepCount;

    private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shealth_step_count);

        dateFormatter = new SimpleDateFormat("dd.MM.yyyy  HH:mm:SSS");

        tvSHealthStepCount = findViewById(R.id.tv_shealth_sc);
        tvSHealthInfo = findViewById(R.id.tv_shealth_info);
        btnSHealthStepCount = findViewById(R.id.btn_shealth_sc);
        btnSHealthStepCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndReadData();
            }
        });
        // disable Button (set enabled if connected)
        btnSHealthStepCount.setEnabled(false);


        npDaysStepCount = findViewById(R.id.np_shealth_last_days);
        npDaysStepCount.setMinValue(0);
        npDaysStepCount.setMaxValue(50);
        npDaysStepCount.setOnValueChangedListener(npOnValueChangeListener);


        mInstance = this;
        mKeySet = new HashSet<>();
        mKeySet.add(new HealthPermissionManager.PermissionKey(
                HealthConstants.StepCount.HEALTH_DATA_TYPE,
                HealthPermissionManager.PermissionType.READ));
        mKeySet.add(new HealthPermissionManager.PermissionKey(
                HealthConstants.HeartRate.HEALTH_DATA_TYPE,
                HealthPermissionManager.PermissionType.READ));
        // Create a HealthDataStore instance and set its listener
        mStore = new HealthDataStore(this, mConnectionListener);
        // Request the connection to the health data store
        mStore.connectService();
    }


    @Override
    public void onDestroy() {
        mStore.disconnectService();
        super.onDestroy();
    }

    private final NumberPicker.OnValueChangeListener npOnValueChangeListener =
            new NumberPicker.OnValueChangeListener(){
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    Toast.makeText(SHealthActivityStepCount.this,
                            "selected number "+numberPicker.getValue(), Toast.LENGTH_SHORT);
                }
            };


    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {

        @Override
        public void onConnected() {
            Log.d(APP_TAG, "Health data service is connected.");
            tvSHealthInfo.setText("Health data service is connected.");


            // enable Button if connected
            btnSHealthStepCount.setEnabled(true);

            //checkPermissionAndReadStepCount();
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            Log.d(APP_TAG, "Health data service is not available.");
            tvSHealthInfo.setText("Health data service is not available.");
            showConnectionFailureDialog(error);
        }

        @Override
        public void onDisconnected() {
            Log.d(APP_TAG, "Health data service is disconnected.");
            tvSHealthInfo.setText("Health data service is disconnected.");
        }
    };


    private final HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> mPermissionListener =
            new HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {

                @Override
                public void onResult(HealthPermissionManager.PermissionResult result) {
                    Log.d(APP_TAG, "Permission callback is received.");
                    tvSHealthInfo.setText("Permission callback is received.");
                    Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = result.getResultMap();

                    if (resultMap.containsValue(Boolean.FALSE)) {
                        Log.i(APP_TAG, "Permission Request failed");
                        tvSHealthInfo.setText("Permission Request failed");
                    } else {
                        // Get the current step count and display it
                        //readStepCount();
                        readHeartRate();
                    }
                }
            };


    private void checkPermissionAndReadData() {
        HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);

        try {
            // Check whether the permissions that this application needs are acquired
            Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(mKeySet);

            if (resultMap.containsValue(Boolean.FALSE)) {
                // Request the permission for reading step counts if it is not acquired
                pmsManager.requestPermissions(mKeySet, SHealthActivityStepCount.this).setResultListener(mPermissionListener);
            } else {
                //readStepCount();
                readHeartRate();
            }
        } catch (Exception e) {
            Log.e(APP_TAG, e.getClass().getName() + " - " + e.getMessage());
            Log.e(APP_TAG, "Permission setting fails.");
            tvSHealthInfo.setText("Permission setting fails.");
        }
    }


    private void readStepCount() {
        Log.i(APP_TAG, "Query step data");
        tvSHealthInfo.setText("Query step data");
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);

        // Set time range from start time of today to the current time
        long startTime = getStartTimeOfToday();
        long endTime = startTime + ONE_DAY_IN_MILLIS;

        if (npDaysStepCount.getValue() != 0) {
            startTime -= ONE_DAY_IN_MILLIS * npDaysStepCount.getValue();
        }

        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.StepCount.HEALTH_DATA_TYPE)
                .setProperties(new String[] {
                        HealthConstants.StepCount.COUNT,
                        HealthConstants.StepCount.START_TIME,
                        HealthConstants.StepCount.END_TIME
                })
                .setLocalTimeRange(
                        HealthConstants.StepCount.START_TIME,
                        HealthConstants.StepCount.TIME_OFFSET,
                        startTime,
                        endTime)
                .build();

        try {
            resolver.read(request).setResultListener(mListenerSteps);
        } catch (Exception e) {
            Log.e(SHealthActivityStepCount.APP_TAG, "Getting step count fails.", e);
            tvSHealthInfo.setText("Getting step count fails.");
        }
    }


    private final HealthResultHolder.ResultListener<HealthDataResolver.ReadResult> mListenerSteps =
            new HealthResultHolder.ResultListener<HealthDataResolver.ReadResult>() {
                @Override
                public void onResult(HealthDataResolver.ReadResult result){
                    int ii = 0;
                    int count = 0;
                    String startTime;
                    String endTime;

                    try {
                        for (HealthData data : result) {
                            count += data.getInt(HealthConstants.StepCount.COUNT);
                            startTime = data.getString(HealthConstants.StepCount.START_TIME);
                            endTime = data.getString(HealthConstants.StepCount.END_TIME);
                            Log.i(APP_TAG, "Count: " + count
                                    + "\t\tStart time: " + ms2str(startTime)
                                    + "\t\tEnd time: " + ms2str(endTime));
                            ii++;
                        }
                    } finally {
                        tvSHealthStepCount.setText(Integer.toString(count));
                        tvSHealthStepCount.setTextSize(24);
                        Log.i(APP_TAG, "Number of loaded data points (steps): " + ii);
                        result.close();
                    }
                }
            };


    private void readHeartRate() {
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);

        // Set time range from start time of today to the current time
        long startTime = getStartTimeOfToday();
        long endTime = startTime + ONE_DAY_IN_MILLIS;

        if (npDaysStepCount.getValue() != 0) {
            startTime -= ONE_DAY_IN_MILLIS * npDaysStepCount.getValue();
        }

        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.HeartRate.HEALTH_DATA_TYPE)
                .setProperties(new String[] {
                        HealthConstants.HeartRate.HEART_BEAT_COUNT,
                        HealthConstants.HeartRate.HEART_RATE,
                        HealthConstants.HeartRate.BINNING_DATA,
                        HealthConstants.HeartRate.START_TIME,
                        HealthConstants.HeartRate.END_TIME
                })
                .setLocalTimeRange(
                        HealthConstants.HeartRate.START_TIME,
                        HealthConstants.HeartRate.TIME_OFFSET,
                        startTime,
                        endTime)
                .build();

        try {
            resolver.read(request).setResultListener(mListenerHeartRate);
        } catch (Exception e) {
            Log.e(SHealthActivityStepCount.APP_TAG, "Getting heart rate fails.", e);
            tvSHealthInfo.setText("Getting heart rate fails.");
        }
    }


    private class LiveData {
        float heart_rate = 0.f;
        float heart_rate_min = 0.f;
        float heart_rate_max = 0.f;
        long start_time = 0L;
        long end_time = 0L;
    }


    private final HealthResultHolder.ResultListener<HealthDataResolver.ReadResult> mListenerHeartRate =
            new HealthResultHolder.ResultListener<HealthDataResolver.ReadResult>() {
                @Override
                public void onResult(HealthDataResolver.ReadResult result){
                    int ii = 0;
                    int hbc = 0;
                    long hr = 0;
                    byte[] binningHR;
                    String startTime;
                    String endTime;

                    try {
                        Log.w(APP_TAG, "Heart rate INFO: " + result.getCount());
                        for (HealthData data : result) {
                            hbc = data.getInt(HealthConstants.HeartRate.HEART_BEAT_COUNT);
                            hr = data.getLong(HealthConstants.HeartRate.HEART_RATE);
                            binningHR = data.getBlob(HealthConstants.HeartRate.BINNING_DATA);
                            startTime = data.getString(HealthConstants.StepCount.START_TIME);
                            endTime = data.getString(HealthConstants.StepCount.END_TIME);


                            Log.i(APP_TAG,"Heart rate: " + hr
                                    + "\t\tHeart Beat Count: " + hbc
                                    + "\t\tStart time: " + ms2str(startTime)
                                    + "\t\tEnd time: " + ms2str(endTime));


                            if (binningHR != null) {
                                //Log.i(APP_TAG, new String(binningHR));

                                List<LiveData> liveDataList = HealthDataUtil.getStructuredDataList(binningHR, LiveData.class);
                                Log.i(APP_TAG, "liveDataList size: " + liveDataList.size());

                                for (LiveData liveData : liveDataList) {
                                    Log.i(" ", "-->\tHR: " + liveData.heart_rate
                                            + "\t\tHR min: " + liveData.heart_rate_min
                                            + "\t\tHR max: " + liveData.heart_rate_max
                                            + "\t\tStart time: " + ms2str(liveData.start_time)
                                            + "\t\tEnd time: " + ms2str(liveData.end_time));
                                }
                            }

                            ii++;

                        }
                    } finally {
                        Log.i(APP_TAG, "Number of loaded data points (heart rate): " + ii);
                        result.close();
                    }
                }
            };


    private void showConnectionFailureDialog(HealthConnectionErrorResult error) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        mConnError = error;
        String message = "Connection with Samsung Health is not available";

        if (mConnError.hasResolution()) {
            switch(error.getErrorCode()) {
                case HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED:
                    message = "Please install Samsung Health";
                    break;
                case HealthConnectionErrorResult.OLD_VERSION_PLATFORM:
                    message = "Please upgrade Samsung Health";
                    break;
                case HealthConnectionErrorResult.PLATFORM_DISABLED:
                    message = "Please enable Samsung Health";
                    break;
                case HealthConnectionErrorResult.USER_AGREEMENT_NEEDED:
                    message = "Please agree with Samsung Health policy";
                    break;
                default:
                    message = "Please make Samsung Health available";
                    break;
            }
        }

        alert.setMessage(message);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (mConnError.hasResolution()) {
                    mConnError.resolve(mInstance);
                }
            }
            });

        if (error.hasResolution()) {
            alert.setNegativeButton("Cancel", null);
        }

        alert.show();
    }


    private String ms2str(long timeInMillis) {
        return(dateFormatter.format(new Date(timeInMillis)));
    }

    private String ms2str(String timeInMillis) {
        return(dateFormatter.format(new Date(Long.parseLong(timeInMillis))));
    }


    private long getStartTimeOfToday() {
        Calendar today = Calendar.getInstance(TimeZone.getDefault());

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today.getTimeInMillis();
    }


    /*
    private void readHeartRate(long startTime, long endTime) {
        HealthDataResolver resolver = new HealthDataResolver(mStore, null);

        // Set time range to all day yesterday
        HealthDataResolver.Filter filter = HealthDataResolver.Filter.and(HealthDataResolver.Filter.greaterThanEquals(HealthConstants.HeartRate.START_TIME, startTime),
                HealthDataResolver.Filter.lessThanEquals(HealthConstants.HeartRate.START_TIME, endTime));
        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.HeartRate.HEALTH_DATA_TYPE)
                .setProperties(new String[]{
                        HealthConstants.HeartRate.HEART_BEAT_COUNT,
                        HealthConstants.HeartRate.HEART_RATE
                })
                .setFilter(filter)
                .build();

        try {
            resolver.read(request).setResultListener(new HealthResultHolder.ResultListener<HealthDataResolver.ReadResult>() {
                @Override
                public void onResult(HealthDataResolver.ReadResult result) {
                    Log.w(APP_TAG, "Getting heart rate...");

                    int heartBeatCount;
                    long heartRate;
                    Cursor c = null;

                    try {
                        c = result.getResultCursor();
                        if (c != null) {
                            if (c.getCount() == 0) {
                                Log.d(APP_TAG, "No heart rate entry found.");
                            }
                            while (c.moveToNext()) {
                                heartBeatCount = c.getInt(c.getColumnIndex(HealthConstants.HeartRate.HEART_BEAT_COUNT));
                                heartRate = c.getLong(c.getColumnIndex(HealthConstants.HeartRate.HEART_RATE));
                                //Toast.makeText(context,""+heartRate,Toast.LENGTH_LONG).show();
                                mBeatcount.setText(""+heartRate);
                                Log.w(APP_TAG, "Heart beat count " + heartBeatCount
                                        + ", heart rate " + heartRate + "bpm"
                                );
                            }
                        }
                    } finally {
                        if (c != null) {
                            c.close();
                        }
                    }

                    // TODO: Save heart rate to DB
                    //mCountDownLatch.countDown();
                }
            });
        }
        catch (Exception e) {
            Log.d(APP_TAG, e.getClass().getName() + " - " + e.getMessage());
            Log.d(APP_TAG, "Getting heart rate failed.");
        }
    }
    */



}
