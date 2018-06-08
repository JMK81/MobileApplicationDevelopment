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

public class ObjectViewProvider extends ContentProvider {

    private SQLiteOpenHelper dbHelper;

    //term bass path
    private static final String AUTHORITY = "com.example.mobileapplicationdevelopment.objectviewprovider";
    private static final String BASE_PATH = "term";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    //course bass path


    // Constant to identify the requested operation
    private static final int TERM = 1;
    private static final int TERM_ID = 2;


    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    public static final String TERM_CONTENT_TYPE = "TERM";


    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, TERM);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TERM_ID);

    }

    private SQLiteDatabase database;
// todo needs to have metods to pull items form db cources assessments and notes.
    //pass uri return a db object with the querie method

    @Override
    public boolean onCreate() {

        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;

    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //where clause using the uri that is pass to the method
        if (uriMatcher.match(uri) == TERM_ID) {
            //returns the numeric value of the primary key with in the term table
            selection = DBOpenHelper.TERM_ID + " = " + uri.getLastPathSegment();
        }

        return database.query(DBOpenHelper.TABLE_TERM, DBOpenHelper.ALL_TERM_COLUMNS, selection,
                null, null, null, DBOpenHelper.TERM_START + " DESC");

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
    //todo make metods that you can pass a table name as well as ContentValues ??

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = database.insert(DBOpenHelper.TABLE_TERM, null, values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d("ObjectViewProvider", "from inside the delete method");
        return database.delete(DBOpenHelper.TABLE_TERM, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_TERM, values, selection, selectionArgs);
    }
}

