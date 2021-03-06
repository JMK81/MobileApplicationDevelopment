package com.example.mobileapplicationdevelopment;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


public class CourseViewProvider extends ContentProvider {

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;

    private static final int COURSE = 3;
    private static final int COURSE_ID = 4;

    private static final String COURSE_AUTHORITY = "com.example.mobileapplicationdevelopment.courseviewprovider";
    private static final String COURSE_BASE_PATH = "course";
    public static final Uri COURSE_URI = Uri.parse("content://" + COURSE_AUTHORITY + "/" + COURSE_BASE_PATH);

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);
    String TAG = "CourseViewProvider";

    static {
        uriMatcher.addURI(COURSE_AUTHORITY, COURSE_BASE_PATH, COURSE);
        uriMatcher.addURI(COURSE_AUTHORITY, COURSE_BASE_PATH + "/#", COURSE_ID);
    }

    public static final String COURSE_CONTENT_TYPE = "COURSE";

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String basePath = uri.getPath();
        //select a single course
        if (uriMatcher.match(uri) == COURSE_ID) {
Log.d(TAG, "uri: " + uri.getPath());
            selection = DBOpenHelper.COURSE_ID + "=" + uri.getLastPathSegment();//this uri is course

            return database.query(DBOpenHelper.TABLE_COURSE, DBOpenHelper.ALL_COURSE_COLUMNS, selection,
                    null, null, null, DBOpenHelper.COURSE__START + " DESC");

        } else if (uriMatcher.match(uri) == COURSE) {
            //select courses term_id match a course_term
            Log.d(TAG, "selection is :" + selection);
            return database.query(DBOpenHelper.TABLE_COURSE, DBOpenHelper.ALL_COURSE_COLUMNS, selection,
                    null, null, null, DBOpenHelper.COURSE__START + " DESC");
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = database.insert(DBOpenHelper.TABLE_COURSE, null, values);
        return Uri.parse(COURSE_BASE_PATH + "/" + id);
    }

    @Nullable
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d("ObjectViewProvider", "from inside the delete method");
        return database.delete(DBOpenHelper.TABLE_COURSE, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_COURSE, values, selection, selectionArgs);
    }

}
