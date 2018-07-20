package com.example.mobileapplicationdevelopment;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

public class Note extends AppCompatActivity {

    MultiAutoCompleteTextView editNote;
    TextView noteTitle;

    String note;
    String title;
    String courseId;
    String TAG = "Note";



    private static final int NOT_REQUEST_CODE = 5005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        note = intent.getStringExtra("note");
        title = intent.getStringExtra("courseTitle");
        courseId = intent.getStringExtra("courseId");
    Log.d(TAG, "Course title: " + title + ", course id: " + courseId);
        noteTitle = findViewById(R.id.note_title);
        noteTitle.setText(title);
        editNote = findViewById(R.id.multiAutoCompleteTextView);
        editNote.setText(note);
    }

    private void updateCourse(String note) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_NOTE, note);
        String selection = DBOpenHelper.COURSE_ID + " = " + courseId;
        getContentResolver().update(CourseViewProvider.COURSE_URI, values, selection, null);
        Toast.makeText(this, "Course has been updated", Toast.LENGTH_SHORT);
        setResult(RESULT_OK);
        Log.d(TAG, "udate Course was call note is trying to be updated");
    }


    @Override
    public void onBackPressed() {
        String newNote = editNote.getText().toString().trim();
        if (newNote.equals(note)) {
            Log.d(TAG, "note hasn't changed");
            finish();
        } else {
            updateCourse(newNote);
            Log.d(TAG, "updateCourse was called from onBackPressed");
            finish();
        }
    }

}
