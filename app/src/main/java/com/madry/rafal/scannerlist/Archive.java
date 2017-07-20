package com.madry.rafal.scannerlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

//each scanned product is automatically added to the second database - archive where
// we can see product names and their barcodes
public class Archive extends AppCompatActivity {
    MyDBHandler mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        mydb = new MyDBHandler(this, null, null, 1);
        ListView lvItems = (ListView) findViewById(R.id.list_view2);

        MyCursorAdapterA myCursorAdapter = new MyCursorAdapterA(this, mydb.getCursorA(), 0);
        lvItems.setAdapter(myCursorAdapter);
    }



}
