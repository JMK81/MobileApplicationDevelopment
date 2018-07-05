package com.example.mobileapplicationdevelopment;


import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    private final Context context;

    //Constants for db name and version
    private static final String DATABASE_NAME = "term.db";
    private static final int DATABASE_VERSION = 1;

    //Constants for identifying terms table and columns
    public static final String TABLE_TERM = "term";
    public static final String TERM_ID = "_id";
    public static final String TERM_NAME = "termText";
    public static final String TERM_START = "termStart";
    public static final String TERM_END = "termEnd";


    public static final String[] ALL_TERM_COLUMNS =
            {TERM_ID, TERM_NAME, TERM_START, TERM_END};

    //SQL to create table terms
    //TODO make required columns not null
    private static final String TERM_TABLE_CREATE =
            "CREATE TABLE " + TABLE_TERM + " (" +
                    TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TERM_NAME + " TEXT, " +
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
    public static final String MENTOR_EMAIL = "mentorEmail";
    public static final String MENTOR_PHONE = "mentorPhone";


    public static final String[] ALL_COURSE_COLUMNS = {COURSE_ID, COURSE_TEXT, COURSE__START,
            COURSE_END, COURSE_TERM, COURSE_ASSESSMENTS, COURSE_MENTOR, MENTOR_EMAIL, MENTOR_PHONE};

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
                    MENTOR_EMAIL + " INTEGER, " +
                    MENTOR_PHONE + " INTEGER, " +
                    "FOREIGN KEY (" + COURSE_TERM + ") REFERENCES " + TABLE_TERM + "(" + TERM_ID + ")" +
                    ")";

    //CONSTANTS FOR ASSESSMENTS
    public static final String TABLE_ASSESSMENTS = "assessments";
    public static final String ASSESSMENT_ID = "_id";
    public static final String ASSESSMENT_TITLE = "assessmentTitle";
    public static final String ASSESSMENT_TYPE = "assessmentType";
    public static final String ASSESSMENT_DATE = "assessmentDate";
    public static final String ASSESSMENT_TIME = "assessmentTime";
    public static final String ASSESSMENT_NOTE = "assessmentNote";
    public static final String ASSESSMENT_COURSE = "assessmentCourse";

    public static final String[] ALL_ASSESSMENTS_COLUMNS = {
            ASSESSMENT_ID, ASSESSMENT_TITLE, ASSESSMENT_TYPE, ASSESSMENT_DATE, ASSESSMENT_TIME,
            ASSESSMENT_COURSE, ASSESSMENT_NOTE
    };

    //SQL to create assessments table
    public static final String ASSESSMENT_TABLE_CREATE =
            "CREATE TABLE " + TABLE_ASSESSMENTS + "(" +
                    ASSESSMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSESSMENT_TITLE + " TEXT, " +
                    ASSESSMENT_TYPE + " TEXT, " +
                    ASSESSMENT_DATE + " TEXT, " +
                    ASSESSMENT_TIME + " TEXT, " +
                    ASSESSMENT_COURSE + " TEXT, " +
                    ASSESSMENT_NOTE + " TEXT, " +
                    "FOREIGN KEY(" + ASSESSMENT_COURSE + ") REFERENCES " + TABLE_COURSE + "(" + COURSE_ID + ")" +
                    ")";

    public DBOpenHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //todo remove debug message
        Resources res = context.getResources();
        db.execSQL(TERM_TABLE_CREATE);
        db.execSQL(COURSE_TABLE_CREATE);
        db.execSQL(ASSESSMENT_TABLE_CREATE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERM);
        onCreate(db);
    }
}

