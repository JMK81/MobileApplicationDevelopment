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

import com.example.mobileapplicationdevelopment.DBOpenHelper;


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

        //were item that it is selected form is the term parent
       /* if (uriMatcher.match(uri) == COURSE_ID) {
            selection = DBOpenHelper.COURSE_TERM + "=" + uri.getLastPathSegment();//this uri is course
            Log.d("CVP", uri.getLastPathSegment());
        }
*/
        return database.query(DBOpenHelper.TABLE_COURSE, DBOpenHelper.ALL_COURSE_COLLUMS, selection,
                null, null, null, DBOpenHelper.COURSE_TEXT + " DESC");
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
