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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CourseDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri uri;
    private AssessmentsCursorAdapter assessmentAdapter;
    private TextView title;
    private TextView start;
    private TextView end;
    private TextView mentor;
    private TextView mentorPhone;
    private TextView mentorEmail;
    private TextView courseStatus;
    private TextView courseNote;

    ImageButton assessmentAddBtn;
    ImageButton contactMentorEmail;
    ImageButton contactMentorPhone;
    ImageButton noteDetailBtn;

    private String courseTitle;
    private String courseStart;
    private String courseEnd;
    private String courseMentor;
    private String courseMentorPhone;
    private String courseMentorEmail;
    private String courseId;
    private String term;
    private String assessmentFilter;
    private String courseFilter;
    private String status;
    private String note;
    private String shortNote;

    private ListView list;



    private static final int ASSESSMENT_REQUEST_CODE = 4004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);

        title = findViewById(R.id.course_item_name);
        start = findViewById(R.id.course_item_start);
        end = findViewById(R.id.course_item_end);
        mentor = findViewById(R.id.course_item_mentor);
        mentorEmail = findViewById(R.id.course_item_email);
        mentorPhone = findViewById(R.id.course_item_phone);
        assessmentAddBtn = findViewById(R.id.assessment_add_btn);
        contactMentorEmail = findViewById(R.id.course_email_btn);
        contactMentorPhone = findViewById(R.id.course_phone_btn);
        noteDetailBtn = findViewById(R.id.note_add_btn);
        courseStatus = findViewById(R.id.course_item_status);
        courseNote = findViewById(R.id.course_item_note);

        if (savedInstanceState == null) {
            //assigning string from the saveInstanceState Bund
            Intent intent = getIntent();
            uri = intent.getParcelableExtra(CourseViewProvider.COURSE_CONTENT_TYPE);

        } else {//only when retruning form the add/edit
            uri = savedInstanceState.getParcelable(CourseViewProvider.COURSE_CONTENT_TYPE);

        }
        courseId = uri.getLastPathSegment();
        courseFilter = DBOpenHelper.COURSE_ID + " = " + courseId;
        assessmentFilter = DBOpenHelper.ASSESSMENT_COURSE + " = " + courseId;
        list = findViewById(R.id.course_detail_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CourseDetailActivity.this, AssessmentDetailActivity.class);
                Uri uri = Uri.parse(AssessmentViewProvider.ASSESSMENT_URI + "/" + id);
                intent.putExtra(AssessmentViewProvider.ASSESSMENT_CONTENT_TYPE, uri);
                startActivityForResult(intent, ASSESSMENT_REQUEST_CODE);

            }
        });

        Cursor course = getContentResolver().query(uri, DBOpenHelper.ALL_COURSE_COLUMNS, courseFilter,
                null, null);
        course.moveToFirst();
        setLocalData(course);

        Cursor assessmentCursor = getContentResolver().query(AssessmentViewProvider.ASSESSMENT_URI, DBOpenHelper.ALL_ASSESSMENTS_COLUMNS,
                assessmentFilter, null, null);


        if (assessmentCursor != null && assessmentCursor.getCount() > 0) {
            assessmentCursor.moveToFirst();
            //cursor addapter needs to referance the assessments table
            assessmentAdapter = new AssessmentsCursorAdapter(this, assessmentCursor, 0);
            list.setAdapter(assessmentAdapter);


            getLoaderManager().initLoader(0, null, this);
        }
        assessmentAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseDetailActivity.this, AssessmentAddActivity.class);
                intent.putExtra(CourseViewProvider.COURSE_CONTENT_TYPE, uri);
                intent.putExtra("course", courseId);
                startActivityForResult(intent, ASSESSMENT_REQUEST_CODE);
            }
        });
        contactMentorPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + contactMentorPhone));
                    startActivity(intent);
                } catch (Exception e) {
                    Snackbar.make(v, "Sorry there is no supported phone client", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        contactMentorEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int button) {
                                if (button == DialogInterface.BUTTON_POSITIVE) {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setData(Uri.parse("mailto: "));
                                        String[] emailString = {courseMentorEmail};
                                        intent.putExtra(Intent.EXTRA_EMAIL, emailString);
                                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.questions_about) + courseTitle);
                                        intent.putExtra(Intent.EXTRA_TEXT, "Hello " + courseMentor+ "\n" + note);
                                        intent.setType("message/rfc822");
                                        startActivity(Intent.createChooser(intent, "Pick Email App"));
                                    } catch (Exception e) {
                                        Toast.makeText(CourseDetailActivity.this,
                                                "Sorry there is no supported Email client", Snackbar.LENGTH_LONG);

                                    }

                                } else if (button == DialogInterface.BUTTON_NEGATIVE) {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setData(Uri.parse("mailto: "));
                                        String[] emailString = {courseMentorEmail};
                                        intent.putExtra(Intent.EXTRA_EMAIL, emailString);
                                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.questions_about) + courseTitle);
                                        intent.putExtra(Intent.EXTRA_TEXT, "Hello " + courseMentor);
                                        intent.setType("message/rfc822");
                                        startActivity(Intent.createChooser(intent, "Pick Email App"));
                                    } catch (Exception e) {
                                        Toast.makeText(CourseDetailActivity.this,
                                                "Sorry there is no supported Email client", Snackbar.LENGTH_LONG);

                                    }
                                }
                            }
                        };

                AlertDialog.Builder builder = new AlertDialog.Builder(CourseDetailActivity.this);
                builder.setMessage(getString(R.string.note_to_email))
                        .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                        .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                        .show();

            }
        });
        noteDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseDetailActivity.this, Note.class);
                intent.putExtra("note", note);
                intent.putExtra("courseTitle", "Note: " + courseTitle);
                intent.putExtra("courseId", courseId);
                startActivityForResult(intent, ASSESSMENT_REQUEST_CODE);

            }

        });

        getLoaderManager().initLoader(0, null, this);

    }

    private void setLocalData(Cursor course) {

        courseTitle = course.getString(course.getColumnIndex(DBOpenHelper.COURSE_TEXT));
        courseStart = course.getString(course.getColumnIndex(DBOpenHelper.COURSE__START));
        courseEnd = course.getString(course.getColumnIndex(DBOpenHelper.COURSE_END));
        courseMentor = course.getString(course.getColumnIndex(DBOpenHelper.COURSE_MENTOR));
        courseMentorPhone = course.getString(course.getColumnIndex(DBOpenHelper.MENTOR_PHONE));
        courseMentorEmail = course.getString(course.getColumnIndex(DBOpenHelper.MENTOR_EMAIL));
        term = course.getString(course.getColumnIndex(DBOpenHelper.COURSE_TERM));
        status = course.getString(course.getColumnIndex(DBOpenHelper.COURSE_STATUS));
        note = course.getString(course.getColumnIndex(DBOpenHelper.COURSE_NOTE));

        if (note.length() > 25) {

            shortNote = note.substring(0, 25) + " ...";

        }else{
            shortNote = note;
        }

        title.setText(courseTitle);
        start.setText(courseStart);
        end.setText(courseEnd);
        mentor.setText(courseMentor);
        mentorPhone.setText(courseMentorPhone);
        mentorEmail.setText(courseMentorEmail);
        courseStatus.setText(status);
        courseNote.setText(shortNote);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_edit:
                actionEdit();
                break;
        }
        return true;
    }

    public void actionEdit() {
        Intent intent = new Intent(CourseDetailActivity.this, CourseAddActivity.class);
        intent.putExtra(CourseViewProvider.COURSE_CONTENT_TYPE, uri);
        startActivityForResult(intent, ASSESSMENT_REQUEST_CODE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, AssessmentViewProvider.ASSESSMENT_URI,
                null, assessmentFilter, null, null);
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (assessmentAdapter != null && assessmentAdapter.getCount() > 0) {
            assessmentAdapter.swapCursor(data);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resuldCode, Intent data) {
        if (requestCode == ASSESSMENT_REQUEST_CODE && requestCode == RESULT_OK) {
            restartLoader();

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (assessmentAdapter != null && assessmentAdapter.getCount() > 0) {
            assessmentAdapter.swapCursor(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelable(CourseViewProvider.COURSE_CONTENT_TYPE, uri);
        super.onSaveInstanceState(bundle);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceStart) {
        super.onRestoreInstanceState(savedInstanceStart);

    }

    @Override
    public void onResume() {

        Cursor course = getContentResolver().query(uri, DBOpenHelper.ALL_COURSE_COLUMNS, courseFilter,
                null, null);
        if (course != null && course.getCount() > 0) {

            course.moveToFirst();
            setLocalData(course);
        } else {
            onBackPressed();
        }
        Cursor assessmentCursorUpdate = getContentResolver().query(AssessmentViewProvider.ASSESSMENT_URI, DBOpenHelper.ALL_ASSESSMENTS_COLUMNS,
                assessmentFilter, null, null);
        assessmentCursorUpdate.moveToFirst();
        DatabaseUtils.dumpCursor(assessmentCursorUpdate);

        if (assessmentCursorUpdate != null && assessmentCursorUpdate.getCount() > 0) {
            assessmentCursorUpdate.moveToFirst();
            if (assessmentAdapter == null) {
                assessmentAdapter = new AssessmentsCursorAdapter(this, assessmentCursorUpdate, 0);
                list.setAdapter(assessmentAdapter);
            } else {

                assessmentAdapter.swapCursor(assessmentCursorUpdate);

            }
        } else {
            list.setAdapter(null);
        }
        super.onResume();
    }


}
