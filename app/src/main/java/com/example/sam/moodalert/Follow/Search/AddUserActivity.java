package com.example.sam.moodalert.Follow.Search;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sam.moodalert.Model.CurrentUser;
import com.example.sam.moodalert.Model.Friend;
import com.example.sam.moodalert.Model.OtherUser;
import com.example.sam.moodalert.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class AddUserActivity extends ActionBarActivity {
    private static SearchArrayAdapter adapter;
    ArrayList<OtherUser> searchEntries;
    ListView listview;
    private Activity activity;
    private static final String TAG = AddUserActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        activity = this;

        //Show back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Search intent
        handleIntent(getIntent());

        searchEntries = new ArrayList<>();

        listview = (ListView) findViewById(R.id.listView);
        adapter = new SearchArrayAdapter(activity, searchEntries);
        listview.setAdapter(adapter);

        //Clicking a user
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OtherUser user =  searchEntries.get(position);
                CurrentUser.getInstance(activity).addSearchResult(user);

                Intent intent = new Intent(AddUserActivity.this, AddUserAction.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //clear currently displayed data
            adapter.clear();

            String queryString = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "Search Query:" + queryString);
            //use the query to search your data somehow

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            if(queryString.contains("@")) { //test@test.com
                query.whereEqualTo("email", queryString.toLowerCase());
            } else if(queryString.contains(" ")){ //firstName lastName
                query.whereEqualTo("firstName", queryString.split(" ")[0].toLowerCase());
                query.whereEqualTo("lastName", queryString.split(" ")[1].toLowerCase());
            } else { //firstName
                query.whereEqualTo("firstName", queryString.toLowerCase());
            }

//            //filter out already added
            for(Friend user : CurrentUser.getInstance(activity).getFriends()) {
                Log.d(TAG, "Filtering Out:" + user.getParseUser().getEmail());
//                query.whereNotEqualTo("email", user.getParseUser().getEmail());
            }
            //filter out self
//            query.whereNotEqualTo("email", ParseUser.getCurrentUser().getEmail());
//            Log.d(TAG, "Filtering Out Self:" + ParseUser.getCurrentUser().getEmail());

            query.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> userList, ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "Retrieved " + userList.size() + " users");
                        searchEntries.clear();

                        for(ParseUser parseUser : userList){
                            String firstName = parseUser.getString("firstName");
                            String lastName = parseUser.getString("lastName");
                            String email = parseUser.getString("email");
                            String userId = parseUser.getObjectId();

                            //(String userId, String firstName, String lastName)
                            OtherUser otherUser = new OtherUser(userId, firstName, lastName, parseUser);
                            otherUser.setEmail(email);
                            otherUser.setParseUser(parseUser);

                            searchEntries.add(otherUser);
//                            adapter.add(otherUser);
                        }

                        //notify data has changed
                        adapter.notifyDataSetChanged();

                        //Set up list adapter

//                        adapter = new SearchArrayAdapter(activity, searchEntries);
//                        listview.setAdapter(adapter);

                    } else {
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_user, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setFocusable(true);
        searchView.setIconified(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
