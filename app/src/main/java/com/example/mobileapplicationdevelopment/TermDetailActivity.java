package com.example.mobileapplicationdevelopment;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.database.Cursor;
import android.net.Uri;
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

    private String termFilter;
    private CourseCursorAdapter cursorAdapter;
    private static final int COURSE_REQUEST_CODE = 2002;


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



        termStart = (TextView) findViewById(R.id.activity_course_detail_start);
        termEnd = (TextView) findViewById(R.id.activity_course_detail_end);

        Intent intent = getIntent();

        uri = intent.getParcelableExtra(ObjectViewProvider.TERM_CONTENT_TYPE);

        termFilter = DBOpenHelper.TERM_ID + "=" + uri.getLastPathSegment();


        Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COURSE_COLLUMS, termFilter,
                null, null);
        cursor.moveToFirst();

        insertNote("Simple note");
        insertNote("Multi-line\nnote");
        insertNote("Very long note with a lot of text that exceeds the width of the screen");
        restartLoader();


        //cursor is attached to term table
        setTitle(cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_NAME)));
        termStart.setText("Hello Start");//(cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START)));
        termEnd.setText("Hello End");//(cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END)));

        //fixme the cursorAdapter need to take in the parant term cursor to filter the results
        cursorAdapter = new CourseCursorAdapter(this, null, 0);
        //cusor to hold course items

        //todo connect list to a course cursor adapter
        ListView list = (ListView) findViewById(R.id.activity_course_detail_list);
        list.setAdapter(cursorAdapter);

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
/*     Intent intent = new Intent(TermListActivity.this,TermDetailActivity.class);
  *      Uri uri = Uri.parse(ObjectViewProvider.CONTENT_URI + "/" + id);
   *     intent.putExtra(ObjectViewProvider.TERM_CONTENT_TYPE, uri);
    *    startActivityForResult(intent, TERM_REQUEST_CODE);
     */

    }
    public void actionEdit(MenuItem item) {

        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(ObjectViewProvider.TERM_CONTENT_TYPE, uri);
        startActivityForResult(intent, COURSE_REQUEST_CODE);
        Log.d("actionAddTerm", "test =--->");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.term_detail, menu);
        return true;
    }

    private void insertNote(String courseText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TEXT, courseText);
        Uri noteUri = getContentResolver().insert(CourseViewProvider.COURSE_URI,
                values);
        Log.d("MainActivity", "Inserted note " + noteUri.getLastPathSegment());
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
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    //TODO
//    public void openAddTerm(View view) {
//        Intent intent = new Intent(this, AddActivity.class);
//        //use this to
//        startActivityForResult(intent, TERM_REQUEST_CODE);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COURSE_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
        }

    }
}