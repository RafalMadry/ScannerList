package com.madry.rafal.scannerlist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static com.madry.rafal.scannerlist.MyDBHandler.COLUMN_CODE;
import static com.madry.rafal.scannerlist.MyDBHandler.COLUMN_ITEM;

//adapts the archive table to the listview in the archive  activity
public class MyCursorAdapterA extends CursorAdapter {

    private LayoutInflater cursorInflater;

    public MyCursorAdapterA(Context context, Cursor c, int flags) {
        super(context, c, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.archive_content, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView item = (TextView) view.findViewById(R.id.textView33);
        String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM));
        item.setText(title);
        TextView barcode = (TextView) view.findViewById(R.id.barcode);
        String code = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODE));
        barcode.setText(code);
    }
}