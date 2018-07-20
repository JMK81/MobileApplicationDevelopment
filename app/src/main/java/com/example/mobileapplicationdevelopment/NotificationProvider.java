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

public class NotificationProvider extends ContentProvider {
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;


    private static final int NOTIFICATION = 7;
    private static final int NOTIFICATION_ID = 8;

    private static final String NOTIFICATION_AUTHORITY = "com.example.mobileapplicationdevelopment.notificationprovider";
    private static final String NOTIFICATION_BASE_PATH = "notification";
    public static final Uri NOTIFICATION_URI = Uri.parse("content://" + NOTIFICATION_AUTHORITY + "/" + NOTIFICATION_BASE_PATH);

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(NOTIFICATION_AUTHORITY, NOTIFICATION_BASE_PATH, NOTIFICATION);
        uriMatcher.addURI(NOTIFICATION_AUTHORITY, NOTIFICATION_BASE_PATH + "/#", NOTIFICATION_ID);
    }

    public static final String NOTIFICATION_CONTENT_TYPE = "NOTIFICATION";

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
        if (uriMatcher.match(uri) == NOTIFICATION_ID) {
            selection = DBOpenHelper.ASSESSMENT_ID + " = " + uri.getLastPathSegment();//this uri is course
            return cursor = database.query(DBOpenHelper.TABLE_NOTIFICATION, DBOpenHelper.ALL_NOTIFICATION_COLUMNS, selection,
                    null, null, null, DBOpenHelper.ASSESSMENT_DATE + " DESC");
        } else if(uriMatcher.match(uri) == NOTIFICATION) {
            return cursor = database.query(DBOpenHelper.TABLE_NOTIFICATION, DBOpenHelper.ALL_NOTIFICATION_COLUMNS, selection,
                    null, null, null, DBOpenHelper.NOTIFICATION_MILLS + " DESC");
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
        long id = database.insert(DBOpenHelper.TABLE_NOTIFICATION
                , null, values);
        return Uri.parse(NOTIFICATION_BASE_PATH + "/" + id);
    }

    @Nullable
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d("ObjectViewProvider", "from inside the delete method");
        return database.delete(DBOpenHelper.TABLE_NOTIFICATION, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_NOTIFICATION, values, selection, selectionArgs);
    }
}
