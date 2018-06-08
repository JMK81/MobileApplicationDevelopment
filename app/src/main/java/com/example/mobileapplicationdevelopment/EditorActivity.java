package com.example.mobileapplicationdevelopment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity {
    private String action;
    private String termFilter;
    private String oldName;
    private String oldStart;
    private String oldEnd;

    private EditText editName;
    private EditText editStart;
    private EditText editEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editName = (EditText) findViewById(R.id.edit_name);
        editStart = (EditText) findViewById(R.id.edit_start);
        editEnd = (EditText) findViewById(R.id.edit_end);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(ObjectViewProvider.TERM_CONTENT_TYPE);

        if(uri == null){
            action = Intent.ACTION_INSERT;
            setTitle("New Term");
        }else{
            action = Intent.ACTION_EDIT;
            //getting the numeric term PK of the record to be edited
            termFilter = DBOpenHelper.TERM_ID + " = " + uri.getLastPathSegment();
            //give acsess to the record in the db
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_TERM_COLUMNS, termFilter,
                    null, null);
            cursor.moveToFirst();
            oldName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_NAME));
            oldStart = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START));
            oldEnd = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END));

            editName.setText(oldName);
            editName.requestFocus();
            editStart.setText(oldStart);
            editEnd.setText(oldEnd);
        }
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    */}
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(action.equals(Intent.ACTION_EDIT)){
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteTerm();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

//fixme dialog box opens but the record is deleted before the aswer can be taken form the user
    private void deleteTerm() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //db management
                            getContentResolver().delete(ObjectViewProvider.CONTENT_URI, termFilter, null);
                            setResult(RESULT_OK);
                            finish();

                            Toast.makeText(EditorActivity.this,
                                    getString(R.string.delete),
                                    Toast.LENGTH_SHORT).show();
                        }else if(button == DialogInterface.BUTTON_NEGATIVE){

                            }

                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();




    }

    private void finishEditing(){
        String newName = editName.getText().toString().trim();
        Log.d("EditorActivity", newName);
        String newStart = editStart.getText().toString().trim();
        String newEnd = editEnd.getText().toString().trim();
        //checking there is a new term if a new term then inset into the db
        switch(action) {
            case Intent.ACTION_INSERT://
                if(newName.length() == 0 && newStart.length() == 0 && newEnd.length() == 0){
                setResult(RESULT_CANCELED);
                    }else{
                    insertTerm(newName);
                    }
                    break;
                //if not new note update the exsiting note
            case Intent.ACTION_EDIT:
                //if all values are null the note will be deleted
                if(newName.length() == 0 && newStart.length() == 0 && newEnd.length() == 0){
                   deleteTerm();
                   
                    //if true cancel return to list
                }else if(newName.equals(oldName) && newStart.equals(oldStart) && newEnd.equals(oldEnd)){
                    setResult(RESULT_CANCELED);
                }else{
                    updateNote(newName);
                }
        }

        finish();
    }

    private void updateNote(String termText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_NAME, termText);

        getContentResolver().update(ObjectViewProvider.CONTENT_URI, values, termFilter, null);
        Toast.makeText(this, "Term Updated", Toast.LENGTH_SHORT);
        //tell the list that something has changed and that the data must be updated
        setResult(RESULT_OK);

    }

    private void insertTerm(String termText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_NAME, termText);

        getContentResolver().insert(ObjectViewProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }
    @Override
    public void onBackPressed(){
        finishEditing();
    }

}
