package com.example.sam.moodalert.Model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.sam.moodalert.Reminder.ReminderService;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by sam on 5/14/2015.
 */
public class CurrentUser {
    private static CurrentUser instance;
    private Context context;
    private ArrayList<Entry> entries;
    private ArrayList<Reminder> reminders;
    private ArrayList<SupportEntry> supportEntries;
    private ArrayList<Follower> followers; //People that follow me
    private ArrayList<Friend> friends; //People that I follow
    private String firstName;
    private String lastName;
    private int highestEntry;
    private final String TAG = CurrentUser.class.getSimpleName();

    //Use this to create new instance and retrieve existing instance
    public static CurrentUser getInstance(Context context){
        if(instance == null){
            instance = new CurrentUser(context);
        }
        return instance;
    }

    private CurrentUser(Context context){
        this.context = context;

        entries = new ArrayList<>();
        supportEntries = new ArrayList<>();
        followers = new ArrayList<>();
        friends = new ArrayList<>();
        reminders = new ArrayList<>();
    }

    //Call this after loading the information
    public void updated(){
        Date lastLoad = new Date();
    }

    public void saveEntries(){
        for(Entry e: entries){
            e.saveOffline();
        }
    }

    public void addEntry(Entry entry){
        //Make sure entry doesn't exist by checking unique ID
        if(!entries.contains(entry))
            entries.add(entry);
        Collections.sort(entries);
        Intent intent = new Intent("ENTRY_UPDATE");
        context.sendBroadcast(intent);
    }

    //Creates new alarm because not loading from local storage
    public void addReminder(Reminder reminder){
        if(!reminders.contains(reminder)) {
            reminders.add(reminder);

            //Set up alarm
            Intent myIntent = new Intent(context, ReminderService.class);
            //hh*60+mm as the ID makes sure the pending intent is unique
            PendingIntent pendingIntent = PendingIntent.getService(context, (reminder.getHours()*60)+reminder.getMinutes(), myIntent, 0);
//            Toast.makeText(context, "addReminder.ID:"+(reminder.getHours()*60)+reminder.getMinutes(), Toast.LENGTH_LONG).show();
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
            reminder.setPendingIntent(pendingIntent, alarmManager);

            //start alarm
            reminder.startReminder();
        }
        Collections.sort(reminders);
    }

    //Doesn't create new reminder alarm if loading from local storage
    public void loadReminder(Reminder reminder){
        if(!reminders.contains(reminder)) {
            reminders.add(reminder);

            //Set up alarm
            Intent myIntent = new Intent(context, ReminderService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, (reminder.getHours()*60)+reminder.getMinutes(), myIntent, 0);
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
            reminder.setPendingIntent(pendingIntent, alarmManager);

            //Don't start because it's the same
        }
        Collections.sort(reminders);
    }

    public Reminder getReminder(int position){
        return(reminders.get(position));
    }

    public void removeReminder(int position){
        Log.d(TAG, "Removing reminder: " + getReminder(position).getString());
        Reminder r = reminders.remove(position);
        //Destroy reminderService
        r.close();
    }

    public void removeReminder(Reminder r){
        Log.d(TAG, "Removing reminder: " + r.getString());
        r.close();
        reminders.remove(r);
    }

    public ArrayList<Reminder> getReminders(){
        return reminders;
    }

    public int getReminderSize(){
        return reminders.size();
    }

    public void deleteEntry(ParseObject entryObject){
        for(Entry entry : entries){
            if(entry.getEntryObject().getObjectId() == entryObject.getObjectId()){
                entries.remove(entry);
                break;
            }
        }
    }

    private static OtherUser oUser;
    public void addSearchResult(OtherUser oUser){
        this.oUser = oUser;
    }

    public OtherUser getSearchResult(){
        return oUser;
    }

    public ArrayList<Friend> getSortedFriends() {

        Collections.sort(friends, new Comparator() {

            public int compare(Object o1, Object o2) {

                Boolean x1 = ((Friend) o1).isAccepted();
                Boolean x2 = ((Friend) o2).isAccepted();
                int sComp = x1.compareTo(x2);

                if (sComp != 0) {
                    return sComp;
                } else {
                    Date y1 = ((Friend) o1).getCreatedAt();
                    Date y2 = ((Friend) o2).getCreatedAt();
                    return y2.compareTo(y1);
                }
            }
        });

        return friends;
    }

    public boolean addFriend(Friend friend){
        if(!this.friends.contains(friend) && friend.getId() != ParseUser.getCurrentUser().getObjectId()) {
            this.friends.add(friend);
            return true;
        }
        return false;
    }

    public boolean friendsContains(OtherUser user){
        if(!this.friends.contains(new Friend(user.getParseUser().getObjectId(), user.getFirstName(), user.getLastName(), user.getParseUser().getCreatedAt(), null, false, user.getParseUser(), true))) {
            return true;
        }
        return false;
    }

    public Friend getFriends(int position){
        return friends.get(position);
    }

    public ArrayList<Friend> getFriends(){
        return friends;
    }

    public ArrayList<Follower> getFollowers(){
        return followers;
    }

    public ArrayList<Entry> getEntries(){
        Collections.sort(entries);
        return entries;
    }

    public ArrayList<SupportEntry> getSupportEntries() {
        return supportEntries;
    }

    public ArrayList<String> getEntryMessages() {
        ArrayList<String> messages = new ArrayList<>();
        for(Entry entry : entries){
            messages.add(entry.getMessage());
        }
        return messages;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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
