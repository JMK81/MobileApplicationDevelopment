package com.example.mobileapplicationdevelopment;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
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


    private String termFilter;

    private TermCursorAdapter cursorAdapter;
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
        //todo all add and edit function throught the same activity
        termName = (TextView) findViewById(R.id.course_detail_name);
        termStart = (TextView) findViewById(R.id.activity_course_detail_start);
        termEnd = (TextView) findViewById(R.id.activity_course_detail_end);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(ObjectViewProvider.CONTENT_ITEM_TYPE);
    //
        Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COURSE_COLLUMS, termFilter,
                null, null);
        cursor.moveToFirst();

        termFilter = DBOpenHelper.TERM_ID + "=" + uri.getLastPathSegment();

        termName.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_TEXT)));
        termStart.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE__START)));
        termEnd.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END)));

        cursorAdapter = new TermCursorAdapter(this, null, 0);

        ListView list = (ListView) findViewById(R.id.activity_course_detail_list);
        list.setAdapter(cursorAdapter);

        getLoaderManager().initLoader(0, null, this);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TermDetailActivity.this, DetailActivity.class);
                Uri uri = Uri.parse(ObjectViewProvider.CONTENT_URI + "/" + id);
                intent.putExtra(ObjectViewProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, TERM_REQUEST_CODE);

            }
        });


    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ObjectViewProvider.CONTENT_URI, null, null,
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

    public void openAddTerm(View view) {
        Intent intent = new Intent(this, AddActivity.class);
        //use this to
        startActivityForResult(intent, TERM_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TERM_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
        }

    }
}