package com.example.mobileapplicationdevelopment;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class AssessmentsCursorAdapter extends CursorAdapter {


    public AssessmentsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.term_list_content, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String assessmentTitle  = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TITLE));
        String assessmentDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DATE));
        String assessmentTime = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TIME));
        //String assessmentNote = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END));


        //a string longer then 10 will get ... added to the end
        int pos = assessmentTitle.indexOf(20);
        if (pos != -1) {
            assessmentTitle = assessmentTitle.substring(0, pos) + "...";
        }


            //fixme need to add to xml files
            TextView tv = (TextView) view.findViewById(R.id.tvTerm);
            tv.setText(assessmentTitle);
            TextView sd = (TextView) view.findViewById(R.id.term_start);
            sd.setText(assessmentDate);
            TextView ed = (TextView) view.findViewById(R.id.term_end);
            ed.setText(assessmentTime);



    }


}
