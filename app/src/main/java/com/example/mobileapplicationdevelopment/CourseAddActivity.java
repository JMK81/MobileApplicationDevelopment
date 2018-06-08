package com.example.mobileapplicationdevelopment;

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

public class CourseAddActivity extends AppCompatActivity {
    String courseFilter;
    String action;

    EditText courseName;
    EditText courseStart;
    EditText courseEnd;
    EditText courseMentor;
    private static final int ASSESSMENTS_REQUEST_CODE = 3003;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setContentView(R.layout.activity_course_add);


        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(CourseViewProvider.COURSE_CONTENT_TYPE);

        if(uri == null){
            //add function
            action = Intent.ACTION_INSERT;
            setTitle("New Course");
        }else{
            action = Intent.ACTION_EDIT;
            courseFilter = DBOpenHelper.COURSE_ID + " = " + uri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COURSE_COLLUMS, courseFilter,
                    null, null);
            setTitle("Edit " + cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_TEXT)));

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void actionAdd(MenuItem item) {
        Intent intent = new Intent(CourseAddActivity.this, CourseAddActivity.class);
        startActivityForResult(intent, ASSESSMENTS_REQUEST_CODE);
        Log.d("actionAddTerm", "test =--->");

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trem_list, menu);
        return true;
    }
}
