package com.example.mobileapplicationdevelopment;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class TermCursorAdapter extends CursorAdapter {
    public TermCursorAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);
    }
    //todo add current term modification
    //todo add more fields to the term_list_item


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.term_list_content, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //todo need to add multible feilds check the context switch between tables

        String termId   = cursor.getColumnName(cursor.getColumnIndex(DBOpenHelper.TERM_ID));
        String termName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_NAME));
        String termStart = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START));
        String termEnd = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END));
        //String termNote = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_NOTES));

        //a string longer then 10 will get ... added to the end
        int pos = termName.indexOf(10);
        if (pos != -1) {
            termName = termName.substring(0, pos) + "...";
        }
        try {
            //fixme need to add to xml files
            TextView tv = (TextView) view.findViewById(R.id.tvTerm);
            tv.setText(termName);
            Log.d("TermCursorAdapter:", "termName TextView");
            TextView sd = (TextView) view.findViewById(R.id.term_start);
            sd.setText(termStart);
            TextView ed = (TextView) view.findViewById(R.id.term_end);
            ed.setText(termEnd);
        } catch (Exception e) {
            Log.d("TermCusronAdapter", e.toString() + "Binding view");
        }

    }
}

