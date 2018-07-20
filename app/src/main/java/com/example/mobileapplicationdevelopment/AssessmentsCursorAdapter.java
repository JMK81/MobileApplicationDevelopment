package com.example.mobileapplicationdevelopment;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class AssessmentsCursorAdapter extends CursorAdapter {


    public AssessmentsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.assessment_list_content, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String assessmentTitle  = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TITLE));
        String assessmentDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DATE));
        String assessmentTime = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TIME));
        String assessmentType = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TYPE));
        String assessmentNote = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NOTE));


        //a string longer then 10 will get ... added to the end
        int posTitle = assessmentTitle.indexOf(20);
        if (posTitle != -1) {
            assessmentTitle = assessmentTitle.substring(0, posTitle) + "...";
        }
        int posNote = assessmentNote.indexOf(30);
        if(posNote != -1){
            assessmentNote = assessmentNote.substring(0, posNote )+ " ...";
        }



            TextView title = view.findViewById(R.id.assessment_content_title);
            title.setText(assessmentTitle);
                    //assessmentTitle);
            TextView date = view.findViewById(R.id.assessment_content_item_date);
            date.setText(assessmentDate);
            TextView time = view.findViewById(R.id.assessment_content_item_time);
            time.setText(assessmentTime);
            TextView type = view.findViewById(R.id.assessment_content_item_type);
            type.setText(assessmentType);
            TextView note = view.findViewById(R.id.assessment_content_item_note);
            note.setText(assessmentNote);



    }


}
