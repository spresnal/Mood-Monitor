package com.example.sam.moodalert.Model;

import java.util.Date;

/**
 * Created by sam on 5/14/2015.
 */
public class SupportEntry {
    String message;
    Date createdAt;
    OtherUser from;

    public SupportEntry(String message, Date createdAt, OtherUser from){
        this.message = message;
        this.createdAt = createdAt;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OtherUser getFrom(){
        return from;
    }
}
