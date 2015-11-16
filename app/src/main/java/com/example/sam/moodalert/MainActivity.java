package com.example.sam.moodalert;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import com.example.sam.moodalert.Entry.EntriesFragment;
import com.example.sam.moodalert.Entry.EntryActivity;
import com.example.sam.moodalert.Feedback.FeedbackFragment;
import com.example.sam.moodalert.Follow.FollowerFragment;
import com.example.sam.moodalert.Follow.FriendsFragment;
import com.example.sam.moodalert.Follow.FriendsUserActivity;
import com.example.sam.moodalert.Follow.Search.AddUserAction;
import com.example.sam.moodalert.Navigation.NavigationDrawerFragment;
import com.parse.ParseInstallation;
import com.parse.ParseUser;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, EntriesFragment.OnFragmentInteractionListener,
        FollowerFragment.OnFollowerFragmentInteractionListener, FriendsFragment.OnFollowingFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private static CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //update installing id
        ParseInstallation.getCurrentInstallation().put("userPushId", ParseUser.getCurrentUser().getObjectId());
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        position++;
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(position == 1){
            fragmentManager.beginTransaction()
                    .replace(R.id.container, EntriesFragment.newInstance(position))
                    .commit();
        } else if(position == 2) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, FriendsFragment.newInstance(position))
                    .commit();
        } else if(position == 3) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, FeedbackFragment.newInstance(position))
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position))
                    .commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            menu.clear();
            getMenuInflater().inflate(R.menu.menu_empty, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Callback for when item is selected in entry fragment
    //Starts with position 0 for first item
    public void onEntryInteraction(int position){
        //Toast.makeText(this, "Item " + position + " selected!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    public void OnFollowerFragmentInteractionListener(String id) {

    }

    @Override
    public void OnFollowingFragmentInteraction(int position, boolean accepted) {
        if(!accepted){
            Intent intent = new Intent(this, AddUserAction.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, FriendsUserActivity.class);
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }

    ///////////////////////Demo Fragments Below//////////////////////

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            //String[] fragments = {"com.example.sam.moodalert.EntriesFragment"};
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
