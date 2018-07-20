package com.example.mobileapplicationdevelopment;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    boolean canDelete = true;

    ListView list;
    String TAG = "TermDetailActivity";

    private static final int COURSE_REQUEST_CODE = 2002;
    private static final int TERM_REQUEST_CODE = 1001;




    /*
     * display in appbar term title start and end dates
     * display the courses in
     * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);
        Toolbar toolbar = findViewById(R.id.term_detail_toolbar);
        setSupportActionBar(toolbar);
        termStart = findViewById(R.id.activity_course_detail_start);
        termEnd = findViewById(R.id.activity_course_detail_end);

        cursorAdapter = new CourseCursorAdapter(this, null, 0);

        list = findViewById(R.id.activity_course_detail_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TermDetailActivity.this, CourseDetailActivity.class);
                Uri uri = Uri.parse(CourseViewProvider.COURSE_URI + "/" + id);
                intent.putExtra(CourseViewProvider.COURSE_CONTENT_TYPE, uri);
                startActivityForResult(intent, COURSE_REQUEST_CODE);
            }
        });

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            uri = intent.getParcelableExtra(ObjectViewProvider.TERM_CONTENT_TYPE);

        } else {
            uri = savedInstanceState.getParcelable(ObjectViewProvider.TERM_CONTENT_TYPE);
        }

        termId = uri.getLastPathSegment();
        courseFilter = DBOpenHelper.COURSE_TERM + " = " + termId;
        String termFilter = DBOpenHelper.TERM_ID + " = " + termId;
        Cursor termCursor = getContentResolver().query(uri, DBOpenHelper.ALL_TERM_COLUMNS, termFilter,
                null, null);
        setLocalData(termCursor);

        courseFilter = DBOpenHelper.COURSE_TERM + " = " + termId;

        Cursor courseCursor = getContentResolver().query(CourseViewProvider.COURSE_URI, DBOpenHelper.ALL_COURSE_COLUMNS, courseFilter,
                null, null);

        if (courseCursor != null && courseCursor.getCount() > 0) {
            //if there are any matches to the query call  && courseCursor.getString(courseCursor.getColumnIndex(DBOpenHelper.TERM_ID)
            courseCursor.moveToFirst();

            list.setAdapter(cursorAdapter);

            canDelete = false;

            cursorAdapter = new CourseCursorAdapter(this, courseCursor, 0);
            list.setAdapter(cursorAdapter);
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private void setLocalData(Cursor termCursor) {

        termCursor.moveToFirst();
        term = termCursor.getString(termCursor.getColumnIndex(DBOpenHelper.TERM_NAME));
        start = termCursor.getString(termCursor.getColumnIndex(DBOpenHelper.TERM_START));
        end = termCursor.getString(termCursor.getColumnIndex(DBOpenHelper.TERM_END));
        setTitle(term);
        termStart.setText(start);
        termEnd.setText(end);
        Log.d(TAG, "termName: " + termId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigate_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


            switch (id) {
                case R.id.action_home:
                    Intent intentHome = new Intent(TermDetailActivity.this, TermListActivity.class);
                    startActivityForResult(intentHome, TERM_REQUEST_CODE);
                    break;
                case R.id.action_add:
                    Intent intentAdd = new Intent(TermDetailActivity.this, CourseAddActivity.class);
                    intentAdd.putExtra(ObjectViewProvider.TERM_CONTENT_TYPE, uri);
                    startActivityForResult(intentAdd, COURSE_REQUEST_CODE);
                    break;
                case R.id.action_add_alarm:
                    Intent intent = new Intent(TermDetailActivity.this, SetNotification.class);
                    startActivityForResult(intent, TERM_REQUEST_CODE);
                    break;
                case R.id.action_edit:
                    Intent i = new Intent(TermDetailActivity.this, EditorActivity.class);
                    i.putExtra("canDelete", canDelete);
                    i.putExtra(ObjectViewProvider.TERM_CONTENT_TYPE, uri);
                    startActivityForResult(i, TERM_REQUEST_CODE);
                    break;
                case R.id.action_delete:
                    deleteTerm();
                    break;

            }
            return true;
        }


    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    private void deleteTerm() {

        Cursor cursorUpdate = getContentResolver().query(CourseViewProvider.COURSE_URI, DBOpenHelper.ALL_COURSE_COLUMNS,
                DBOpenHelper.COURSE_TERM + " = " + termId,
                null, null);
        DatabaseUtils.dumpCursor(cursorUpdate);
        if (cursorUpdate.getCount() == 0) {
            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            if (button == DialogInterface.BUTTON_POSITIVE) {
                                //db management
                                getContentResolver().delete(ObjectViewProvider.CONTENT_URI, termFilter, null);
                                setResult(RESULT_OK);
                                Cursor cursorUpdate = getContentResolver().query(ObjectViewProvider.CONTENT_URI, DBOpenHelper.ALL_TERM_COLUMNS, null,
                                        null, null);
                                DatabaseUtils.dumpCursor(cursorUpdate);
                                Intent intent = new Intent(TermDetailActivity.this, TermListActivity.class);
                                startActivityForResult(intent, TERM_REQUEST_CODE);
                                finish();

                                Toast.makeText(TermDetailActivity.this,
                                        getString(R.string.delete),
                                        Toast.LENGTH_SHORT).show();
                            } else if (
                                    button == DialogInterface.BUTTON_NEGATIVE) {
                            }
                        }
                    };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.are_you_sure))
                    .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                    .show();
        }else{
            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            if (button == DialogInterface.BUTTON_POSITIVE) {
                                finish();
                            }
                        }
                    };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("The term you are trying to delete contains courses you will need to delete the courses first")
                    .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                    .show();
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this, CourseViewProvider.COURSE_URI, null, courseFilter,
                null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (cursorAdapter != null && cursorAdapter.getCount() > 0)
            cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (cursorAdapter != null && cursorAdapter.getCount() > 0) {
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
        super.onRestoreInstanceState(savedInstanceStart);
        savedInstanceStart.putParcelable(ObjectViewProvider.TERM_CONTENT_TYPE, uri);
        restartLoader();
    }

    @Override
    public void onResume() {

        Cursor termCursorUpdate = getContentResolver().query(uri, DBOpenHelper.ALL_TERM_COLUMNS, termFilter,
                null, null);
        termCursorUpdate.moveToFirst();
        setLocalData(termCursorUpdate);

        Cursor courseCursorUpdate = getContentResolver().query(CourseViewProvider.COURSE_URI, DBOpenHelper.ALL_COURSE_COLUMNS, courseFilter,
                null, null);
        courseCursorUpdate.moveToFirst();
        int count = 1;



        if (cursorAdapter != null) {
            if (courseCursorUpdate != null) {
                canDelete = false;
                cursorAdapter = new CourseCursorAdapter(this, courseCursorUpdate, 0);
                list.setAdapter(cursorAdapter);
            }
        } else if (courseCursorUpdate != null && courseCursorUpdate.getCount() > 0) {
            cursorAdapter.swapCursor(courseCursorUpdate);

        }
        super.onResume();
    }
    @Override
    public void onBackPressed(){
        finish();
    }
}