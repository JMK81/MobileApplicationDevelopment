package com.example.mobileapplicationdevelopment;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.database.Cursor;
import android.net.Uri;
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

/**
 * An activity representing a single Term detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link TermListActivity}.
 */
public class TermDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String action;

    private TextView termName;
    private TextView termStart;
    private TextView termEnd;
    private TextView Note;

    private Uri uri;

    private String courseFilter;
    private String termFilter;
    private String term;
    private String start;
    private String end;
    private String termId;
    private CourseCursorAdapter cursorAdapter;
    private static final int COURSE_REQUEST_CODE = 2002;
    private static final int TERM_REQUEST_CODE = 1001;


    //todo list will display courses in the appbar it needs to display the term title start and end dates
    /*
     * display in appbar term title start and end dates
     * display the courses in
     * */
//fixme activity term detail --> activity term course make same structur of activity term list -> termlist -> term list content
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.term_detail_toolbar);
        setSupportActionBar(toolbar);



        //check if the activity is starting a new activity or returning to an exsiting state
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            uri = intent.getParcelableExtra(ObjectViewProvider.TERM_CONTENT_TYPE);
            termId = uri.getLastPathSegment();
            term = intent.getStringExtra("termTitle");
            start = intent.getStringExtra("termStart");
            end = intent.getStringExtra("termEnd");
            setTitle(term);

        } else {
            uri = savedInstanceState.getParcelable(ObjectViewProvider.TERM_CONTENT_TYPE);
            restartLoader();
            Intent intent = savedInstanceState.getParcelable("intent");
            Log.d("TermDA", "SavedInstaceState != null loader reset");
            termId = uri.getLastPathSegment();
            term = intent.getStringExtra("termTitle");
            start = intent.getStringExtra("termStart");
            end = intent.getStringExtra("termEnd");
            setTitle(term);
        }

        TextView termStart = (TextView) findViewById(R.id.activity_course_detail_start);
        termStart.setText(start);
        TextView termEnd = (TextView) findViewById(R.id.activity_course_detail_end);
        termEnd.setText(end);
        courseFilter = DBOpenHelper.COURSE_TERM + " = " + uri.getLastPathSegment();

        Cursor courseCursor = getContentResolver().query(uri, DBOpenHelper.ALL_COURSE_COLUMNS, courseFilter,
                null, null);

        ListView list = (ListView) findViewById(R.id.activity_course_detail_list);
        if (courseCursor != null && courseCursor.getCount()>0) {
            //if there are any matches to the query call  && courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.TERM_ID)
            courseCursor.moveToFirst();
            cursorAdapter = new CourseCursorAdapter(this, courseCursor, 0);
            list.setAdapter(cursorAdapter);
        }
        getLoaderManager().initLoader(0, null, this);



        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TermDetailActivity.this, CourseDetailActivity.class);
                Uri uri = Uri.parse(CourseViewProvider.COURSE_URI + "/" + id);
                intent.putExtra(CourseViewProvider.COURSE_CONTENT_TYPE, uri);
                startActivityForResult(intent, COURSE_REQUEST_CODE);


            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.term_detail_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TermDetailActivity.this, CourseAddActivity.class);
                intent.putExtra(ObjectViewProvider.TERM_CONTENT_TYPE, uri);
                startActivityForResult(intent, COURSE_REQUEST_CODE);
            }
        });


    }

    public void actionEdit() {
        Intent intent = new Intent(TermDetailActivity.this, EditorActivity.class);
        intent.putExtra(ObjectViewProvider.TERM_CONTENT_TYPE, uri);
        startActivityForResult(intent, COURSE_REQUEST_CODE);
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
                //edit or delete term
                Intent intent = new Intent(TermDetailActivity.this, EditorActivity.class);
                intent.putExtra(ObjectViewProvider.TERM_CONTENT_TYPE, uri);
                startActivityForResult(intent, TERM_REQUEST_CODE);
                break;
        }
        return true;
    }

    private void restartLoader() {

        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CourseViewProvider.COURSE_URI, null, null,
                null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(cursorAdapter != null && cursorAdapter.getCount() >0)
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    if(cursorAdapter != null && cursorAdapter.getCount()>0) {
        cursorAdapter.swapCursor(null);
    }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COURSE_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {

        bundle.putParcelable(ObjectViewProvider.TERM_CONTENT_TYPE, uri);
        Intent intent = getIntent();
        intent.putExtra("termTitle", term);
        intent.putExtra("termStart", start);
        intent.putExtra("termEnd", end);
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