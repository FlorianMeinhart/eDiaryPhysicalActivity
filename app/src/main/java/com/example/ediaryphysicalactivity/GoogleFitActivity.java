
package com.example.ediaryphysicalactivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

/**
 * This sample demonstrates combining the Recording API and History API of the Google Fit platform
 * to record steps, and display the daily current step count. It also demonstrates how to
 * authenticate a user with Google Play Services.
 */
public class GoogleFitActivity extends AppCompatActivity {

    public static final String TAG = "StepCounter";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;

    private TextView textViewInfo;
    private Button btnGetSteps;

    private static final int ACTIVITY_RECOGNITION_PERMISSION_CODE = 333;
    private static final int BODY_SENSOR_PERMISSION_CODE = 555;

    // actual Google API client
    private GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_fit);

        textViewInfo = findViewById(R.id.text_view_google_fit_info);

        btnGetSteps = findViewById(R.id.button_get_steps);
        btnGetSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readData();
            }
        });

        checkPermission(Manifest.permission.ACTIVITY_RECOGNITION,
                ACTIVITY_RECOGNITION_PERMISSION_CODE);

        checkPermission(Manifest.permission.BODY_SENSORS,
                BODY_SENSOR_PERMISSION_CODE);


        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            subscribe();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.i("onActivityResult", "resultCode == Activity.RESULT_OK");

            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe();
                showInfo("Subscribed");
            }
        }
        else {
            Log.i("onActivityResult", Activity.RESULT_OK + "");
            showInfo("Not subscribed");
        }
    }

    /** Records step data by requesting a subscription to background step data. */
    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Successfully subscribed!");
                                    showInfo("Successfully subscribed!");
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                    showInfo("There was a problem subscribing.");
                                }
                            }
                        });
    }

    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    private void readData() {
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                long total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                Log.i(TAG, "Total steps: " + total);
                                showInfo("Total steps: " + total);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                                showInfo("There was a problem getting the step count.");
                            }
                        });

        readDataFitnessHistory();
    }



    private void showInfo(String info) {
        textViewInfo.setText(info);
    }




    // Function to check and request permission
    public void checkPermission(String permission, int requestCode)
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
        }
        else {
            Toast.makeText(this,
                    permission+" \nPermission already granted",
                     Toast.LENGTH_LONG)
                    .show();
        }
    }



    private void readDataFitnessHistory()
    {

        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();

        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.d(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.d(TAG, "Range End: " + dateFormat.format(endTime));


        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        /*
        DataReadResult dataReadResult =
                Fitness.HistoryApi.readData(mApiClient, readRequest).await(1, TimeUnit.MINUTES);
        DataSet dataSet = dataReadResult.getDataSet(DataType.TYPE_HEART_RATE_BPM);
        */

        //Task<DataReadResponse> response = Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)).readData(readRequest);
        //List<DataSet> dataSets = response.getResult().getDataSets();

        //DataReadResult readDataResult = Tasks.await(response).getDataSets();
        //List<DataSet> dataSets = readDataResult.getDataSets();

        /*
        DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .setAppPackageName("com.example.ediaryphysicalactivity")
                .build();
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(ESTIMATED_STEP_DELTAS,    DataType.AGGREGATE_STEP_COUNT_DELTA)
                //.aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                //.aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                //.aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();
        */

        final DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_STEP_COUNT_DELTA)
                //.read(DataType.TYPE_HEART_RATE_BPM)
                //.enableServerQueries()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();


        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse response) {
                                Log.e(TAG, "/n----------- onSuccess -----------");

                                //for (Bucket bucket : response.getBuckets()) {
                                //    Log.e(TAG, "/n----------- Bucket -----------");

                                    for (DataSet dataSet : response.getDataSets()) {
                                        Log.e(TAG, "/n----------- Data Set -----------");
                                        printDataSet(dataSet);
                                    }
                                //}
                            }
                        }
                )
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Problem getting data --> readDataFitnessHistory", e);
                            }
                        });


    }

    private void printDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = getTimeInstance();
        DateFormat dateOnlyFormat = new SimpleDateFormat("dd:MM:yy");

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tDate: " + dateOnlyFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                Value val = dp.getValue(field);
                String fieldName = field.getName();
                Log.i(TAG, "\tField: " + fieldName);
                Log.i(TAG, "\tValue: " + val);
            }
        }
    }




    /*
    private void buildSensors() {
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_BODY_READ))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        DataSourcesRequest dataSourcesRequest = new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_HEART_RATE_BPM)
                .setDataSourceTypes(DataSource.TYPE_RAW) // data type, raw or derived?
                .build();

        Fitness.SensorsApi.findDataSources(mClient, dataSourcesRequest).setResultCallback(this);
    }

    @Override
    public void onResult(DataSourcesResult dataSourcesResult) {
        // On New Source Result
        for (final DataSource dataSource : dataSourcesResult.getDataSources()) {

            // Request updates from this source, samplingRate
            SensorRequest sensorRequest = new SensorRequest.Builder()
                    .setDataSource(dataSource) // Optional but recommended for custom data sets.
                    .setDataType(dataSource.getDataType()) // Can't be omitted.
                    .setSamplingRate(1, TimeUnit.SECONDS)
                    .build();

            Log.i(TAG, "Fitness.SensorsApi.add for " + dataSource.toString() + " and type " + dataSource.getDataType().getName());
            Fitness.SensorsApi.add(mClient, sensorRequest, this)
                    //Can be removed later
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) { // This might be important later on "Cannot register listener to source"
                            if (status.isSuccess()) {
                                Log.i(TAG, "Listener registered!");
                            } else {
                                Log.e(TAG, "Unable to register listener for source: " + dataSource.toString());
                            }
                        }
                    });
        }
    }
    */

}