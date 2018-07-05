package com.example.mobileapplicationdevelopment;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AssessmentDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri uri;
    private String assessmentFilter;
    private TextView type;
    private TextView date;
    private TextView time;
    private TextView note;


    private String assessmentTitle;
    private String assessmentDate;
    private String assessmentTime;
    private String assessmentType;
    private String assessmentNote;
    private String courseId;

    private static final int ASSESSMENT_REQUEST_CODE = 4004;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent;
        if (savedInstanceState == null) {
            intent = getIntent();
            uri = intent.getParcelableExtra(AssessmentViewProvider.ASSESSMENT_CONTENT_TYPE);

        } else {
            uri = savedInstanceState.getParcelable(AssessmentViewProvider.ASSESSMENT_CONTENT_TYPE);

        }
        assessmentFilter = DBOpenHelper.ASSESSMENT_ID + " = " + uri.getLastPathSegment();
        Cursor assessmentCursor = getContentResolver().query(uri, DBOpenHelper.ALL_ASSESSMENTS_COLUMNS, assessmentFilter,
                null, null);
        assessmentCursor.moveToFirst();
        DatabaseUtils.dumpCursor(assessmentCursor);
        assessmentTitle = assessmentCursor.getString(assessmentCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TITLE));
        assessmentType = assessmentCursor.getString(assessmentCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TYPE));
        assessmentDate = assessmentCursor.getString(assessmentCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DATE));
        assessmentTime = assessmentCursor.getString(assessmentCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TIME));
        assessmentNote = assessmentCursor.getString(assessmentCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTE));

        getLoaderManager().initLoader(0, null, this);

        type = (TextView) findViewById(R.id.assessment_item_type);
        date = (TextView) findViewById(R.id.assessment_item_date);
        time = (TextView) findViewById(R.id.assessment_item_time);
        note = (TextView) findViewById(R.id.assessment_item_note);

        setTitle(assessmentTitle);
        Log.d("ada ---->", assessmentTitle + " " +assessmentType);
        type.setText(assessmentType);
        date.setText(assessmentDate);
        time.setText(assessmentTime);
        note.setText(assessmentNote);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_edit:
                actionEdit();
                break;
            case R.id.action_delete:
                actionDelete();
                break;
        }
        return true;
    }

    private void actionDelete() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //db management
                            getContentResolver().delete(AssessmentViewProvider.ASSESSMENT_URI, assessmentFilter, null);
                            setResult(RESULT_OK);
                            finish();

                            Toast.makeText(AssessmentDetailActivity.this,
                                    getString(R.string.delete),
                                    Toast.LENGTH_SHORT).show();
                        } else if (button == DialogInterface.BUTTON_NEGATIVE) {

                        }

                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
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
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelable(AssessmentViewProvider.ASSESSMENT_CONTENT_TYPE, uri);
        Intent intent = getIntent();
        intent.putExtra("title", assessmentTitle);
        intent.putExtra("date", assessmentDate);
        intent.putExtra("time", assessmentTime);
        intent.putExtra("course", courseId);
        intent.putExtra("note", assessmentNote);
        bundle.putParcelable("intent", intent);
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstatnceState) {
        super.onRestoreInstanceState(savedInstatnceState);
        restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }
}
