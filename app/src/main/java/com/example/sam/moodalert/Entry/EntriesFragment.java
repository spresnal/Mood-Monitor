package com.example.sam.moodalert.Entry;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.sam.moodalert.Library.FloatingActionButton;
import com.example.sam.moodalert.MainActivity;
import com.example.sam.moodalert.Model.CurrentUser;
import com.example.sam.moodalert.R;
import com.example.sam.moodalert.Reminder.ReminderSettings;

public class EntriesFragment extends ListFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private OnFragmentInteractionListener mListener;
    private CurrentUser currentUser;
    private static ListAdapter adapter;
    FloatingActionButton fab;

    // TODO: Rename and change types of parameters
    public static EntriesFragment newInstance(int sectionNumber) {
        EntriesFragment fragment = new EntriesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EntriesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = CurrentUser.getInstance(getActivity());

        //Change actionbar menu
        setHasOptionsMenu(true);

        setListAdapter(adapter = new EntryArrayAdapter(getActivity(), currentUser.getEntries()));

        //floating button
        fab = new FloatingActionButton.Builder(getActivity())
                .withDrawable(getResources().getDrawable(R.drawable.ic_tab_add))
                .withButtonColor(getResources().getColor(R.color.Theme))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewEntryActivity.class);
                startActivity(intent);
            }
        });
    }

    private final BroadcastReceiver EntryObserver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //update adapter
            setListAdapter(adapter);
        }
    };

    //Set fragment actionbar menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_entry, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //Override option selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.alarm_settings){
            //TODO: Perform action here
            //Toast.makeText(getActivity(), "Change Settings", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), ReminderSettings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        ((MainActivity) getActivity()).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onResume(){
        super.onResume();
        //Set up update broadcast receiver
        getActivity().registerReceiver(EntryObserver, new IntentFilter("ENTRY_UPDATE"));
        ((MainActivity) getActivity()).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        //Update UI
        setListAdapter(adapter);
        //show fab
        fab.showFloatingActionButton();
    }

    @Override
    public void onPause(){
        super.onPause();

        //pin all entries
        CurrentUser.getInstance(getActivity()).saveEntries();

        //Unregister entry updater
        getActivity().unregisterReceiver(EntryObserver);

        //Hide fab
        fab.hideFloatingActionButton();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onEntryInteraction(position);
        }
    }

    //Listens for click on entry to blow it up
    public interface OnFragmentInteractionListener{
        public void onEntryInteraction(int position);
    }
}
