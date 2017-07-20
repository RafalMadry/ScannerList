package com.madry.rafal.scannerlist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    MyDBHandler mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //creating and populating the listview
        mydb = new MyDBHandler(this, null, null, 1);
        ListView lvItems = (ListView) findViewById(R.id.list_view);

        MyCursorAdapter myCursorAdapter = new MyCursorAdapter(this, mydb.getCursor(), 0);
        lvItems.setAdapter(myCursorAdapter);


        //when the button is pressed, the barcode scanner starts
        //once scanned, onActivityResult method handles what happens next
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan the barcode");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();

            }
        });
    }


    //this method handles the results of the scanner
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        final String barcode = scanResult.getContents();
        if (scanResult != null) {
            if(scanResult.getContents() == null) {

                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();

            } else {
                //checks if the barcode is already in the archive
                int rows = mydb.checkRows(barcode);


                if (rows == 0){ //if not
                    //starts the asynctask which is below
                    new FetchName().execute(barcode);

                } else { //if yes
                    //adds it to the main database
                    mydb.addItem(barcode, rows);
                    recreate();
                }


            }


        }

    }
    //creates an alert dialog that allows us to enter the name of a product manually
    public void popUP (String barcode) {

        final String barcode1;
        barcode1 = barcode;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_input, null);
                TextView tv = (TextView) mView.findViewById(R.id.textView2);
                final EditText et = (EditText) mView.findViewById(R.id.item1);
                Button bt1 = (Button) mView.findViewById(R.id.button2);
                Button bt2 = (Button) mView.findViewById(R.id.button3);
                final String code = barcode1;

                bt1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mydb.addNewItem(et.getText().toString(), code);
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);

                    }
                });
                bt2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(i);

                    }
                });


                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, Archive.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    //deletes a record from the database
    public void onOutClick (View v) {

        View parent = (View) v.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.textView22);
        String item = String.valueOf(taskTextView.getText());
        mydb.deleteItem(item);
        recreate();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    //asynctask that downloads a product's name from an open source online database via their API
    //gets it as a JSON object and extracts the name
    public class FetchName extends AsyncTask<String, Void, Void> {

        MyDBHandler mydb = new MyDBHandler(MainActivity.this, null, null, 1);

        public String result = ""; //stores the name of the product if found
        public String barcode = "";

        ProgressDialog progress;



        @Override
        protected void onPreExecute() {

            progress = new ProgressDialog(MainActivity.this);
            progress.show();

        }

        @Override
        protected Void doInBackground (String... params) {

            this.barcode = params[0];


            String query = "https://api.outpan.com/v2/products/" + barcode + "?apikey=60fe5579b429461e437c5be24109729f";
            try {
                URL url = new URL(query);
                URLConnection gimme = url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(gimme.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null) {
                    stringBuffer.append(line);
                }
                JSONObject result1 = new JSONObject(stringBuffer.toString());
                this.result = result1.getString("name");



            } catch (UnsupportedEncodingException e1) {
                Log.e("UnsupportedgException", e1.toString());
                e1.printStackTrace();
            }  catch (IllegalStateException e3) {
                Log.e("IllegalStateException", e3.toString());
                e3.printStackTrace();
            } catch (IOException e4) {
                Log.e("IOException", e4.toString());
                e4.printStackTrace();
            } catch (JSONException e5) {
                Log.e("JSONException", e5.toString());
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void v) {
            if (Objects.equals(result, "null")){ //if there was no result, the popUP method is called that allows us to enter the name manually
                popUP(barcode);
            } else {
                mydb.addNewItem(result, barcode);
                progress.dismiss();
                MainActivity.this.recreate();
            }
        }



    }
}
