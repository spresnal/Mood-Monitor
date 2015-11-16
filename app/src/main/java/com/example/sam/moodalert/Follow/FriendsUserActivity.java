package com.example.sam.moodalert.Follow;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sam.moodalert.Entry.EntryActivity;
import com.example.sam.moodalert.Entry.EntryArrayAdapter;
import com.example.sam.moodalert.Model.CurrentUser;
import com.example.sam.moodalert.Model.Friend;
import com.example.sam.moodalert.R;

//Displays user profile with past entries
public class FriendsUserActivity extends ActionBarActivity {
    private static ListAdapter adapter;
    private static CurrentUser currentUser;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_user);
        activity = this;

        final ListView listview = (ListView) findViewById(R.id.listView);
        currentUser = CurrentUser.getInstance(this);

        //get position from intent
        Intent intent = this.getIntent();
        final int followingPosition = intent.getIntExtra("position", -1);

        Friend friend = currentUser.getFriends(followingPosition);
        final boolean privacySetting = friend.isPrivate();

        //Set title to user's name
        setTitle(friend.getFirstName() + " " + friend.getLastName());

        adapter = new EntryArrayAdapter(this, friend.getEntries(), privacySetting);
        listview.setAdapter(adapter);

        //Clicking an entry
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!privacySetting) {
                    Intent intent = new Intent(activity, EntryActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("followingPosition", followingPosition);
                    startActivity(intent);
                } else {
                    Toast.makeText(activity, "This profile is set to private", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
