package com.example.mobileapplicationdevelopment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

public class EditorActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private String action;
    private String oldName;
    private String oldStart;
    private String termFilter;
    private String oldEnd;
    private String termId;

    private EditText editName;
    private EditText editStart;
    private EditText editEnd;
    private EditText dateTarget;

    private static int EDITOR_RETURN = 444;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        editName = findViewById(R.id.edit_name);
        editStart = findViewById(R.id.edit_start);
        editEnd = findViewById(R.id.edit_end);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(ObjectViewProvider.TERM_CONTENT_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle("New Term");
        } else {
            action = Intent.ACTION_EDIT;
            termId = uri.getLastPathSegment();

            //getting the numeric term PK of the record to be edited
            termFilter = DBOpenHelper.TERM_ID + " = " + uri.getLastPathSegment();
            //give acsess to the record in the db
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_TERM_COLUMNS, termFilter,
                    null, null);
            cursor.moveToFirst();
            oldName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_NAME));
            setTitle(oldName);
            oldStart = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START));
            oldEnd = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END));

            editName.setText(oldName);
            editName.requestFocus();
            editStart.setText(oldStart);
            editEnd.setText(oldEnd);

            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
        }
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                finishEditing();
                finish();
                break;
            case R.id.action_add:
                finishEditing();
                finish();
                break;
            case R.id.action_delete:
                deleteTerm();
                break;
        }

        return true;
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
                                Intent intent = new Intent(EditorActivity.this, TermListActivity.class);
                                startActivityForResult(intent, EDITOR_RETURN);
                                finish();

                                Toast.makeText(EditorActivity.this,
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

    private void finishEditing() {
        String newName = editName.getText().toString().trim();
        String newStart = editStart.getText().toString().trim();
        String newEnd = editEnd.getText().toString().trim();
        //checking there is a new term if a new term then inset into the db
        switch (action) {
            case Intent.ACTION_INSERT://
                if (newName.length() == 0 && newStart.length() == 0 && newEnd.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertTerm(newName, newStart, newEnd);
                }
                break;
            //if not new note update the exsiting note
            case Intent.ACTION_EDIT:
                //if all values are null the note will be deleted
                if (newName.length() == 0 && newStart.length() == 0 && newEnd.length() == 0) {
                    deleteTerm();
                    //if true cancel return to list
                } else if (newName.equals(oldName) && newStart.equals(oldStart) && newEnd.equals(oldEnd)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newName, newStart, newEnd);
                }
                break;
        }
    }

    private void updateNote(String termText, String startText, String endText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_NAME, termText);
        values.put(DBOpenHelper.TERM_START, startText);
        values.put(DBOpenHelper.TERM_END, endText);

        getContentResolver().update(ObjectViewProvider.CONTENT_URI, values, termFilter, null);
        Toast.makeText(this, "Term Updated", Toast.LENGTH_SHORT);
        //tell the list that something has changed and that the data must be updated
        setResult(RESULT_OK);

    }

    private void insertTerm(String termText, String startText, String endText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_NAME, termText);
        values.put(DBOpenHelper.TERM_START, startText);
        values.put(DBOpenHelper.TERM_END, endText);

        getContentResolver().insert(ObjectViewProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
        finish();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime());

        dateTarget.setText(currentDateString);
    }

    public void pickStart(View v) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
        dateTarget = findViewById(R.id.edit_start);

    }

    public void pickEnd(View v) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
        dateTarget = findViewById(R.id.edit_end);

    }
}
