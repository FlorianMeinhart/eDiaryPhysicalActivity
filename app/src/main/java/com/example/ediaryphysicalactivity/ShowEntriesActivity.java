package com.example.ediaryphysicalactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ShowEntriesActivity extends AppCompatActivity {

    private FloatingActionButton buttonAddEntry;
    private RecyclerView recyclerViewEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ediary_entries);

        recyclerViewEntries = findViewById(R.id.recyclerview_ediary_entries);
        recyclerViewEntries.setLayoutManager(new LinearLayoutManager(this));

        buttonAddEntry = findViewById(R.id.floating_button_add_entry);

        /*
        buttonAddEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(this, AddEntryActivity.class);
                startActivity(intent);
            }
        });

         */
    }
}
