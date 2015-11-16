package com.example.sam.moodalert.Follow.Search;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sam.moodalert.Model.CurrentUser;
import com.example.sam.moodalert.Model.OtherUser;
import com.example.sam.moodalert.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;

public class AddUserAction extends Activity {
    private String TAG = AddUserAction.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_user_action);
        final Activity activity = this;

        final OtherUser user = CurrentUser.getInstance(activity).getSearchResult();

        String name = user.getFirstName() + " " + user.getLastName();

        TextView nameView = (TextView) findViewById(R.id.name);
        nameView.setText("Add " + name + "?");

        Button addButton = (Button) findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Send everything to parse
                ParseUser currentUser = ParseUser.getCurrentUser();

                //Add locally
//                boolean successfulAdd = Updater.getInstance(activity).loadFriendsEntries(
//                        user.getParseUser(),
//                        user.getFirstName(),
//                        user.getLastName(),
//                        user.getParseUser().getCreatedAt(),
//                        user.getParseUser().getBoolean("private"));

                if (CurrentUser.getInstance(activity).friendsContains(user)) { //sending a user request
                    //Push notification
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("userId", user.getParseUser().getObjectId());
                    ParseCloud.callFunctionInBackground("friendRequest", params, new FunctionCallback<Float>() {
                        @Override
                        public void done(Float aFloat, com.parse.ParseException e) {
                            if (e == null) {
                                Toast.makeText(activity, "Request sent successfully", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });

                    final ParseObject pEntry = new ParseObject("Followers");
                    pEntry.put("fromUser", currentUser);
                    pEntry.put("toUser", user.getParseUser());
                    pEntry.put("accepted", false);
                    //Online
                    if (isConnected()) {
                        pEntry.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {
                                if (e == null) {
                                    Log.d(TAG, "Online saveOffline successful");
                                } else {
                                    Log.d(TAG, "Error: " + e.getMessage());
                                    pEntry.saveEventually();
                                }
                            }
                        });
                    } else { //Offline
                        Log.d(TAG, "Offline saveOffline successful");
                        pEntry.saveEventually();
                    }
                    Toast.makeText(activity, "Request sent successfully", Toast.LENGTH_LONG).show();
                    finish();
                } else { //accepting a user request
                    //get query using from user and to (currentUser) update accepted to true
                }
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
