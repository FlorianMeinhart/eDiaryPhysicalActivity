package com.example.ediaryphysicalactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class UpdateEntryActivity extends AppCompatActivity {

    private EditText editTextAttr1, editTextAttr2, editTextAttr3;
    private CheckBox checkBoxAttr4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ediary_entry);

        editTextAttr1 = findViewById(R.id.edit_text_attr_1);
        editTextAttr2 = findViewById(R.id.edit_text_attr_2);
        editTextAttr3 = findViewById(R.id.edit_text_attr_3);
        checkBoxAttr4 = findViewById(R.id.checkbox_attr_4);

        // Selected task (clicked viewHolder)
        final EDiaryEntry entry = (EDiaryEntry) getIntent().getSerializableExtra("entry");

        // Fill views
        loadEDiaryEntry(entry);

        findViewById(R.id.button_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_LONG).show();
                updateEDiaryEntry(entry);
            }
        });

        findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateEntryActivity.this);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteEDiaryEntry(entry);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
            }
        });
    }

    private void loadEDiaryEntry(EDiaryEntry entry) {
        editTextAttr1.setText(entry.getAttr_str_1());
        editTextAttr2.setText(entry.getAttr_str_2());
        editTextAttr3.setText(entry.getAttr_str_3());
        checkBoxAttr4.setChecked(entry.isAttr_bl_4());
    }

    private void updateEDiaryEntry(final EDiaryEntry entry) {
        final String sAttr1 = editTextAttr1.getText().toString().trim();
        final String sAttr2 = editTextAttr2.getText().toString().trim();
        final String sAttr3 = editTextAttr3.getText().toString().trim();
        final boolean bAttr4 = checkBoxAttr4.isChecked();

        if (sAttr1.isEmpty()) {
            editTextAttr1.setError("Text required");
            editTextAttr1.requestFocus();
            return;
        }

        class UpdateEDiaryEntry extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                entry.setAttr_str_1(sAttr1);
                entry.setAttr_str_2(sAttr2);
                entry.setAttr_str_3(sAttr3);
                entry.setAttr_bl_4(bAttr4);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .update(entry);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(UpdateEntryActivity.this, ShowEntriesActivity.class));
            }
        }

        UpdateEDiaryEntry ue = new UpdateEDiaryEntry();
        ue.execute();
    }

    private void deleteEDiaryEntry(final EDiaryEntry entry) {
        class DeleteEDiaryEntry extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .delete(entry);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(UpdateEntryActivity.this, ShowEntriesActivity.class));
            }
        }

        DeleteEDiaryEntry de = new DeleteEDiaryEntry();
        de.execute();

    }

}