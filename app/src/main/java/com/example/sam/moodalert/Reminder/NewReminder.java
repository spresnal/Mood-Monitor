package com.example.sam.moodalert.Reminder;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sam.moodalert.Model.CurrentUser;
import com.example.sam.moodalert.Model.Entry;
import com.example.sam.moodalert.Model.Reminder;
import com.example.sam.moodalert.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewReminder extends Activity {
    private String TAG = NewReminder.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_reminder);
        final Activity activity = this;

        //Intent will be passed if an edit is being made
        Intent intent = this.getIntent();
        final int position = intent.getIntExtra("POSITION", -1);

        //position != -1 when its an object being edited
        if(position != -1){
            //Get Reminder Object
            final CurrentUser user = CurrentUser.getInstance(activity);
            final Reminder r = user.getReminder(position);

            //TimePicker
            TimePicker time = (TimePicker) findViewById(R.id.timePicker);
            time.setCurrentHour(r.getHours());
            time.setCurrentMinute(r.getMinutes());

            Button createButton = (Button) findViewById(R.id.create);
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Retrieve date
                    TimePicker time = (TimePicker) findViewById(R.id.timePicker);
                    int hour = time.getCurrentHour();
                    int minute = time.getCurrentMinute();
                    String timeString = Integer.toString(hour) + "-" + Integer.toString(minute);

                    //remove old reminder
                    user.removeReminder(position);

                    //add new reminder
                    SimpleDateFormat f = new SimpleDateFormat("kk-mm");
                    Date date = new Date();
                    try {
                        date = f.parse(timeString);
                        user.addReminder(new Reminder(date));
                    } catch (ParseException e) {
                        Log.d(TAG, "ERROR: failed to parse reminder time");
                        e.printStackTrace();
                    }

                    Toast.makeText(activity, "Reminder updated!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            Button createButton = (Button) findViewById(R.id.create);
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Retrieve date
                    TimePicker time = (TimePicker) findViewById(R.id.timePicker);
                    int hour = time.getCurrentHour();
                    int minute = time.getCurrentMinute();
                    String timeString = Integer.toString(hour) + "-" + Integer.toString(minute);

                    boolean added = false;

                    //Toast.makeText(activity, timeString, Toast.LENGTH_SHORT).show();
                    SimpleDateFormat f = new SimpleDateFormat("kk-mm");
                    Date date = new Date();
                    try {
                        date = f.parse(timeString);
                        CurrentUser currentUser = CurrentUser.getInstance(activity);
                        int size = currentUser.getReminders().size();
                        currentUser.addReminder(new Reminder(date));
                        if (size != currentUser.getReminders().size())
                            added = true;
                    } catch (ParseException e) {
                        Log.d(TAG, "ERROR: failed to parse reminder time");
                        e.printStackTrace();
                    }

                    if (added)
                        Toast.makeText(activity, "Reminder created!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(activity, "That reminder already exists!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_new_reminder, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
