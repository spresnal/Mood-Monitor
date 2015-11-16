package com.example.sam.moodalert.Entry;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sam.moodalert.MainActivity;
import com.example.sam.moodalert.Model.CurrentUser;
import com.example.sam.moodalert.Model.Entry;
import com.example.sam.moodalert.R;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/*
Actual entry view has back/edit/delete as options in menu
 */

public class EntryActivity extends ActionBarActivity {
    private static final String TAG = EntryActivity.class.getSimpleName();
    private int position;
    private int following;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        position = intent.getIntExtra("position", -1);
        following = intent.getIntExtra("followingPosition", -1);

        Log.d(TAG, "Position received: " + position);
    }

    @Override
    public void onResume(){
        super.onResume();

        CurrentUser user = CurrentUser.getInstance(this);

        //If called from following activity then load following entries
        Entry entry;
        if(following != -1) {
            ArrayList<Entry> entries = user.getFriends(following).getEntries();
            entry = entries.get(position);
        } else {
            ArrayList<Entry> entries = user.getEntries();
            entry = entries.get(position);
        }

        TextView textView = (TextView) findViewById(R.id.message);
        TextView textViewDate = (TextView) findViewById(R.id.date);
        ImageView imageView = (ImageView) findViewById(R.id.icon);

        //get message
        String message = entry.getMessage();
        //Set text to message
        textView.setText(message);

        //get date
        String formattedDate = (String) android.text.format.DateFormat.format("MM/dd/yyyy h:mm a", entry.getDateTime());
        String date = formattedDate;
        //set date
        textViewDate.setText(date);

        //Set icon
        int icon;
        switch(entry.getValue()){ //offset from 0
            case -3: icon = R.drawable.entryvalue_n3;
                break;
            case -2: icon = R.drawable.entryvalue_n2;
                break;
            case -1: icon = R.drawable.entryvalue_n1;
                break;
            case 0: icon = R.drawable.entryvalue_0;
                break;
            case 1: icon = R.drawable.entryvalue_p1;
                break;
            case 2: icon = R.drawable.entryvalue_p2;
                break;
            case 3: icon = R.drawable.entryvalue_p3;
                break;
            default: icon = R.drawable.entryvalue_unk;
                break;
        }
        imageView.setImageResource(icon);

        if(menu != null && following != -1)
            menu.clear();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(following != -1)
            getMenuInflater().inflate(R.menu.menu_empty, menu);
        else
            getMenuInflater().inflate(R.menu.menu_entry_view, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit_entry) {
            Intent intent = new Intent(this, EditEntryActivity.class);
            intent.putExtra("position", position);
            startActivity(intent);
            return true;
        } else if (id == R.id.delete_entry){
            CurrentUser user = CurrentUser.getInstance(this);
            ArrayList<Entry> entries = user.getEntries();
            final Entry entry = entries.get(position);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Entry");
            query.whereEqualTo("currUser", ParseUser.getCurrentUser());
            query.whereEqualTo("message", entry.getMessage());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> entries, com.parse.ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "Retrieved " + entries.size() + " entries to delete");
                        for (ParseObject pEntry : entries) {
                            pEntry.deleteInBackground();
                        }
                        Log.d(TAG, "Entry deleted successfully");
                    } else {
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                }
            });

            //Delete locally
            user.deleteEntry(entry.getEntryObject());

            Toast.makeText(this, "Entry deleted!", Toast.LENGTH_SHORT).show();

            //go to main activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
