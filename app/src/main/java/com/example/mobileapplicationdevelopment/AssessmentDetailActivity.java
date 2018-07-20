package com.example.mobileapplicationdevelopment;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private String TAG = "AssessmentDetailACtivity";

    private int notificationId;

    private static final int ASSESSMENT_REQUEST_CODE = 4004;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        type = findViewById(R.id.assessment_item_type);
        date = findViewById(R.id.assessment_item_date);
        time = findViewById(R.id.assessment_item_time);
        note = findViewById(R.id.assessment_item_note);

    Intent intent;
        if (savedInstanceState == null) {
            intent = getIntent();
            uri = intent.getParcelableExtra(AssessmentViewProvider.ASSESSMENT_CONTENT_TYPE);

        } else {
            uri = savedInstanceState.getParcelable(AssessmentViewProvider.ASSESSMENT_CONTENT_TYPE);

        }
        notificationId =Integer.parseInt(ASSESSMENT_REQUEST_CODE +uri.getLastPathSegment());

        assessmentFilter = DBOpenHelper.ASSESSMENT_ID + " = " + uri.getLastPathSegment();
        Cursor assessmentCursor = getContentResolver().query(uri, DBOpenHelper.ALL_ASSESSMENTS_COLUMNS, assessmentFilter,
                null, null);
        assessmentCursor.moveToFirst();
        setLocalDate(assessmentCursor);

        getLoaderManager().initLoader(0, null, this);
        FloatingActionButton fab = findViewById(R.id.addAssessmentAlarm);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssessmentDetailActivity.this, SetNotification.class);
                intent.putExtra(AssessmentViewProvider.ASSESSMENT_CONTENT_TYPE, uri);
                startActivityForResult(intent, ASSESSMENT_REQUEST_CODE);
            }
        });
    }

    private void setLocalDate(Cursor assessmentCursor){
     assessmentTitle = assessmentCursor.getString(assessmentCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TITLE));
     assessmentType = assessmentCursor.getString(assessmentCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TYPE));
     assessmentDate = assessmentCursor.getString(assessmentCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DATE));
     assessmentTime = assessmentCursor.getString(assessmentCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TIME));
     assessmentNote = assessmentCursor.getString(assessmentCursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTE));


     setTitle(assessmentTitle);
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

    }



    @Override
    public void onResume(){
        Intent i = getIntent();
        if(i.getStringExtra("id") != null && i.getStringExtra("id").equals(uri)){
            setTitle("id == "+ uri+ " alarm is armed");
        }
        super.onResume();
        Cursor assessmentCursorUpdate = getContentResolver().query(uri, DBOpenHelper.ALL_ASSESSMENTS_COLUMNS, assessmentFilter,
                null, null);
        assessmentCursorUpdate.moveToFirst();
        setLocalDate(assessmentCursorUpdate);
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
