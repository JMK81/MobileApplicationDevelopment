package com.example.mobileapplicationdevelopment;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.function.DoubleBinaryOperator;

public class CourseCursorAdapter extends CursorAdapter {

    public CourseCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    String courseId;
    String courseStart;
    String courseEnd;
    String courseText;
    //String courseMentor;


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.term_list_content, parent, false);
    }

    @Override                                       //cursor in the db term object
    public void bindView(View view, Context context, Cursor cursor) {
        //needs to target the course table
        if(cursor.isNull(cursor.getColumnIndex(DBOpenHelper.COURSE_TERM)) ||
                cursor.isNull(cursor.getColumnIndex(DBOpenHelper.COURSE__START)) ||
                cursor.isNull(cursor.getColumnIndex(DBOpenHelper.COURSE_TEXT)) ||
                cursor.isNull(cursor.getColumnIndex(DBOpenHelper.COURSE_END))){
             courseText = "There in no course assigned to this term";

        }else {
            courseId = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_ID));
            courseStart = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE__START));
            courseText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_TEXT));
            courseEnd = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END));
            //courseMentor = cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_NAME));
        }

        //a string longer then 10 will get ... added to the end
        int pos = courseText.indexOf(20);
        if (pos != -1) {
            courseText = courseText.substring(0, pos) + "...";
        }


        //fixme need to add to xml files
        TextView tv = (TextView) view.findViewById(R.id.tvTerm);
        tv.setText(courseText);
        TextView sd = (TextView) view.findViewById(R.id.term_start);
        sd.setText(courseStart);
        TextView ed = (TextView) view.findViewById(R.id.term_end);
        ed.setText(courseEnd);
        /*TextView cm = (TextView) view.findViewById(R.id.course_mentor);
        cm.setText(courseMentor);*/


    }

}

