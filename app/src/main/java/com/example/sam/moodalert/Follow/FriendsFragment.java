package com.example.sam.moodalert.Follow;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.sam.moodalert.Follow.Search.AddUserActivity;
import com.example.sam.moodalert.Library.FloatingActionButton;
import com.example.sam.moodalert.MainActivity;
import com.example.sam.moodalert.Model.CurrentUser;
import com.example.sam.moodalert.Model.OtherUser;
import com.example.sam.moodalert.R;

/*
LIST OF PEOPLE THAT ARE FOLLOWING ME
 */

public class FriendsFragment extends ListFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private OnFollowingFragmentInteractionListener mListener;

    private CurrentUser currentUser;

    FloatingActionButton fab;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private static ListAdapter adapter;

    // TODO: Rename and change types of parameters
    public static FriendsFragment newInstance(int sectionNumber) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FriendsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = CurrentUser.getInstance(getActivity());

        setListAdapter(adapter = new FriendsArrayAdapter(getActivity(), currentUser.getSortedFriends()));

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
                Intent intent = new Intent(getActivity(), AddUserActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFollowingFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        //set attached section for title purposes
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        //Set up update broadcast receiver
        getActivity().registerReceiver(FollowingObserver, new IntentFilter("FOLLOWING_UPDATE"));
        //Update UI
        setListAdapter(adapter);
        //set attached section for title purposes
        ((MainActivity) getActivity()).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        //show fab
        fab.showFloatingActionButton();
    }

    @Override
    public void onPause(){
        super.onPause();

        //Hide fab
        fab.hideFloatingActionButton();

        //Unregister entry updater
        getActivity().unregisterReceiver(FollowingObserver);
    }

    private final BroadcastReceiver FollowingObserver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //update adapter
            setListAdapter(adapter);
        }
    };

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            OtherUser user =  currentUser.getSortedFriends().get(position);
            CurrentUser.getInstance(getActivity()).addSearchResult(user);

            mListener.OnFollowingFragmentInteraction(position, currentUser.getSortedFriends().get(position).isAccepted());
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFollowingFragmentInteractionListener {
        // TODO: Update argument type and name
        public void OnFollowingFragmentInteraction(int position, boolean accepted);
    }

}
