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
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getTimeInstance;

public class GraphViewActivity extends AppCompatActivity {

    GraphView graph;

    Integer numLastDays;
    Calendar cal;
    Date now;
    long startTime;
    long endTime;


    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;
    private static final int ACTIVITY_RECOGNITION_PERMISSION_CODE = 333;
    //private static final int BODY_SENSOR_PERMISSION_CODE = 500;

    final DataType DATA_TYPE = DataType.TYPE_STEP_COUNT_DELTA;
    //final DataType DATA_TYPE = DataType.TYPE_HEART_RATE_BPM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);

        graph = (GraphView) findViewById(R.id.graph_view);

        checkPermission(Manifest.permission.ACTIVITY_RECOGNITION,
                ACTIVITY_RECOGNITION_PERMISSION_CODE);

        numLastDays = 1;
        cal = Calendar.getInstance();
        now = new Date();
        cal.setTime(now);
        endTime = cal.getTimeInMillis();

        cal.add(Calendar.DAY_OF_MONTH, -numLastDays);
        startTime = cal.getTimeInMillis();

        loadDataGoogleFit(numLastDays);

    }






    public void loadDataGoogleFit(int numLastDays) {

        // Google Fit API

        // Set fitness options
        GoogleSignInOptionsExtension fitnessOptions =
                FitnessOptions
                        .builder()
                        .addDataType(DATA_TYPE, FitnessOptions.ACCESS_READ)
                        .build();

        // Sign in
        GoogleSignInAccount googleSignInAccount =
                GoogleSignIn
                        .getAccountForExtension(this, fitnessOptions);

        // Task to load data
        Task<DataReadResponse> response =
                Fitness
                        .getHistoryClient(this, googleSignInAccount)
                        .readData(new DataReadRequest.Builder()
                                .read(DATA_TYPE)
                                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                                .build());

        // Get data when loaded
        response.addOnSuccessListener(
                new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse response) {
                        DataSet dataSet = response.getDataSet(DATA_TYPE);
                        Log.i("", "Data loaded.");

                        plotData(dataSet);
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("", "Problem: Loading data.", e);
                            }
                        });;
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




    private void plotData(DataSet dataSet) {

        final int MAX_DATA_POINTS = dataSet.getDataPoints().size();

        DataPoint[] dataPoints = new DataPoint[MAX_DATA_POINTS];

        Integer i = 0;
        long xStart;
        long sumSteps = 0;

        for (com.google.android.gms.fitness.data.DataPoint dp : dataSet.getDataPoints()) {

            xStart = dp.getEndTime(TimeUnit.MILLISECONDS);
            Log.i("mdg", "\tStart: " + dp.getStartTime(TimeUnit.MILLISECONDS));

            for(Field field : dp.getDataType().getFields()) {

                String fieldName = field.getName();
                Log.i("MSG", "\tField: " + fieldName);

                Value val = dp.getValue(field);
                Log.i("MSG", "\tValue: " + val);

                if (fieldName.trim().equals("steps")) {

                    sumSteps += val.asInt();
                }
            }

            dataPoints[i] = new DataPoint(xStart, sumSteps);

            i++;
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
        graph.addSeries(series);


        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    //Format formatter = new SimpleDateFormat("dd.MM.yy  HH:mm");
                    Format formatter = new SimpleDateFormat("HH:mm");
                    return formatter.format(value);
                }
                return super.formatLabel(value, isValueX);
            }
        });

        graph.getGridLabelRenderer().setHorizontalLabelsAngle(130);

        graph.getViewport().setScalable(true);
        graph.getGridLabelRenderer().setNumHorizontalLabels(8);

        graph.getViewport().setScrollable(true);
        graph.setTitle("Steps in the last 24h");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Number of steps");

        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(60f);
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(60f);
        graph.setTitleTextSize(60f);
        graph.getGridLabelRenderer().setTextSize(45f);
        graph.getGridLabelRenderer().setPadding(50);
        graph.getGridLabelRenderer().setLabelHorizontalHeight(150);
        graph.getGridLabelRenderer().reloadStyles();


        graph.getViewport().setMinX(startTime);
        graph.getViewport().setMaxX(endTime);
    }
}
