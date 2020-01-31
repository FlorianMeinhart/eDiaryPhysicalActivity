package com.example.ediaryphysicalactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getTimeInstance;


public class GoogleFitClient extends AppCompatActivity {

    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;
    private static final int ACTIVITY_RECOGNITION_PERMISSION_CODE = 333;
    private static final int BODY_SENSOR_PERMISSION_CODE = 500;

    //final DataType DATA_TYPE = DataType.TYPE_STEP_COUNT_DELTA;
    final DataType DATA_TYPE = DataType.TYPE_HEART_RATE_BPM;

    private static final String TAG = "GoogleFitClient";

    private TextView textViewInfo;
    private Button btnGetSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_fit_client);

        checkPermission(Manifest.permission.ACTIVITY_RECOGNITION,
                ACTIVITY_RECOGNITION_PERMISSION_CODE);

        checkPermission(Manifest.permission.BODY_SENSORS,
                BODY_SENSOR_PERMISSION_CODE);

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DATA_TYPE)
                //.addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        }

        textViewInfo = findViewById(R.id.text_view_google_fit_info);

        btnGetSteps = findViewById(R.id.button_get_steps);
        btnGetSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleFitHistoryClient();
            }
        });

}



    private void googleFitHistoryClient() {

        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();

        //cal.add(Calendar.WEEK_OF_YEAR, -1);
        cal.add(Calendar.WEEK_OF_YEAR, -52);
        long startTime = cal.getTimeInMillis();


        //------------------------------------------------------------------------------------------
        // Google Fit API


        GoogleSignInOptionsExtension fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DATA_TYPE, FitnessOptions.ACCESS_READ)
                        .build();

        GoogleSignInAccount googleSignInAccount =
                GoogleSignIn.getAccountForExtension(this, fitnessOptions);

        Task<DataReadResponse> response = Fitness.getHistoryClient(this, googleSignInAccount)
                .readData(new DataReadRequest.Builder()
                        .read(DATA_TYPE)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build());

        response.addOnSuccessListener(
                new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse response) {
                        DataSet dataSet = response.getDataSet(DATA_TYPE);

                        printDataSet(dataSet);
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("", "Problem getting data --> readDataFitnessHistory", e);
                            }
                        });;
    }
        //DataReadResponse readDataResponse = Tasks.await(response);

        //DataSet dataSet = readDataResponse.getDataSet(DATA_TYPE);

        //printDataSet(dataSet);

        //List<DataPoint> dataPoints = readDataResponse.getDataSet(DATA_TYPE).getDataPoints();
        //DataSet dataSet = readDataResult.getDataSet(DATA_TYPE );

    public void printDataSet(DataSet dataSet) {

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
}
