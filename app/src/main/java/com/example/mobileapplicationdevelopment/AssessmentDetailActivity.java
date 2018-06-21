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
import android.widget.TextView;

import org.w3c.dom.Text;

public class AssessmentDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri uri;
    private String noteFilter;
    private NoteCursorAdapter noteAdapter;
    private TextView title;
    private TextView date;
    private TextView time;
    private ListView list;

    private String assessmentTitle;
    private String assessmentDate;
    private String assessmentTime;
    private String courseId;

    private static final int ASSESSMENT_REQUEST_CODE = 4004;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = (TextView) findViewById(R.id.course_item_name);
        date = (TextView) findViewById(R.id.course_item_start);
        time = (TextView) findViewById(R.id.course_item_end);

        Intent intent;
        if(savedInstanceState == null){
            intent = getIntent();
            uri = intent.getParcelableExtra(AssessmentViewProvider.ASSESSMENT_CONTENT_TYPE);
            assessmentTitle = intent.getStringExtra("title");
            assessmentDate = intent.getStringExtra("date");
            assessmentTime = intent.getStringExtra("time");
            courseId = intent.getStringExtra("course");

        }else{
            uri = savedInstanceState.getParcelable(AssessmentViewProvider.ASSESSMENT_CONTENT_TYPE);
            intent = savedInstanceState.getParcelable("intent");
            assessmentTitle = intent.getStringExtra("title");
            assessmentDate = intent.getStringExtra("date");
            assessmentTime = intent.getStringExtra("time");
            courseId = intent.getStringExtra("course");
        }

        title = (TextView) findViewById(R.id.assessment_item_name);
        date = (TextView) findViewById(R.id.assessment_item_date);
        time = (TextView) findViewById(R.id.assessment_item_date);


        title.setText(assessmentTitle);
        date.setText(assessmentDate);
        time.setText(assessmentTime);

        uri = intent.getParcelableExtra(CourseViewProvider.COURSE_CONTENT_TYPE);

        noteFilter = DBOpenHelper.NOTE_ASSESSMENT + " = " + uri.getLastPathSegment();
        Cursor courseCursor = getContentResolver().query(uri, DBOpenHelper.ALL_COURSE_COLUMNS, null,
                null, null);



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

    public void actionAdd() {
        Intent intent = new Intent(AssessmentDetailActivity.this, CourseAddActivity.class);
        startActivityForResult(intent,ASSESSMENT_REQUEST_CODE);
        Log.d("actionAddTerm", "test =--->");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_edit:
                actionEdit();
                break;
        }
        return true;
    }



    public void actionEdit() {
        Intent intent = new Intent(AssessmentDetailActivity.this, AssessmentAddActivity.class);
        intent.putExtra(AssessmentViewProvider.ASSESSMENT_CONTENT_TYPE, uri);
        startActivityForResult(intent, ASSESSMENT_REQUEST_CODE);
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
    @Override
    public void onSaveInstanceState(Bundle bundle){
        bundle.putParcelable(AssessmentViewProvider.ASSESSMENT_CONTENT_TYPE, uri);
        Intent intent = getIntent();
        intent.putExtra("title", assessmentTitle);
        intent.putExtra("date", assessmentDate);
        intent.putExtra("time", assessmentTime);
        intent.putExtra("course", courseId);
        bundle.putParcelable("intent", intent);
        super.onSaveInstanceState(bundle);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstatnceState){
        super.onRestoreInstanceState(savedInstatnceState);
        restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }
}
