package com.example.ediaryphysicalactivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEntryActivity extends AppCompatActivity {

    private TextView textViewDateTime, textViewDateAttr8, textViewTimeAttr9;
    private EditText editTextAttr1, editTextAttr2, editTextAttr3;
    private CheckedTextView checkedTextViewAttr4;
    private SeekBar seekBarAttr5;
    private RadioGroup radioGroupAttr6;
    private RatingBar ratingBarAttr7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ediary_entry);

        // Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy   HH:mm");
        Date date = new Date();
        textViewDateTime = findViewById(R.id.text_view_date_time);
        textViewDateTime.setText(formatter.format(date));

        // Get the views of all attributes
        editTextAttr1 = findViewById(R.id.edit_text_attr_1);
        editTextAttr2 = findViewById(R.id.edit_text_attr_2);
        editTextAttr3 = findViewById(R.id.edit_text_attr_3);

        checkedTextViewAttr4 = findViewById(R.id.checkedtextview_attr_4);
        checkedTextViewAttr4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedTextViewAttr4.toggle();
            }
        });

        seekBarAttr5 = findViewById(R.id.seekbar_attr_5);

        radioGroupAttr6 = findViewById(R.id.radio_group_attr_6);

        ratingBarAttr7 = findViewById(R.id.rating_bar_attr_7);

        textViewDateAttr8 = findViewById(R.id.text_view_attr_8);
        findViewById(R.id.button_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEntryActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                textViewDateAttr8.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        textViewTimeAttr9 = findViewById(R.id.text_view_attr_9);
        findViewById(R.id.button_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEntryActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                textViewTimeAttr9.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });


        // Set save button
        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEntry();
            }
        });
    }



    private void saveEntry() {
        final String sDateTime = textViewDateTime.getText().toString().trim();
        final String sAttr1 = editTextAttr1.getText().toString().trim();
        final String sAttr2 = editTextAttr2.getText().toString().trim();
        final String sAttr3 = editTextAttr3.getText().toString().trim();
        final boolean bAttr4 = checkedTextViewAttr4.isChecked();
        final Integer iAttr5 = seekBarAttr5.getProgress();

        String bufferString = new String();
        int selectedRadioButtonID = radioGroupAttr6.getCheckedRadioButtonId();
        // If nothing is selected from Radio Group, then it return -1
        if (selectedRadioButtonID != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonID);
            bufferString = selectedRadioButton.getText().toString().trim();
        } else {
            bufferString = "Nothing selected";
        }
        final String sAttr6 = bufferString;

        final Float fAttr7 = ratingBarAttr7.getRating();
        final String sAttr8 = textViewDateAttr8.getText().toString().trim();
        final String sAttr9 = textViewTimeAttr9.getText().toString().trim();

        if (sAttr1.isEmpty()) {
            editTextAttr1.setError("Attribute 1 required");
            editTextAttr1.requestFocus();
            return;
        }

        class SaveEntry extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating an entry
                EDiaryEntry entry = new EDiaryEntry();
                entry.setDate_time_str(sDateTime);
                entry.setAttr_str_1(sAttr1);
                entry.setAttr_str_2(sAttr2);
                entry.setAttr_str_3(sAttr3);
                entry.setAttr_bl_4(bAttr4);
                entry.setAttr_i_5(iAttr5);
                entry.setAttr_s_6(sAttr6);
                entry.setAttr_f_7(fAttr7);
                entry.setAttr_str_8(sAttr8);
                entry.setAttr_str_9(sAttr9);

                //adding to database
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .insert(entry);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                finish();
                startActivity(new Intent(getApplicationContext(), ShowEntriesActivity.class));
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
            }
        }

        SaveEntry se = new SaveEntry();
        se.execute();
    }

}