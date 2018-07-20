package com.example.mobileapplicationdevelopment;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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
import android.widget.Toast;


public class TermListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private TermCursorAdapter cursorAdapter;
    private static final int TERM_REQUEST_CODE = 1001;
    String TAG = "TermListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_term_list);
        Toolbar toolbar = findViewById(R.id.view_toolbar);
        setSupportActionBar(toolbar);
        Uri uri = ObjectViewProvider.CONTENT_URI;
        //need a Cursor adapter constant title
        Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_TERM_COLUMNS, null,
                null, null);
        cursor.moveToFirst();
        Log.d(TAG, "cursor count: " + cursor.getCount());
        cursorAdapter = new TermCursorAdapter(this, cursor, 0);

        ListView list = findViewById(R.id.view_list);

        list.setAdapter(cursorAdapter);

        getLoaderManager().initLoader(0, null, this);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //on click take the item click and reset the cusor adapter to view in
                //termListActivity content id and search the db for items that have
                Intent intent = new Intent(TermListActivity.this, TermDetailActivity.class);
                Uri uri = Uri.parse(ObjectViewProvider.CONTENT_URI + "/" + id);
                intent.putExtra(ObjectViewProvider.TERM_CONTENT_TYPE, uri);
                startActivityForResult(intent, TERM_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Cursor cursorUpdate = getContentResolver().query(ObjectViewProvider.CONTENT_URI, DBOpenHelper.ALL_TERM_COLUMNS, null,
                null, null);
        cursorUpdate.moveToFirst();
        cursorAdapter.swapCursor(cursorUpdate);
        Log.d(TAG, "cursorUpdate count: " + cursorUpdate.getCount());

    }

    public void actionAdd(MenuItem item) {
        Intent intent = new Intent(TermListActivity.this, EditorActivity.class);
        startActivityForResult(intent, TERM_REQUEST_CODE);
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
                restartLoader();
                break;
            case R.id.action_add:
                actionAdd(item);
                break;
            case R.id.action_add_alarm:
                Intent intent = new Intent(TermListActivity.this, SetNotification.class);
                startActivityForResult(intent, TERM_REQUEST_CODE);
                break;
            case R.id.action_edit:
                Toast.makeText(this, "Select an item first", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_delete:
                Toast.makeText(this, "Select an item first", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TERM_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
        }

    }
}
