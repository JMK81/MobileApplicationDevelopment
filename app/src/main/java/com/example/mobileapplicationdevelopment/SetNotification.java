package com.example.mobileapplicationdevelopment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;

public class SetNotification extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private String editDate;
    private String editTime;

    TextView date;
    TextView time;

    EditText editTitle;
    EditText editNote;

    int alarmYear;
    int alarmMonth;
    int alarmDayOfMonth;
    int alarmHour;
    int alarmMinute;

    int id;
    String TAG = "SetNotification";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_notification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        ImageButton addDateBtn = findViewById(R.id.notification_add_date);
        ImageButton addTimeBtn = findViewById(R.id.notification_add_time);
        //needs to pick a date


        addDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });
        addTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "timePicker");
            }
        });
        editTitle = findViewById(R.id.notification_input_title);
        editNote = findViewById(R.id.notification_input_note);
        date = findViewById(R.id.notification_input_date);
        time = findViewById(R.id.notification_input_time);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishEditing();
                finish();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime());

        date.setText(currentDateString);

        alarmYear = year;
        alarmMonth = month;
        alarmDayOfMonth = dayOfMonth;
    }

    public void pickStart(View v) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        String currentTimeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        time.setText(currentTimeString);

        alarmHour = hourOfDay;
        alarmMinute = minute;


    }

    private long getMills(int year, int month, int dayOfMonth, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);

        long mills = c.getTimeInMillis();
        Log.d(TAG, " getMills" + mills);
        return mills;
    }

    private void finishEditing() {

        String title = editTitle.getText().toString().trim();
        String note = editNote.getText().toString().trim();

        //only setting up to have one action
        if (date == null && date == null && title.length() == 0 && note.length() == 0) {
            setResult(RESULT_CANCELED);
        } else {
            long l = getMills(alarmYear, alarmMonth, alarmDayOfMonth, alarmHour, alarmMinute);
            String notificationTitle = editTitle.getText().toString().trim();
            String notificationNote = editNote.getText().toString().trim();

            id = insertNotification(l, notificationTitle, notificationNote);

        }
    }

    //todo not necessary to satisfy rubric
    private int insertNotification(long mills, String title, String note) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTIFICATION_MILLS, mills);
        values.put(DBOpenHelper.NOTIFICATION_TITLE, title);
        values.put(DBOpenHelper.NOTIFICATION_NOTE, note);
        Uri nUri = getContentResolver().insert(NotificationProvider.NOTIFICATION_URI, values);

        setAlarm(getNotification(title, note), mills);

        int i = Integer.parseInt(nUri.getLastPathSegment());
        return i;

    }

    public void setAlarm(Notification notification, long mills) {

        Intent intent = new Intent(this, ReceiverHelper.class);
        intent.putExtra(ReceiverHelper.NOTIFICATION_ID, id);
        intent.putExtra(ReceiverHelper.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, mills, pendingIntent);
        // ELAPSED_REALTIME_WAKEUP
    }

    private Notification getNotification(String title, String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_document_anouncement);
        return builder.build();
    }

    @Override
    public void onBackPressed() {
        finishEditing();
        finish();
    }
//todo extend app
    public void getPendingAlarms() {

    }


}
