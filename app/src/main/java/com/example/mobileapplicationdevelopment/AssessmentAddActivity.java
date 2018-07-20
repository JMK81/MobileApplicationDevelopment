package com.example.mobileapplicationdevelopment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

public class AssessmentAddActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    String assessmentFilter;
    String assessmentAction;

    String oldTitle;
    String oldDate;
    String oldTime;
    String oldType;
    String courseId;
    String oldNote;
    String TAG = "assessmentAddActivity";

    EditText editTitle;
    EditText editNote;
    EditText editType;

    TextView editDate;
    TextView editTime;

    ImageButton addDate;
    ImageButton addTime;
    private final int ASSESSMENT_REQUEST_CODE = 4004;
    private final int COURSE_REQUEST_CODE = 3003;

    Uri assessmentUri;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_add);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();


        editDate = findViewById(R.id.assessment_input_date);
        editTime = findViewById(R.id.assessment_input_time);
        editTitle = findViewById(R.id.assessment_input_title);
        editNote = findViewById(R.id.assessment_input_note);
        editType = findViewById(R.id.assessment_input_type);

        uri = intent.getParcelableExtra(CourseViewProvider.COURSE_CONTENT_TYPE);

        if (uri != null && uri.getPath().contains("course")) {//coming from CourseDetailActivity uri will have a "course" insert new record
            assessmentAction = Intent.ACTION_INSERT;
            courseId = uri.getLastPathSegment();
            setTitle("Add Assessment");
        } else {//this is when the assessment is being edited
            uri = intent.getParcelableExtra(AssessmentViewProvider.ASSESSMENT_CONTENT_TYPE);
            assessmentAction = Intent.ACTION_EDIT;
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_ASSESSMENTS_COLUMNS, null,
                    null, null);
            cursor.moveToFirst();
            oldTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TITLE));
            setTitle("Edit:" + oldTitle);
            editTitle.setText(oldTitle);
            oldDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DATE));
            editDate.setText(oldDate);
            oldTime = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TIME));
            editTime.setText(oldTime);
            oldType = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TYPE));
            editType.setText(oldType);
            oldNote = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTE));
            editNote.setText(oldNote);
            courseId = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_COURSE));

        }
            addDate = findViewById(R.id.assessment_add_date);
            addDate.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    DialogFragment datePicker = new DatePickerFragment();
                    datePicker.show(getSupportFragmentManager(), "datePicker");
                }
            });
        addTime = findViewById(R.id.assessment_add_time);
        addTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "timePicker");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (assessmentAction.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_add, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_add:
                finishEditing();
                break;
            case R.id.action_edit:
                finishEditing();
                break;
        }
        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime());

        editDate.setText(currentDateString);
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

        editTime.setText(currentTimeString);

    }
public void pickTime(View v){
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "timePicker");
}

    private void finishEditing() {
        String title = editTitle.getText().toString().trim();
        String date = editDate.getText().toString().trim();
        String time = editTime.getText().toString().trim();
        String type = editType.getText().toString().trim();
        String note = editNote.getText().toString().trim();

        switch (assessmentAction) {
            case Intent.ACTION_INSERT:
                if (title.length() == 0 && date.length() == 0 && time.length() == 0 && type.length() == 0
                        && note.length() == 0) {
                    setResult(RESULT_CANCELED);
                }else {
                    assessmentUri = insertAssessment(title, date, time, type, note, courseId);
                }
                break;
            case Intent.ACTION_EDIT:
                if(title.length() == 0 && date.length() == 0 && time.length() == 0 && type.length() == 0
                    && note.length() == 0) {
                    deleteAssessment();
                }else if(title.equals(oldTitle) && date.equals(oldDate) && time.equals(oldTime) && type.equals(oldType)
                        && note.equals(oldType)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateAssessment(title, date, time, type, note, courseId);

                }
        }
finish();
    }

    private void deleteAssessment() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //db management
                            getContentResolver().delete(AssessmentViewProvider.ASSESSMENT_URI, assessmentFilter, null);
                            setResult(RESULT_OK);
                            finish();

                            Toast.makeText(AssessmentAddActivity.this,
                                    getString(R.string.delete),
                                    Toast.LENGTH_SHORT).show();
                        } else if (button == DialogInterface.BUTTON_NEGATIVE) {

                        }

                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void updateAssessment(String title, String date, String time, String type, String note, String courseId) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_TITLE, title);
        values.put(DBOpenHelper.ASSESSMENT_DATE, date);
        values.put(DBOpenHelper.ASSESSMENT_TIME, time);
        values.put(DBOpenHelper.ASSESSMENT_TYPE, type);
        values.put(DBOpenHelper.ASSESSMENT_NOTE, note);
        values.put(DBOpenHelper.ASSESSMENT_COURSE, courseId);
        getContentResolver().update(AssessmentViewProvider.ASSESSMENT_URI, values, assessmentFilter, null);
        setResult(RESULT_OK);
    }


    private Uri insertAssessment(String title, String date, String time, String type, String note, String courseId) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_TITLE, title);
        values.put(DBOpenHelper.ASSESSMENT_DATE, date);
        values.put(DBOpenHelper.ASSESSMENT_TIME, time);
        values.put(DBOpenHelper.ASSESSMENT_TYPE, type);
        values.put(DBOpenHelper.ASSESSMENT_NOTE, note);
        values.put(DBOpenHelper.ASSESSMENT_COURSE, courseId);
        Uri uri = getContentResolver().insert(AssessmentViewProvider.ASSESSMENT_URI, values);
        setResult(RESULT_OK);
        return uri;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishEditing();

    }


}
