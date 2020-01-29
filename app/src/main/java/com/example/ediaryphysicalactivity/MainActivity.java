package com.example.ediaryphysicalactivity;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnDataPointListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;



    private FloatingActionButton buttonShowEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonShowEntries = findViewById(R.id.show_entries);
        buttonShowEntries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ShowEntriesActivity.class);
                startActivity(intent);

                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                 */
            }
        });


        // Google Fit
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Google Fit
        mApiClient.connect();
        Log.e( "GoogleFit", "mApiClient.connect()" );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.send_data) {
            sendData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    // Send data via CSV-file
    public void sendData() {

        class GetEntriesForExport extends AsyncTask<Void, Void, List<EDiaryEntry>> {

            @Override
            protected List<EDiaryEntry> doInBackground(Void... voids) {
                List<EDiaryEntry> entryList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .taskDao()
                        .getAll();
                return entryList;
            }

            @Override
            protected void onPostExecute(List<EDiaryEntry> entries) {
                super.onPostExecute(entries);

                //generate data
                StringBuilder data = new StringBuilder();
                data.append("ID,DATE_TIME,ATTR_01,ATTR_02,ATTR_03,ATTR_04,ATTR_05,ATTR_06,ATTR_07,ATTR_08,ATTR_08");
                for (EDiaryEntry entry : entries) {
                    data.append("\n"+entry.getId()+","
                                    +entry.getDate_time_str()+","
                                    +entry.getAttr_str_1()+","
                                    +entry.getAttr_str_2()+","
                                    +entry.getAttr_str_3()+","
                                    +entry.isAttr_bl_4()+","
                                    +entry.getAttr_i_5()+","
                                    +entry.getAttr_s_6()+","
                                    +entry.getAttr_f_7()+","
                                    +entry.getAttr_str_8()+","
                                    +entry.getAttr_str_9());
                }

                // Export data
                try{
                    //saving the file into device
                    FileOutputStream out = openFileOutput("eDiaryData.csv", Context.MODE_PRIVATE);
                    out.write((data.toString()).getBytes());
                    out.close();

                    Toast.makeText(getApplicationContext(), "eDiaryData.csv created", Toast.LENGTH_LONG).show();

                    //exporting
                    Context context = getApplicationContext();
                    File filelocation = new File(getFilesDir(), "eDiaryData.csv");
                    Uri path = FileProvider.getUriForFile(context, "com.example.ediaryphysicalactivity.fileprovider", filelocation);
                    Intent fileIntent = new Intent(Intent.ACTION_SEND);
                    fileIntent.setType("text/csv");
                    fileIntent.putExtra(Intent.EXTRA_SUBJECT, "eDiaryData.csv");
                    //fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    fileIntent.putExtra(Intent.EXTRA_STREAM, path);

                    startActivity(Intent.createChooser(fileIntent, "Send data"));
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        GetEntriesForExport ge = new GetEntriesForExport();
        ge.execute();
    }



    // Google Fit

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if( !authInProgress ) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult( MainActivity.this, REQUEST_OAUTH );
            } catch(IntentSender.SendIntentException e ) {

            }
        } else {
            Log.e( "GoogleFit", "authInProgress" );
        }
    }

    @Override
    public void onDataPoint(DataPoint dataPoint) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_OAUTH ) {
            Log.e( "GoogleFit", "REQUEST_OAUTH" );
            authInProgress = false;
            if( resultCode == RESULT_OK ) {
                Log.e( "GoogleFit", "Got authorisation from Google Fit" );
                if( !mApiClient.isConnecting() && !mApiClient.isConnected() ) {
                    Log.e( "GoogleFit", "Re-trying connection with Fit" );
                    mApiClient.connect();
                }
            } else if( resultCode == RESULT_CANCELED ) {
                Log.e( "GoogleFit", "User cancelled the dialog" );
            } else {
                Log.e( "GoogleFit", "Authorisation failed, result code "+ resultCode);
            }
        } else {
            Log.e("GoogleFit", "requestCode NOT request_oauth");
        }
    }
}

