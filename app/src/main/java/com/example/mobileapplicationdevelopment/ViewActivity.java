package com.example.mobileapplicationdevelopment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class ViewActivity extends AppCompatActivity {
    /*
    *intent item pass to viewActivity
    * list view
    * */
    private static final int COURSE_REQUEST_CODE = 2002;
    private static final int ASSESSMENTS_REQUEST_CODE = 3003;
    private static final int MENTOR_REQUEST_CODE = 4004;

//will this return to prevous item.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //set toolbar lable to parent title

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

  /*  public void openEditorForNewNote(View view) {
        Intent intent = new Intent(this, ViewActivity.class);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);


    }*/
}
