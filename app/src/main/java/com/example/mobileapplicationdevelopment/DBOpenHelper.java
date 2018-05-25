package com.example.mobileapplicationdevelopment;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "term.db";
    private static final int DATABASE_VERSION = 1;

    //CONSTANTS FOR MENTOR
    public static final String TABLE_MENTOR = "mentor";
    public static final String MENTOR_ID = "_id";
    public static final String MENTOR_NAME = "mentorName";
    public static final String MENTOR_EMAIL = "mentorEmail";
    public static final String MENTOR_PHONE = "mentorPhone";

    public static final String[] ALL_MENTOR_COLUMNS = {
            MENTOR_ID, MENTOR_NAME, MENTOR_EMAIL, MENTOR_PHONE
    };

    //SQL to create mentors table
    public static final String MENTOR_TABLE_CREATE =
            "CREATE TABLE " + TABLE_MENTOR + "(" +
                    MENTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MENTOR_NAME + " TEXT, " +
                    MENTOR_EMAIL + " TEXT, " +
                    MENTOR_PHONE + " TEXT" +
                    ")";

    //CONSTANTS FOR NOTES
    public static final String TABLE_NOTES = "notes";
    public static final String NOTE_ID = "_id";
    public static final String NOTE_TITLE = "noteTitle";
    public static final String NOTE_TEXT = "noteText";
    public static final String NOTE_TYPE = "noteType";
    public static final String NOTE_CREATE_DATE = "noteCreateDate";

    public static final String[] ALL_NOTE_COLUMNS = {NOTE_ID, NOTE_TITLE, NOTE_TYPE,
            NOTE_CREATE_DATE};

    //SQL to create table notes
    private static final String NOTE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_NOTES + "(" +
                    NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTE_TITLE + " TEXT, " +
                    NOTE_TEXT + " TEXT, " +
                    NOTE_TYPE + " TEXT, " +
                    NOTE_CREATE_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP" +
                    ")";
    //CONSTANTS FOR ASSESSMENTS
    public static final String TABLE_ASSESSMENTS = "assessments";
    public static final String ASSESSMENT_ID = "_id";
    public static final String ASSESSMENT_TITLE = "assessmentTitle";
    public static final String ASSESSMENT_TYPE = "assessmentType";
    public static final String ASSESSMENT_DATE = "assessmentDate";
    public static final String ASSESSMENT_NOTE = "assessmentNote";

    public static final String[] ALL_ASSESSMENTS_COLUMNS = {
            ASSESSMENT_ID, ASSESSMENT_TITLE, ASSESSMENT_TYPE, ASSESSMENT_DATE, ASSESSMENT_NOTE
    };

    //SQL to create assessments table
    public static final String ASSESMENT_TABLE_CREATE =
            "CREATE TABLE " + TABLE_ASSESSMENTS + "(" +
                    ASSESSMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSESSMENT_TITLE + " TEXT, " +
                    ASSESSMENT_TYPE + " TEXT, " +
                    ASSESSMENT_DATE + " TEXT, " +
                    "FOREIGN KEY(" + ASSESSMENT_NOTE + ") REFERENCES " + TABLE_NOTES + "(" + NOTE_ID + ")" +
                    ")";


    //Constants for identifying terms table and columns
    public static final String TABLE_TERM = "term";
    public static final String TERM_ID = "_id";
    public static final String TERM_NANE = "termText";
    public static final String TERM_START = "termStart";
    public static final String TERM_END = "termEnd";
    public static final String TERM_COURSES = "termCources";
    public static final String TERM_NOTES = "termNotes";

    public static final String[] ALL_TERM_COLUMNS =
            {TERM_ID, TERM_NANE, TERM_START, TERM_END};

    //SQL to create table terms
    //TODO make required columns not null
    private static final String TERM_TABLE_CREATE =
            "CREATE TABLE " + TABLE_TERM + " (" +
                    TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TERM_NANE + " TEXT, " +
                    TERM_COURSES + " TEXT," +
                    TERM_NOTES + " TEXT," +
                    TERM_START + " TEXT, " +
                    TERM_END + " TEXT" +
                    ")";

    //Constants for courses

    public static final String TABLE_COURSE = "course";
    public static final String COURSE_ID = "_id";
    public static final String COURSE_TEXT = "courseText";
    public static final String COURSE__START = "courseStart";
    public static final String COURSE_END = "courseEnd";
    public static final String COURSE_TERM = "courseTerm";
    public static final String COURSE_ASSESSMENTS = "courseAssessments";
    public static final String COURSE_MENTOR = "courseMentor";
    public static final String COURSE_NOTES = "courseNotes";

    public static final String[] ALL_COURSE_COLLUMS = {COURSE_ID, COURSE_TEXT, COURSE__START,
            COURSE_END, COURSE_MENTOR, COURSE_ASSESSMENTS, COURSE_NOTES};

    //SQL to create table courses ** FOREIGN KEY(customer_id) REFERENCES customers(id),
    private static final String COURSE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_COURSE + "(" +
                    COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COURSE_TEXT + " TEXT," +
                    COURSE__START + " TEXT, " +
                    COURSE_END + " TEXT," +
                    COURSE_TERM + " INTEGER," +
                    COURSE_ASSESSMENTS + " INTEGER, " +
                    COURSE_MENTOR + " INTEGER, " +
                    COURSE_NOTES + " INTEGER, " +
                    "FOREIGN KEY (" + COURSE_TERM + ") REFERENCES " + TABLE_TERM + "(" + TERM_ID + ")," +
                    "FOREIGN KEY (" + COURSE_ASSESSMENTS + ") REFERENCES " + TABLE_ASSESSMENTS + "(" + ASSESSMENT_ID + ")," +
                    "FOREIGN KEY (" + COURSE_MENTOR + ") REFERENCES " + TABLE_MENTOR + "(" + MENTOR_ID + ")," +
                    "FOREIGN KEY (" + COURSE_NOTES + ") REFERENCES " + TABLE_NOTES + "(" + NOTE_ID + ")" +
                    ")";


    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //todo remove debug message
        Log.d("DBOpenHelper", "onCreate ");
        try {
            db.execSQL(TERM_TABLE_CREATE);
            db.execSQL(NOTE_TABLE_CREATE);
            db.execSQL(ASSESMENT_TABLE_CREATE);
            db.execSQL(MENTOR_TABLE_CREATE);
            db.execSQL(COURSE_TABLE_CREATE);
        } catch (RuntimeException e) {
            Log.d("DBOpenHelper", "instanciate db");
            Log.d("DBOpenHelper", e.toString());
        }
        //todo remove debug message
        Log.d("DBOpenHelper", "onCreate after database tables were created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERM);
        onCreate(db);
    }
}

