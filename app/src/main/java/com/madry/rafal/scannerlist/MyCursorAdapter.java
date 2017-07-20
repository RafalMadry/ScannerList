package com.madry.rafal.scannerlist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static com.madry.rafal.scannerlist.MyDBHandler.COLUMN_ITEM;

//adapts first table of products to the listview in the main activity
public class MyCursorAdapter extends CursorAdapter {

    private LayoutInflater cursorInflater;

    public MyCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.list_content, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView item = (TextView) view.findViewById(R.id.textView22);
        String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM));
        item.setText(title);
    }
}
