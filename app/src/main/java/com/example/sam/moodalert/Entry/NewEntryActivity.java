package com.example.sam.moodalert.Entry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.sam.moodalert.MainActivity;
import com.example.sam.moodalert.Model.CurrentUser;
import com.example.sam.moodalert.Model.Entry;
import com.example.sam.moodalert.R;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NewEntryActivity extends ActionBarActivity {
    private static final String TAG = NewEntryActivity.class.getSimpleName();

    private int value = 0;
    private Activity activity;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //floating activity
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_new_entry);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Date date = new Date();
        activity = this;

        //Get date textview
        TextView textViewDate = (TextView) findViewById(R.id.date);

        //Setting date
        String formattedDate = (String) android.text.format.DateFormat.format("MM/dd/yyyy h:mm a", date);
        textViewDate.setText(formattedDate);

        //Number picker
        final NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
        final String[] values = {"+3","+2","+1","0","-1","-2","-3"};
        np.setMinValue(0);
        np.setMaxValue(values.length-1);
        np.setDisplayedValues(values);
        np.setWrapSelectorWheel(false);
        np.setValue(3);

        //Intent will be passed if an edit is being made
        Intent intent = this.getIntent();
        position = intent.getIntExtra("position", -1);

        Log.d(TAG, "Position received: " + position);
        //Check to see if entry is being edited
        if(position != -1) {
            setTitle("Edit Entry");
            CurrentUser user = CurrentUser.getInstance(this);
            ArrayList<Entry> entries = user.getEntries();
            final Entry entry = entries.get(position);

            TextView message = (TextView) findViewById(R.id.entry_message);
            //Set message
            message.setText(entry.getMessage());
            //Set value
            int place = 0;
            for(int i = 0; i < values.length; i++){
                if(stringToInteger(values[i]) == entry.getValue()) {
                    place = i;
                    break;
                }
            }
            np.setValue(place);
            //Set date
            formattedDate = (String) android.text.format.DateFormat.format("MM/dd/yyyy h:mm a", entry.getDateTime());
            textViewDate.setText(formattedDate);

            //Listen for accept button
            Button sendButton = (Button) findViewById(R.id.submit_entry);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView request = (TextView) findViewById(R.id.entry_message);
                    //Message to be sent
                    final String entryMessage = request.getText().toString();
                    //Value from array
                    value = stringToInteger(values[np.getValue()]);

                    //TODO: Patch to put in date, but it is different than what is stored
                    //set new message
                    entry.setMessage(entryMessage);
                    //set new value
                    entry.setValue(value);

                    //Send everything to parse
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Entry");
                    query.whereEqualTo("objectId", entry.getObjectId());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> entries, com.parse.ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "Retrieved " + entries.size() + " scores");
                                final ParseObject pEntry = entries.get(0);
                                pEntry.put("value", value);
                                pEntry.put("message", entryMessage);
                                pEntry.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(com.parse.ParseException e) {
                                        if (e == null) {
                                            Log.d(TAG, "Save succesful");
                                        } else {
                                            Log.d(TAG, "Error: " + e.getMessage());
                                            pEntry.saveEventually();
                                        }
                                    }
                                });
                            } else {
                                Log.d(TAG, "Error: " + e.getMessage());
                            }
                        }
                    });

                    //Make sure entry fragment knows update occurred
//                    Intent intent = new Intent("ENTRY_UPDATE");
//                    sendBroadcast(intent);

                    //go to main activity
//                    Intent intent = new Intent(activity, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);

                    finish();
                }
            });
        } else { //new item
            //Listen for accept button
            Button sendButton = (Button) findViewById(R.id.submit_entry);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView request = (TextView) findViewById(R.id.entry_message);
                    //Message to be sent
                    String entryMessage = request.getText().toString();
                    //Value from array
                    value = stringToInteger(values[np.getValue()]);

                    //put in model and save
                    Entry entry = new Entry(value, entryMessage);
                    CurrentUser.getInstance(activity).addEntry(entry);

                    //go to main activity
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }
    }

    //Changes value in selectable (ie +2 to 2)
    public int stringToInteger(String number){
        switch(number) {
            case "+3":
                return 3;
            case "+2":
                return 2;
            case "+1":
                return 1;
            case "0":
                return 0;
            case "-1":
                return -1;
            case "-2":
                return -2;
            case "-3":
                return -3;
            default:
                return 0;
        }
    }

    public boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.menu_new_entry, menu);
//        menu.clear();
//        return true;
//    }
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            Intent intent = new Intent(this, EntryActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra("position", position);
//            startActivity(intent);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
