package com.example.mobileapplicationdevelopment;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CourseDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri uri;
    private String assessmentFilter;
    private AssessmentsCursorAdapter assessmentAdapter;
    private TextView title;
    private TextView start;
    private TextView end;
    private TextView mentor;
    private String courseTitle;
    private String courseStart;
    private String courseEnd;
    private String courseMentor;
    private String courseId;
    private String term;




    private static final int ASSESSMENT_REQUEST_CODE = 4004;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title = (TextView) findViewById(R.id.course_item_name);
        start = (TextView) findViewById(R.id.course_item_start);
        end = (TextView) findViewById(R.id.course_item_end);
        mentor = (TextView) findViewById(R.id.course_item_mentor);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            uri = intent.getParcelableExtra(CourseViewProvider.COURSE_CONTENT_TYPE);
            term = intent.getStringExtra("term");
            courseTitle = intent.getStringExtra("title");
            courseStart = intent.getStringExtra("start");
            courseEnd = intent.getStringExtra("end");
            courseMentor = intent.getStringExtra("mentor");
            long id = 0;
            courseId = intent.getLongExtra("courseId", id)+"";
        } else {
            uri = savedInstanceState.getParcelable(CourseViewProvider.COURSE_CONTENT_TYPE);
            Intent intent = savedInstanceState.getParcelable("intent");
            courseId = intent.getStringExtra("courseId");
            courseTitle = intent.getStringExtra("courseTitle");
            courseStart = intent.getStringExtra("termStart");
            courseEnd = intent.getStringExtra("termEnd");
            courseMentor = intent.getStringExtra("mentor");
            term = intent.getStringExtra("termId");
        }

            title.setText(courseTitle);
            start.setText(courseStart);
            end.setText(courseEnd);
            mentor.setText(courseMentor);
            assessmentFilter = DBOpenHelper.ASSESSMENT_COURSE + " = " + courseId;
            Log.d("db assessmentFilter", assessmentFilter + " ------->");
        Cursor assessmentCursor = getContentResolver().query(AssessmentViewProvider.ASSESSMENT_URI, DBOpenHelper.ALL_ASSESSMENTS_COLUMNS,
                assessmentFilter, null, null);

        if (assessmentCursor != null && assessmentCursor.getCount()>0) {
            assessmentCursor.moveToFirst();
            //cursor addapter needs to referance the assessments table
            Log.d("db assessments call ", assessmentCursor.getCount()+" ***");
                assessmentAdapter = new AssessmentsCursorAdapter(this, assessmentCursor, 0);
                ListView list = (ListView) findViewById(R.id.course_detail_list);
                list.setAdapter(assessmentAdapter);

            DatabaseUtils.dumpCursor(assessmentCursor);
            getLoaderManager().initLoader(0, null, this);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(CourseDetailActivity.this, AssessmentDetailActivity.class);
                    Uri uri = Uri.parse(CourseViewProvider.COURSE_URI + "/" + id);
                    Log.d("TDA course id", "  " + id);
                    intent.putExtra(CourseViewProvider.COURSE_CONTENT_TYPE, uri);
                    TextView title = (TextView) view.findViewById(R.id.tvTerm);
                    String assessmentTitle = title.getText().toString();
                    TextView ad = (TextView) view.findViewById(R.id.term_start);
                    String assessmentDate = ad.getText().toString();
                    TextView at = (TextView) view.findViewById(R.id.term_end);
                    String assessmentEnd = at.getText().toString();
                    intent.putExtra("title", assessmentTitle);
                    intent.putExtra("date", assessmentDate);
                    intent.putExtra("time", assessmentEnd);
                    intent.putExtra("course", id);
                    startActivityForResult(intent, ASSESSMENT_REQUEST_CODE);


                }
            });

        }
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
    //TODO need to be able to click on mentor

    public void actionAdd() {
        Intent intent = new Intent(CourseDetailActivity.this, AssessmentAddActivity.class);
        intent.putExtra("courseId", courseId);
        startActivityForResult(intent, ASSESSMENT_REQUEST_CODE);
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

    private void restartLoader(){
        getLoaderManager().restartLoader(0, null, this);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(assessmentAdapter != null && assessmentAdapter.getCount() >0){
            assessmentAdapter.swapCursor(data);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resuldCode, Intent data){
        if(requestCode == ASSESSMENT_REQUEST_CODE && requestCode == RESULT_OK){
            restartLoader();
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(assessmentAdapter != null && assessmentAdapter.getCount()>0){
            assessmentAdapter.swapCursor(null);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle bundle) {

        bundle.putParcelable(ObjectViewProvider.TERM_CONTENT_TYPE, uri);
        Intent intent = getIntent();
        intent.putExtra("title", courseTitle);
        intent.putExtra("start", courseStart);
        intent.putExtra("end", courseEnd);
        intent.putExtra("mentor", courseMentor);
        intent.putExtra("courseId", courseId);
        intent.putExtra("term", term);
        bundle.putParcelable("intent", intent);


        super.onSaveInstanceState(bundle);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceStart) {
        // todo
        super.onRestoreInstanceState(savedInstanceStart);
        restartLoader();
    }

}
