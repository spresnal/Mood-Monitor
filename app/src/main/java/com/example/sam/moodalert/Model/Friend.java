package com.example.sam.moodalert.Model;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by sam on 5/15/2015.
 */
public class Friend extends OtherUser implements Comparable<Friend> {
    private Date createdAt;
    private ArrayList<Entry> entries;
    private boolean privateProfile;
    private boolean accepted;

    public Friend(String userId, String firstName, String lastName, Date createdAt, ArrayList<Entry> entries, boolean privateProfile, ParseUser pUser, boolean accepted){
        super(userId, firstName, lastName, pUser);
        this.createdAt = createdAt;
        this.entries = entries;
        this.privateProfile = privateProfile;
        this.accepted = accepted;
    }

    public ArrayList<Entry> getEntries(){
        Collections.sort(entries);
        return entries;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public Date getCreatedAt(){
        return createdAt;
    }

    public void setPrivacy(boolean setting){
        privateProfile = setting;
    }

    public boolean isPrivate(){
        return privateProfile;
    }

    public Entry getNewestEntry(){
        Collections.sort(entries);
        if(entries.size()>0)
            return entries.get(0);
        return null;
    }

    @Override
    public int compareTo(Friend another) {
        return (another.getId()).compareTo(this.getId());
    }

    @Override
    public boolean equals(Object o){
        Friend f = (Friend)o;
        if(f.getId() == this.getId())
            return true;
        return false;
    }
}
