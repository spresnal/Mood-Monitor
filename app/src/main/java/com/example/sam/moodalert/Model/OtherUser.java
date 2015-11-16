package com.example.sam.moodalert.Model;

import com.parse.ParseUser;

import java.io.Serializable;

/**
 * Created by sam on 5/14/2015.
 */
public class OtherUser {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private ParseUser pUser;

    public OtherUser(String userId, String firstName, String lastName, ParseUser pUser){
        this.userId = userId;
        this.firstName = firstName.substring(0,1).toUpperCase()+firstName.substring(1).toLowerCase();
        this.lastName = lastName.substring(0,1).toUpperCase()+lastName.substring(1).toLowerCase();
        this.pUser = pUser;
    }

    public String getId(){
        return userId;
    }

    public void setId(){
        this.userId = userId;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public ParseUser getParseUser(){
        return pUser;
    }

    public void setParseUser(ParseUser pUser){
        this.pUser = pUser;
    }
}
