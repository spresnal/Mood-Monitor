package com.example.sam.moodalert.Feedback;

import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sam.moodalert.MainActivity;
import com.example.sam.moodalert.Model.CurrentUser;
import com.example.sam.moodalert.R;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/*
Houses following and followers fragments
 */

public class FeedbackFragment extends Fragment {

    public static final String TAG = FeedbackFragment.class.getSimpleName();
    SharedPreferences sharedPref;
    private static final String LAST_OPEN = "last_open";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static CurrentUser user;

    public static FeedbackFragment newInstance(int sectionNumber) {
        FeedbackFragment fragment = new FeedbackFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Change actionbar menu
        setHasOptionsMenu(true);
        user = CurrentUser.getInstance(getActivity());

        super.onCreate(savedInstanceState);
    }

    //Set fragment actionbar menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_empty, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //Override option selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_feedback, container, false);
        //Get date textview
        TextView textViewDate = (TextView) v.findViewById(R.id.date);
        //Setting date
        String formattedDate = (String) android.text.format.DateFormat.format("MM/dd/yyyy h:mm a", new Date());
        textViewDate.setText(formattedDate);

        //Listen for accept button
        Button sendButton = (Button) v.findViewById(R.id.submit_entry);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Post to parse or saveOffline offline
                EditText textViewFeedback = (EditText) v.findViewById(R.id.feedback);
                String feedback = textViewFeedback.getText().toString();

                if (feedback.length() <= 0) {
                    Toast.makeText(getActivity(), "You must enter a message first", Toast.LENGTH_LONG).show();
                } else {
                    final ParseObject pEntry = new ParseObject("Feedback");
                    pEntry.add("submission", feedback);

                    //Online
                    if (user.isConnected()) {
                        pEntry.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {
                                if (e == null) {
                                    Log.d(TAG, "Online save successful");
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

                    textViewFeedback.setText("");
                    Toast.makeText(getActivity(), "Feedback sent!", Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        ((MainActivity) getActivity()).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }
}