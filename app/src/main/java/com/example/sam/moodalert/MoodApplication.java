package com.example.sam.moodalert;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import java.text.ParseException;

/**
 * Created by sam on 5/25/2015.
 */
public class MoodApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "wGFBxonbJwCSG3x104nT3VbNkyepGZlBTv6xDv18", "wB8AovfMzwhFFvagAizSzaa0WJrT3Li8oKv3oKKR");

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }
}
