package com.example.mobileapplicationdevelopment;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
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
import android.widget.ListView;

public class CourseDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri uri;
    private String courseFilter;
    private CourseCursorAdapter cursorAdapter;
    private static final int  COURSE_REQUEST_CODE = 3003;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        uri = intent.getParcelableExtra(CourseViewProvider.COURSE_CONTENT_TYPE);

        courseFilter = DBOpenHelper.COURSE_ID + " = " + uri.getLastPathSegment();

        Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COURSE_COLLUMS, courseFilter,
                null, null);
        cursor.moveToFirst();
        //cursor addapter needs to referance the assessments table
        ListView list = (ListView) findViewById(R.id.course_detail_list);
        list.setAdapter(cursorAdapter);

        getLoaderManager().initLoader(0, null, this);
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
        Intent intent = new Intent(CourseDetailActivity.this, CourseAddActivity.class);
        startActivityForResult(intent, COURSE_REQUEST_CODE);
        Log.d("actionAddTerm", "test =--->");

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trem_list, menu);
        return true;
    }

    public void actionEdit(MenuItem item){
        Intent intent = new Intent(this, CourseAddActivity.class);
        intent.putExtra(CourseViewProvider.COURSE_CONTENT_TYPE, uri);
        Log.d("courseDetailActivity", "=-->");
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
