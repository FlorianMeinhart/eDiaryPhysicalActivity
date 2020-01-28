package com.example.ediaryphysicalactivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
                data.append("ID,ATTR_01,ATTR_02,ATTR_03,ATTR_04");
                for (EDiaryEntry entry : entries) {
                    data.append("\n"+entry.getId()+","
                                    +entry.getAttr_str_1()+","
                                    +entry.getAttr_str_2()+","
                                    +entry.getAttr_str_3()+","
                                    +entry.isAttr_bl_4());
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
}

