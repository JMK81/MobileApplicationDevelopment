package com.example.mobileapplicationdevelopment;

import android.app.LoaderManager;
import android.content.CursorLoader;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class CourseDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri uri;
    private String assessmentFilter;
    private AssessmentsCursorAdapter assessmentAdapter;
    private TextView title;
    private TextView start;
    private TextView end;
    private TextView mentor;
    private TextView mentorPhone;
    private TextView mentorEmail;

    ImageButton assessmentAddBtn;

    private String courseTitle;
    private String courseStart;
    private String courseEnd;
    private String courseMentor;
    private String courseMentorPhone;
    private String courseMentorEmail;
    private String courseId;
    private String term;

    private static final int ASSESSMENT_REQUEST_CODE = 4004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        title = (TextView) findViewById(R.id.course_item_name);
        start = (TextView) findViewById(R.id.course_item_start);
        end = (TextView) findViewById(R.id.course_item_end);
        mentor = (TextView) findViewById(R.id.course_item_mentor);
        mentorEmail = (TextView) findViewById(R.id.course_mentor_email);
        mentorPhone = (TextView) findViewById(R.id.course_mentor_phone);
        assessmentAddBtn = (ImageButton) findViewById(R.id.assessment_add_btn);

        if (savedInstanceState == null) {
            //assigning string from the saveInstanceState Bund
            Intent intent = getIntent();
            uri = intent.getParcelableExtra(CourseViewProvider.COURSE_CONTENT_TYPE);
            Log.d("cda if test", "saved instance state is null and");
            Log.d("cda if intent :)", intent.toString());
            Log.d("cda if uri print ", uri.getLastPathSegment());
        } else {//only when retruning form the add/edit
            uri = savedInstanceState.getParcelable(CourseViewProvider.COURSE_CONTENT_TYPE);
            Log.d("cda   else", "uri returning form edit: " + uri.toString());
        }
        courseId = uri.getLastPathSegment();
        String courseFilter = DBOpenHelper.COURSE_ID + " = " + courseId;
        Cursor course = getContentResolver().query(uri, DBOpenHelper.ALL_COURSE_COLUMNS, courseFilter,
                null, null);
        course.moveToFirst();
        DatabaseUtils.dumpCursor(course);
        courseTitle = course.getString(course.getColumnIndex(DBOpenHelper.COURSE_TEXT));
        courseStart = course.getString(course.getColumnIndex(DBOpenHelper.COURSE__START));
        courseEnd = course.getString(course.getColumnIndex(DBOpenHelper.COURSE_END));
        courseMentor = course.getString(course.getColumnIndex(DBOpenHelper.COURSE_MENTOR));
        courseMentorPhone = course.getString(course.getColumnIndex(DBOpenHelper.MENTOR_PHONE));
        courseMentorEmail = course.getString(course.getColumnIndex(DBOpenHelper.MENTOR_EMAIL));
        term = course.getString(course.getColumnIndex(DBOpenHelper.COURSE_TERM));
        course.close();

        title.setText(courseTitle);
        start.setText(courseStart);
        end.setText(courseEnd);
        mentor.setText(courseMentor);
        mentorPhone.setText(courseMentorPhone);
        mentorEmail.setText(courseMentorEmail);

        assessmentFilter = DBOpenHelper.ASSESSMENT_COURSE + " = " + courseId;

        Cursor assessmentCursor = getContentResolver().query(AssessmentViewProvider.ASSESSMENT_URI, DBOpenHelper.ALL_ASSESSMENTS_COLUMNS,
                assessmentFilter, null, null);


        if (assessmentCursor != null && assessmentCursor.getCount() > 0) {
            assessmentCursor.moveToFirst();
            //cursor addapter needs to referance the assessments table
            assessmentAdapter = new AssessmentsCursorAdapter(this, assessmentCursor, 0);
            ListView list = (ListView) findViewById(R.id.course_detail_list);
            list.setAdapter(assessmentAdapter);
            getLoaderManager().initLoader(0, null, this);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(CourseDetailActivity.this, AssessmentDetailActivity.class);
                    Uri uri = Uri.parse(AssessmentViewProvider.ASSESSMENT_URI + "/" + id);
                    intent.putExtra(AssessmentViewProvider.ASSESSMENT_CONTENT_TYPE, uri);
                    startActivityForResult(intent, ASSESSMENT_REQUEST_CODE);

                }
            });
        }
        assessmentAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseDetailActivity.this, AssessmentAddActivity.class);
                intent.putExtra(CourseViewProvider.COURSE_CONTENT_TYPE, uri);
                intent.putExtra("course", courseId);
                startActivityForResult(intent, ASSESSMENT_REQUEST_CODE);
                Snackbar.make(v, "The add buton was pressed", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getLoaderManager().initLoader(0, null, this);

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
        Intent intent = new Intent(CourseDetailActivity.this, CourseAddActivity.class);
        intent.putExtra(CourseViewProvider.COURSE_CONTENT_TYPE, uri);
        startActivityForResult(intent, ASSESSMENT_REQUEST_CODE);
        Log.d("courseDetailActivity", "=-->");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, AssessmentViewProvider.ASSESSMENT_URI,
                null, null, null, null);
    }

    private void restartLoader() {

        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (assessmentAdapter != null && assessmentAdapter.getCount() > 0) {
            assessmentAdapter.swapCursor(data);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resuldCode, Intent data) {
        if (requestCode == ASSESSMENT_REQUEST_CODE && requestCode == RESULT_OK) {
            restartLoader();
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (assessmentAdapter != null && assessmentAdapter.getCount() > 0) {
            assessmentAdapter.swapCursor(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {

       /* bundle.putParcelable(CourseViewProvider.COURSE_CONTENT_TYPE, uri);
        Log.d("cda save instance", uri.toString());*/
        super.onSaveInstanceState(bundle);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceStart) {

        super.onRestoreInstanceState(savedInstanceStart);

    }

    @Override
    public void onResume() {

        super.onResume();


    }

}
