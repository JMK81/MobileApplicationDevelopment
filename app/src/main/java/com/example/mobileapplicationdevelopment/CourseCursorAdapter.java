package com.example.mobileapplicationdevelopment;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CourseCursorAdapter extends CursorAdapter {

    public CourseCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.term_list_content, parent, false);
    }

    @Override                                       //cursor in the db term object
    public void bindView(View view, Context context, Cursor cursor) {
        //needs to target the course table
        String courseId   = cursor.getColumnName(cursor.getColumnIndex(DBOpenHelper.COURSE_ID));
        String courseStart = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE__START));
        String courseText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_TEXT));
        String courseEnd = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END));
        Log.d("@A1", "after line 29 strings have been inithlized");

        //a string longer then 10 will get ... added to the end
        int pos = courseText.indexOf(20);
        if (pos != -1) {
            courseText = courseText.substring(0, pos) + "...";
        }
        try {
            //fixme need to add to xml files
            TextView tv = (TextView) view.findViewById(R.id.tvTerm);
            tv.setText(courseText);
            Log.d("TermCursorAdapter:", "termName TextView");
            TextView sd = (TextView) view.findViewById(R.id.term_start);
            sd.setText(courseStart);
            TextView ed = (TextView) view.findViewById(R.id.term_end);
            ed.setText(courseEnd);
        } catch (Exception e) {
            Log.d("TermCusronAdapter", e.toString() + "Binding view");
        }

    }

    }

