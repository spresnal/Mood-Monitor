package com.example.sam.moodalert.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sam on 5/14/2015.
 */
public class Entry implements Comparable<Entry> {
    private ParseObject entry;

    //retrieved parse object
    public Entry(ParseObject entry){
        this.entry = entry;
    }

    //new parse Entry Object
    public Entry(int value, String message){
        final ParseObject pEntry = new ParseObject("Entry");
        pEntry.put("value", value);
        pEntry.put("message", message);
        pEntry.put("currUser", ParseUser.getCurrentUser());
        pEntry.saveEventually();
        this.entry = pEntry;
    }

    public ParseObject getEntryObject(){
        return entry;
    }

    public String getObjectId(){
        return entry.getObjectId();
    }

    @Override
    public int compareTo(Entry e){
        return e.getDateTime().compareTo(getDateTime());
    }

    @Override
    public boolean equals(Object o){
        Entry e = (Entry)o;
        if(e.getObjectId() == getObjectId())
            return true;
        return false;
    }

    public void saveOffline(){
        entry.pinInBackground();
    }

    public String getMessage() {
        return entry.getString("message");
    }

    public void setMessage(String message) {
        entry.put("message", message);
        entry.saveEventually();
    }

    public int getValue() { return entry.getInt("value"); }

    public void setValue(int value) {
        entry.put("value", value);
        entry.saveEventually();
    }

    public Date getDateTime(){
        return entry.getCreatedAt();
    }

    public String getDate(){
        return entry.getDate("createdAt").toString();
    }
}
