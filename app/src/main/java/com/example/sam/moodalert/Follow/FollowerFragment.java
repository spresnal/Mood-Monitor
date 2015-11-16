package com.example.sam.moodalert.Follow;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.support.v4.app.ListFragment;

import com.example.sam.moodalert.Entry.EntryArrayAdapter;
import com.example.sam.moodalert.MainActivity;
import com.example.sam.moodalert.Model.CurrentUser;

/*
PEOPLE THAT ARE FOLLOWING ME
 */

public class FollowerFragment extends ListFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";

    private OnFollowerFragmentInteractionListener mListener;

    private CurrentUser currentUser;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private static ListAdapter adapter;

    // TODO: Rename and change types of parameters
    public static FollowerFragment newInstance(int sectionNumber) {
        FollowerFragment fragment = new FollowerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FollowerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = CurrentUser.getInstance(getActivity());

        // TODO: Change Adapter to display your content
        setListAdapter(adapter = new FollowerArrayAdapter(getActivity(), currentUser.getFollowers()));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFollowerFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
//        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
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
        getActivity().registerReceiver(FollowerObserver, new IntentFilter("FOLLOWER_UPDATE"));
        //Update UI
        setListAdapter(adapter = new FollowerArrayAdapter(getActivity(), currentUser.getFollowers()));
    }

    @Override
    public void onPause(){
        super.onPause();

        //Unregister entry updater
        getActivity().unregisterReceiver(FollowerObserver);
    }

    private final BroadcastReceiver FollowerObserver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //update adapter
            setListAdapter(adapter);
        }
    };

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
    public interface OnFollowerFragmentInteractionListener {
        // TODO: Update argument type and name
        public void OnFollowerFragmentInteractionListener(String id);
    }

}
