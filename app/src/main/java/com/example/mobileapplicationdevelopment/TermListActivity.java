package com.example.mobileapplicationdevelopment;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
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


public class TermListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private TermCursorAdapter cursorAdapter;
    private static final int TERM_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_term_list_toolbar);
        setSupportActionBar(toolbar);

        cursorAdapter = new TermCursorAdapter(this, null, 0);

        //todo get the mainActivity to display current term
        //todo add start date and end date to the display

        ListView list = (ListView) findViewById(R.id.activity_term_detail_list);

            list.setAdapter(cursorAdapter);

            Log.d("ListVeiw", "Error with cursorAdapter");
            Toast.makeText(TermListActivity.this,
                    "There is no terms created", Toast.LENGTH_SHORT).show();


        getLoaderManager().initLoader(0, null, this);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TermListActivity.this, TermCourseActivity.class);
                Uri uri = Uri.parse(ObjectViewProvider.CONTENT_URI + "/" + id);
                intent.putExtra(ObjectViewProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, TERM_REQUEST_CODE);
                //todo when the item is clicked term detail will open with courses
            }
        });


    }
    //fixme this may be were the error occors
    private void insertTerm(String termText) {

        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_NAME, termText);
        Uri termUri = getContentResolver().insert(ObjectViewProvider.CONTENT_URI, values);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_add_term:
                insertSampleData();
                break;
            case R.id.action_delete_term:
                deleteAll();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAll() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //db management
                            getContentResolver().delete(ObjectViewProvider.CONTENT_URI, null, null);

                            restartLoader();
                            Toast.makeText(TermListActivity.this,
                                    getString(R.string.all_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void insertSampleData() {
        insertTerm("Simple note");
        insertTerm("Multi line \nnote");
        insertTerm("Very long note with a lot of text that exceeds the width of the screen by a lot link its so " +
                "far \nits really amazing how long the note is");
        restartLoader();
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
        Intent intent = new Intent(this, AddTermActivity.class);
        startActivityForResult(intent, TERM_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == TERM_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
        }

    }
}
