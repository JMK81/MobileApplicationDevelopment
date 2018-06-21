package com.example.mobileapplicationdevelopment;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CourseAddActivity extends AppCompatActivity  {
    String courseFilter;
    String action;
    String oldName;
    String oldStart;
    String oldEnd;
    String oldMentor;
    String term;
    String courseId;

    EditText editName;
    EditText editStart;
    EditText editEnd;
    EditText editMentor;
    Button addAssessmentsBtn;
    private static final int ASSESSMENTS_REQUEST_CODE = 3003;
    private static final int TERM_REQUEST_CODE = 1001;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setContentView(R.layout.activity_course_add);


        Intent intent = getIntent();
       //this uri could be a term uri or a course uri need to have an edit course and add course
        uri = intent.getParcelableExtra(CourseViewProvider.COURSE_CONTENT_TYPE);
        Log.d("db CAA uri ", uri.toString());
        editName = (EditText) findViewById(R.id.course_input_name);
        editStart = (EditText) findViewById(R.id.course_input_start);
        editEnd = (EditText)findViewById(R.id.course_input_end);
        editMentor = (EditText)findViewById(R.id.course_input_mentor);

        addAssessmentsBtn = (Button) findViewById(R.id.assment_button);

        //when CourseAddActivity is opened from term and dosnt have a course to add an assessment to
        if (ObjectViewProvider.isTermUri(uri)) {
            //add function
            action = Intent.ACTION_INSERT;
            setTitle("New Course");
            addAssessmentsBtn.setVisibility(View.INVISIBLE);
            term = uri.getLastPathSegment();

        } else {//opened from CourseDetail
            action = Intent.ACTION_EDIT;
            courseFilter = DBOpenHelper.COURSE_ID + " = " + uri.getLastPathSegment();
            Cursor courseCursor = getContentResolver().query(uri, DBOpenHelper.ALL_COURSE_COLUMNS, null,
                    null, null);
            DatabaseUtils.dumpCursor(courseCursor);
            if (courseCursor != null && courseCursor.getCount() > 0) {
                courseCursor.moveToFirst();
                setTitle("Edit " + courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSE_TEXT)));
                courseCursor.moveToFirst();

                oldName = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSE_TEXT));
                oldStart = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSE__START));
                oldEnd = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSE_END));
                oldMentor = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR));
                term = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSE_TERM));

                editName.setText(oldName);
                editStart.setText(oldStart);
                editEnd.setText(oldEnd);
                editMentor.setText(oldMentor);


                //when the button is click the the
                addAssessmentsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishEditing();


                        Intent intent = new Intent(CourseAddActivity.this, AssessmentAddActivity.class);
                        intent.putExtra(CourseViewProvider.COURSE_CONTENT_TYPE, uri);
                        startActivityForResult(intent, ASSESSMENTS_REQUEST_CODE);
                        Log.d("actionAddTerm", "test =--->");
                    }
                });


            }
            Log.d("db CAA " , "uri " + uri + " term " +term);

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, courseFilter + " " + uri, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        /*   getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
    }

    public void setAddAssessments(MenuItem item) {
        Intent intent = new Intent(CourseAddActivity.this, CourseAddActivity.class);
        intent.putExtra(CourseViewProvider.COURSE_CONTENT_TYPE, uri);
        startActivityForResult(intent, ASSESSMENTS_REQUEST_CODE);
        Log.d("actionAddTerm", "test =--->");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d("CAA onCreateOptionsMenu",  " check the menu");
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        } else {
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
            case R.id.action_delete:
                deleteCourse();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteCourse() {
        DialogInterface.OnClickListener dialogClickListener =
                //todo check if there are any assessments attached
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //db management
                            getContentResolver().delete(CourseViewProvider.COURSE_URI, courseFilter, null);
                            setResult(RESULT_OK);
                            finish();
                            //restartLoader();
                            //delete assessments as well
                            Toast.makeText(CourseAddActivity.this,
                                    getString(R.string.all_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();


    }
//    private void restartLoader() {
//        getLoaderManager().restartLoader(0, null, this);
//    }

    //todo start hear next
    private void finishEditing() {
        String newName = editName.getText().toString().trim();
        String newStart = editStart.getText().toString().trim();
        String newEnd = editEnd.getText().toString().trim();
        String newMentor = editMentor.getText().toString().trim();
        //checking there is a new term if a new term then inset into the db
        switch (action) {
            case Intent.ACTION_INSERT://
                if (newName.length() == 0 && newStart.length() == 0 && newEnd.length() == 0
                        && newMentor.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertCourse(newName, newStart, newEnd, newMentor, term);
                }
                break;

            //if not new note update the exsiting note
            case Intent.ACTION_EDIT:
                //if all values are null the note will be deleted
                    if (newName.length() == 0 && newStart.length() == 0 && newEnd.length() == 0) {
                    deleteCourse();
                    //if true cancel return to list
                } else if (newName.equals(oldName) && newStart.equals(oldStart) && newEnd.equals(oldEnd)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateCourse(newName, newStart, newEnd, newMentor, term);
                }
        }
        finish();
    }

    //needs to update the whole record
    private void updateCourse(String title, String start, String end, String mentor, String term) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TEXT, title);
        values.put(DBOpenHelper.COURSE__START, start);
        values.put(DBOpenHelper.COURSE_END, start);
        values.put(DBOpenHelper.COURSE_MENTOR, mentor);
        values.put(DBOpenHelper.COURSE_TERM, term);

        getContentResolver().update(CourseViewProvider.COURSE_URI, values, courseFilter, null);
        Toast.makeText(this, "Course has been updated", Toast.LENGTH_SHORT);
        setResult(RESULT_OK);
    }


    private void insertCourse(String title, String start, String end, String mentor, String term) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TEXT, title);
        values.put(DBOpenHelper.COURSE__START, start);
        values.put(DBOpenHelper.COURSE_END, start);
        values.put(DBOpenHelper.COURSE_MENTOR, mentor);
        values.put(DBOpenHelper.COURSE_TERM, term);

        getContentResolver().insert(CourseViewProvider.COURSE_URI, values);
        setResult(RESULT_OK);

    }


    @Override
    public void onBackPressed() {
        finishEditing();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ASSESSMENTS_REQUEST_CODE | requestCode == TERM_REQUEST_CODE && resultCode == RESULT_OK) {
        
        }
    }

}
