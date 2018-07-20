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

public class AssessmentViewProvider extends ContentProvider {

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;


    private static final int ASSESSMENT = 5;
    private static final int ASSESSMENT_ID = 6;

    private static final String ASSESSMENT_AUTHORITY = "com.example.mobileapplicationdevelopment.assessmentviewprovider";
    private static final String ASSESSMENT_BASE_PATH = "assessment";
    public static final Uri ASSESSMENT_URI = Uri.parse("content://" + ASSESSMENT_AUTHORITY + "/" + ASSESSMENT_BASE_PATH);

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ASSESSMENT_AUTHORITY, ASSESSMENT_BASE_PATH, ASSESSMENT);
        uriMatcher.addURI(ASSESSMENT_AUTHORITY, ASSESSMENT_BASE_PATH + "/#", ASSESSMENT_ID);
    }

    public static final String ASSESSMENT_CONTENT_TYPE = "ASSESSMENT";

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        //were item that it is selected form is the term parent
        if (uriMatcher.match(uri) == ASSESSMENT_ID) {
            selection = DBOpenHelper.ASSESSMENT_ID + "=" + uri.getLastPathSegment();//this uri is course
            return cursor = database.query(DBOpenHelper.TABLE_ASSESSMENTS, DBOpenHelper.ALL_ASSESSMENTS_COLUMNS, selection,
                    null, null, null, DBOpenHelper.ASSESSMENT_DATE + " DESC");
        } else if(uriMatcher.match(uri) == ASSESSMENT) {
            return cursor = database.query(DBOpenHelper.TABLE_ASSESSMENTS, DBOpenHelper.ALL_ASSESSMENTS_COLUMNS, selection,
                    null, null, null, DBOpenHelper.ASSESSMENT_DATE + " DESC");
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
        long id = database.insert(DBOpenHelper.TABLE_ASSESSMENTS
                , null, values);
        return Uri.parse(ASSESSMENT_BASE_PATH + "/" + id);
    }

    @Nullable
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d("ObjectViewProvider", "from inside the delete method");
        return database.delete(DBOpenHelper.TABLE_ASSESSMENTS, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_ASSESSMENTS, values, selection, selectionArgs);
    }
}
