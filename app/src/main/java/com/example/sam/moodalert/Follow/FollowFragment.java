package com.example.sam.moodalert.Follow;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sam.moodalert.Follow.Search.AddUserActivity;
import com.example.sam.moodalert.MainActivity;
import com.example.sam.moodalert.R;

/*
Houses following and followers fragments
 */

public class FollowFragment extends Fragment {

    public static final String TAG = FollowFragment.class.getSimpleName();
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    SharedPreferences sharedPref;
    private static final String LAST_OPEN = "last_open";
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static FollowFragment newInstance(int sectionNumber) {
        FollowFragment fragment = new FollowFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Change actionbar menu
        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
    }

    //Set fragment actionbar menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_follow, menu);
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
        if (id == R.id.add_user) {
            //TODO: Perform action here
            //start new entry
            Intent intent = new Intent(getActivity(), AddUserActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_follow, container, false);
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getChildFragmentManager());

        mViewPager = (ViewPager) v.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Load last opened page
        Context context = getActivity();
        sharedPref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        int defaultPage = 0;
        int page = sharedPref.getInt(LAST_OPEN, defaultPage);

        //set page to retrieved shared pref
        mViewPager.setCurrentItem(page);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        ((MainActivity) getActivity()).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        super.onAttach(activity);
    }

    @Override
    public void onResume(){
        super.onResume();
        ((MainActivity) getActivity()).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new TabbedContentFragment();
            if(position == 0){
                fragment = new FollowerFragment();
                Bundle args = new Bundle();
                args.putInt(TabbedContentFragment.ARG_SECTION_NUMBER_CHILD, position + 1);
                fragment.setArguments(args);
            } else if(position == 1){
                fragment = new FriendsFragment();
                Bundle args = new Bundle();
                args.putInt(TabbedContentFragment.ARG_SECTION_NUMBER_CHILD, position + 1);
                fragment.setArguments(args);
            }

            //Set last open in shared pref
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(LAST_OPEN, position);
            editor.commit();

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_activity_followers).toUpperCase(l);
                case 1:
                    return getString(R.string.title_activity_following).toUpperCase(l);
            }
            return null;
        }
    }

    public static class TabbedContentFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER_CHILD = "section_number";

        public TabbedContentFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tabbed_content,
                    container, false);
            TextView dummyTextView = (TextView) rootView
                    .findViewById(R.id.section_label);
            dummyTextView.setText(Integer.toString(getArguments().getInt(
                    ARG_SECTION_NUMBER_CHILD)));
            return rootView;
        }
    }

}