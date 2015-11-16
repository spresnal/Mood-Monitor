package com.example.sam.moodalert.Model;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sam on 5/15/2015.
 */
public class Follower extends OtherUser {
    private Date createdAt, lastUpdated;
    private ParseUser pUser;

    public Follower(String userId, String firstName, String lastName, Date createdAt, ParseUser pUser){
        super(userId, firstName, lastName, pUser);
        this.createdAt = createdAt;
    }

    public void setLastUpdated(Date date){
        lastUpdated = date;
    }

    public Date getLastUpdated(){
        return lastUpdated;
    }
}
