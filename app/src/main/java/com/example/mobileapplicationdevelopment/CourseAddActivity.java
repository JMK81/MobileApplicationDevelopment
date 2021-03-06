package com.example.mobileapplicationdevelopment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

public class CourseAddActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    String courseFilter;
    String action;
    String oldName;
    String oldStart;
    String oldEnd;
    String oldMentor;
    String oldPhone;
    String oldEmail;
    String term;
    String courseId;
    String oldStatus;
    String oldNote;

    EditText editName;
    EditText editStart;
    EditText editEnd;
    EditText editMentor;
    EditText editEmail;
    EditText editPhone;
    EditText editStatus;
    EditText editNote;
    ImageButton addAssessmentsBtn;

    EditText dateTarget;
    private static final int COURSE_REQUEST_CODE = 2002;

    private static final int ASSESSMENTS_REQUEST_CODE = 3003;
    private static final int TERM_REQUEST_CODE = 1001;

    Uri uri;//term
    Uri uriCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_add);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        uriCourse = intent.getParcelableExtra(CourseViewProvider.COURSE_CONTENT_TYPE);
        editName = findViewById(R.id.course_input_name);
        editStart = findViewById(R.id.course_input_start);
        editEnd = findViewById(R.id.course_input_end);
        editMentor = findViewById(R.id.mentor_input_name);
        editEmail = findViewById(R.id.mentor_input_email);
        editPhone = findViewById(R.id.mentor_input_phone);
        editStatus = findViewById(R.id.course_input_status);
        editNote = findViewById(R.id.course_input_note);

        addAssessmentsBtn = findViewById(R.id.assessment_add_btn);

        //when CourseAddActivity is opened from term and dosnt have a course to add an assessment to
        if (uriCourse == null) {
            //add function
            uri = intent.getParcelableExtra(ObjectViewProvider.TERM_CONTENT_TYPE);
            action = Intent.ACTION_INSERT;
            setTitle("New Course");
            term = uri.getLastPathSegment();

        } else {//opened from CourseDetail
            action = Intent.ACTION_EDIT;

            courseFilter = DBOpenHelper.COURSE_ID + " = " + uriCourse.getLastPathSegment();

            Cursor courseCursor = getContentResolver().query(uriCourse, DBOpenHelper.ALL_COURSE_COLUMNS, courseFilter,
                    null, null);
            if (courseCursor != null && courseCursor.getCount() > 0) {
                courseCursor.moveToFirst();
                long id = 0;
                oldName = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSE_TEXT));
                oldStart = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSE__START));
                oldEnd = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSE_END));
                courseId = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSE_ID));
                oldMentor = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR));
                oldEmail = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.MENTOR_EMAIL));
                oldPhone = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.MENTOR_PHONE));
                term = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSE_TERM));
                oldStatus = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSE_STATUS));
                oldNote = courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.COURSE_NOTE));

                setTitle("Edit " + oldName);
                editName.setText(oldName);
                editStart.setText(oldStart);
                editEnd.setText(oldEnd);
                editMentor.setText(oldMentor);
                editEmail.setText(oldEmail);
                editPhone.setText(oldPhone);
                editStatus.setText(oldStatus);
                editNote.setText(oldNote);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
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
                          /*  getContentResolver().delete(CourseViewProvider.COURSE_URI,
                                    DBOpenHelper.ASSESSMENT_COURSE + " = " +uriCourse.getLastPathSegment(),
                                    null);*/
                            setResult(RESULT_OK);
                            finish();

                            Toast.makeText(CourseAddActivity.this,
                                    getString(R.string.all_deleted),
                                    Toast.LENGTH_SHORT).show();
                        } else if (button == DialogInterface.BUTTON_NEGATIVE) {
                            Toast.makeText(CourseAddActivity.this, "Returning to Course Add Activity",
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

    public void setStart(String s) {
        editStart.setText(s);
    }

    public void setEnd(String s) {
        editEnd.setText(s);
    }


    private void finishEditing() {
        String newName = editName.getText().toString().trim();
        String newStart = editStart.getText().toString().trim();
        String newEnd = editEnd.getText().toString().trim();
        String newMentor = editMentor.getText().toString().trim();
        String newPhone = editPhone.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();
        String newStatus = editStatus.getText().toString().trim();
        String newNote = editNote.getText().toString().trim();
        //checking there is a new term if a new term then inset into the db
        switch (action) {
            case Intent.ACTION_INSERT://
                if (newName.length() == 0 && newStart.length() == 0 && newEnd.length() == 0
                        && newMentor.length() == 0 && newPhone.length() == 0 && newEmail.length() == 0
                        && newStatus.length() == 0 && newNote.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    uriCourse = insertCourse(newName, newStart, newEnd, newMentor, newPhone, newEmail, term, newStatus, newNote);
                }
                break;

            //if not new note update the exsiting note
            case Intent.ACTION_EDIT:
                //if all values are null the note will be deleted
                if (newName.length() == 0 && newStart.length() == 0 && newEnd.length() == 0
                        && newMentor.length() == 0 && newPhone.length() == 0 && newEmail.length() == 0
                        && newStatus.length() == 0 && newNote.length() == 0) {
                    deleteCourse();
                    //if true cancel return to list
                } else if (newName.equals(oldName) && newStart.equals(oldStart) && newEnd.equals(oldEnd)
                        && newMentor.equals(oldMentor) && newPhone.equals(oldPhone) && newEmail.equals(oldEmail)
                        && newStatus.equals(oldStatus) && newNote.equals(newNote)) {

                    setResult(RESULT_CANCELED);
                    finish();
                } else {
                    updateCourse(newName, newStart, newEnd, newMentor, newPhone, newEmail, term, newStatus, newNote);
                }
                break;
        }

        finish();
    }

    //needs to update the whole record
    private void updateCourse(String title, String start, String end, String mentor,
                              String phone, String email, String term, String status, String note) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TEXT, title);
        values.put(DBOpenHelper.COURSE__START, start);
        values.put(DBOpenHelper.COURSE_END, end);
        values.put(DBOpenHelper.COURSE_MENTOR, mentor);
        values.put(DBOpenHelper.MENTOR_PHONE, phone);
        values.put(DBOpenHelper.MENTOR_EMAIL, email);
        values.put(DBOpenHelper.COURSE_TERM, term);
        values.put(DBOpenHelper.COURSE_STATUS, status);
        values.put(DBOpenHelper.COURSE_NOTE, note);

        getContentResolver().update(CourseViewProvider.COURSE_URI, values, courseFilter, null);
        Toast.makeText(this, "Course has been updated", Toast.LENGTH_SHORT);
        setResult(RESULT_OK);
    }


    private Uri insertCourse(String title, String start, String end, String mentor,
                             String phone, String email, String term, String status, String note) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TEXT, title);
        values.put(DBOpenHelper.COURSE__START, start);
        values.put(DBOpenHelper.COURSE_END, end);
        values.put(DBOpenHelper.COURSE_MENTOR, mentor);
        values.put(DBOpenHelper.MENTOR_PHONE, phone);
        values.put(DBOpenHelper.MENTOR_EMAIL, email);
        values.put(DBOpenHelper.COURSE_TERM, term);
        values.put(DBOpenHelper.COURSE_STATUS, status);
        values.put(DBOpenHelper.COURSE_NOTE, note);
        Uri i;
        i = getContentResolver().insert(CourseViewProvider.COURSE_URI, values);
        setResult(RESULT_OK);
        return i;

    }

    //add intent to pass updated data to courseDetail activity
    @Override
    public void onBackPressed() {
        finishEditing();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ASSESSMENTS_REQUEST_CODE | requestCode == TERM_REQUEST_CODE && resultCode == RESULT_OK) {

        }
    }

    public void pickStart(View v) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
        dateTarget = findViewById(R.id.course_input_start);


    }

    public void pickEnd(View v) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
        dateTarget = findViewById(R.id.course_input_end);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime());

        dateTarget.setText(currentDateString);
    }
}



