package com.example.sam.moodalert.Model;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sam on 5/14/2015.
 */
public class Updater { //Loads everything from parse that isn't stored
    private static Updater instance;
    private Context context;
    private String TAG = Updater.class.getSimpleName();
    private ParseUser parseUser;
    private CurrentUser currentUser;

    //Use this to create new instance and retrieve existing instance
    public static Updater getInstance(Context context){
        if(instance == null){
            instance = new Updater(context);
        }
        return instance;
    }

    private Updater(Context context) {
        this.context = context;

        //Get instance of currentUser from model
        currentUser = CurrentUser.getInstance(context);

        //attempt to retrieve currentUser - should always work
        parseUser = ParseUser.getCurrentUser();
        if (parseUser != null) {
            //Load everything!
            runUpdate();
        } else {
            Log.d(TAG, "Something went heavily wrong");
        }
    }

    public void runUpdate(){
        getEntries();
        getFollowers();
        getFriends();
    }

    public void getEntries(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Entry");
        query.whereEqualTo("currUser", parseUser);
        if(!isConnected()){
            query.fromLocalDatastore();
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> entryList, com.parse.ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Entry Retrieved " + entryList.size() + " entries");
                    for(ParseObject parseEntry : entryList){
                        //Create entry
                        Entry entry = new Entry(parseEntry);
                        currentUser.addEntry(entry);
                    }

                    //notify splash screen
                    Intent intent = new Intent("SPLASH_UPDATE");
                    intent.putExtra("OFFLINE_MODE", false);
                    context.sendBroadcast(intent);
                } else {
                    Intent intent = new Intent("SPLASH_UPDATE");
                    intent.putExtra("OFFLINE_MODE", true);
                    context.sendBroadcast(intent);
                    Log.d("Entry", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void getFollowers(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Followers");
        query.whereEqualTo("toUser", parseUser);
        query.whereExists("accepted");
        query.include("fromUser"); //get items pointed to
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, com.parse.ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Followers Retrieved " + userList.size() + " entries");
                    for(ParseObject parseEntry : userList){
                        //get from user
                        ParseUser parseTo = parseEntry.getParseUser("fromUser");
                        //convert into OtherUser in model
                        String firstName = parseTo.getString("firstName");
                        String lastName = parseTo.getString("lastName");
                        Date createdAt = parseEntry.getCreatedAt();
                        boolean privateProfile = parseTo.getBoolean("private");
                        boolean accepted = parseTo.getBoolean("accepted");
//                        Log.d(TAG, "User Following me:" + firstName + " " + lastName);
                        loadFriendsEntries(parseTo, firstName, lastName, createdAt, privateProfile, accepted);
                    }
                } else {
                    Log.d(TAG, "Followers Error: " + e.getMessage());
                }
            }
        });
    }

    //People that I follow
    public void getFriends(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Followers");
        query.whereEqualTo("fromUser", parseUser);
        query.whereEqualTo("accepted", true); //Only get accepted following's
        query.include("toUser"); //get items pointed to
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, com.parse.ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Following Retrieved " + userList.size() + " entries");
                    for(ParseObject parseEntry : userList){
                        //get from user
                        ParseUser parseTo = parseEntry.getParseUser("toUser");
                        //convert into OtherUser in model
                        String firstName = parseTo.getString("firstName");
                        String lastName = parseTo.getString("lastName");
                        Date createdAt = parseEntry.getCreatedAt();
                        boolean privateProfile = parseTo.getBoolean("private");
                        loadFriendsEntries(parseTo, firstName, lastName, createdAt, privateProfile, true);
                    }
                } else {
                    Log.d(TAG, "Following Error: " + e.getMessage());
                }
            }
        });
    }

    static boolean completed = false;
    public static void setCompleted(boolean input){
        completed = input;
    }

    public boolean loadFriendsEntries(final ParseUser parseUser, final String firstName, final String lastName, final Date createdAt, final boolean privateProfile, final boolean accepted){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Entry");
        query.whereEqualTo("currUser", parseUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> entryList, com.parse.ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Following Entries Retrieved " + entryList.size() + " entries");
                    ArrayList<Entry> followingEntries = new ArrayList<>();

                    for(ParseObject parseEntry : entryList){
                        //Create following entry
                        Entry entry = new Entry(parseEntry);
                        followingEntries.add(entry);
                    }

                    Friend user = new Friend(parseUser.getObjectId(), firstName, lastName, createdAt, followingEntries, privateProfile, parseUser, accepted);
                    //Add entry to model
                    setCompleted(currentUser.addFriend(user));
                    //Broadcast load finish
                    Intent intent = new Intent("FOLLOWING_UPDATE");
                    context.sendBroadcast(intent);

                } else {
                    Log.d(TAG, "Following Entries Error: " + e.getMessage());
                }
            }
        });
        return completed;
    }

    public boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
