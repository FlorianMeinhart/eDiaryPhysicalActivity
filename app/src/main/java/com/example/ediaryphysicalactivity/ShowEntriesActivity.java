package com.example.ediaryphysicalactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ShowEntriesActivity extends AppCompatActivity {

    private FloatingActionButton buttonAddEntry;
    private RecyclerView recyclerViewEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ediary_entries);

        recyclerViewEntries = findViewById(R.id.recyclerview_ediary_entries);
        recyclerViewEntries.setLayoutManager(new LinearLayoutManager(this));

        // Set empty adapter to avoid warning message
        List<EDiaryEntry> emptyListBuffer = new ArrayList<EDiaryEntry>();
        EDiaryEntriesAdapter adapter = new EDiaryEntriesAdapter(ShowEntriesActivity.this, emptyListBuffer);
        recyclerViewEntries.setAdapter(adapter);

        buttonAddEntry = findViewById(R.id.floating_button_add_entry);
        buttonAddEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddEntryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        getEntries();
    }


    private void getEntries() {
        class GetEntries extends AsyncTask<Void, Void, List<EDiaryEntry>> {

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
                EDiaryEntriesAdapter adapter = new EDiaryEntriesAdapter(ShowEntriesActivity.this, entries);
                recyclerViewEntries.setAdapter(adapter);
            }
        }

        GetEntries ge = new GetEntries();
        ge.execute();
    }
}
