package com.example.mobileapplicationdevelopment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AssessmentAddActivity extends AppCompatActivity {
    String assessmentFilter;
    String noteFilter;
    String assessmentAction;

    String oldTitle;
    String oldDate;
    String oldTime;
    String oldType;
    String courseId;

    EditText editTitle;
    EditText editDate;
    EditText editTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(CourseViewProvider.COURSE_CONTENT_TYPE);
        courseId = uri.getLastPathSegment();
        assessmentFilter = DBOpenHelper.ASSESSMENT_COURSE + " = " + courseId;

        //checking if it is goind to add a new record or edit an exsiting one

        editDate = (EditText) findViewById(R.id.assessment_input_date);
        editTime = (EditText) findViewById(R.id.assessment_input_time);
        editTitle = (EditText) findViewById(R.id.assessment_input_title);
        Log.d("db course base_path" , uri.getPath().toString());
        Log.d("db getPath contains",  "uri.contains course "
                + uri.getPath().contains("course"));
        if (uri.getPath().contains("course")) {//this will never be null
            assessmentAction = Intent.ACTION_INSERT;
            setTitle("Add Assessment");
        } else {//this is when the assessment is being edited
            assessmentAction = Intent.ACTION_EDIT;
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_ASSESSMENTS_COLUMNS, assessmentFilter,
                    null, null);

            noteFilter = DBOpenHelper.NOTE_ASSESSMENT + " = " + uri.getLastPathSegment();
            oldTitle = (String) cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TITLE));
            setTitle(oldTitle);
            editTitle.setText(oldTitle);
            oldDate = (String) cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DATE));
            editDate.setText(oldDate);
            oldTime = (String) cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TIME));
            editTime.setText(oldTime);
            oldTime = (String) cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TIME));
            oldType = (String) cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TYPE));

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, courseId + " Hello Course", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (assessmentAction.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
        }
        getMenuInflater().inflate(R.menu.menu_add, menu);
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
            case R.id.action_delete:
                deleteAssessment();
                break;
        }
        return true;
    }

    private void finishEditing() {
        String title = editTitle.getText().toString().trim();
        String date = editDate.getText().toString().trim();
        String time = editTime.getText().toString().trim();
        Log.d("db assessmentAdd", title+ " " + date + " " + time + " " + courseId);
        //todo add assessment type
        switch (assessmentAction) {
            case Intent.ACTION_INSERT:
                if (title.length() == 0 && date.length() == 0 && time.length() == 0) {
                    setResult(RESULT_CANCELED);
                }else {
                    insertAssessment(title, date, time, courseId);
                }
                break;
            case Intent.ACTION_EDIT:
                if(title.length() == 0 && date.length() == 0 && time.length() == 0) {
                    deleteAssessment();
                }else if(title.equals(oldTitle) && date.equals(oldDate) && time.equals(oldTime)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateAssessment(title, date, time, courseId);
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

    private void updateAssessment(String title, String date, String time, String courseId) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_TITLE, title);
        values.put(DBOpenHelper.ASSESSMENT_DATE, date);
        values.put(DBOpenHelper.ASSESSMENT_TIME, time);
        //values.put(DBOpenHelper.ASSESSMENT_TYPE, type);
        values.put(DBOpenHelper.ASSESSMENT_COURSE, courseId);
        getContentResolver().update(AssessmentViewProvider.ASSESSMENT_URI, values, assessmentFilter, null);
        setResult(RESULT_OK);
    }
    private void updateNote(){}


    private void insertAssessment(String title, String date, String time, String courseId) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_TITLE, title);
        values.put(DBOpenHelper.ASSESSMENT_DATE, date);
        values.put(DBOpenHelper.ASSESSMENT_TIME, time);
        //values.put(DBOpenHelper.ASSESSMENT_TYPE, type);
        values.put(DBOpenHelper.ASSESSMENT_COURSE, courseId);
        getContentResolver().insert(AssessmentViewProvider.ASSESSMENT_URI, values);
        setResult(RESULT_OK);
    }

    private void insertNote(){

    }

    @Override
    public void onBackPressed() {
        finishEditing();

    }
}
