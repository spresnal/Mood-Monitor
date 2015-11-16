package com.example.sam.moodalert.Follow.Search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sam.moodalert.Model.OtherUser;
import com.example.sam.moodalert.R;

import java.util.ArrayList;

/**
 * Created by sam on 5/15/2015.
 */
public class SearchArrayAdapter extends ArrayAdapter<OtherUser> {
    private final Context context;
    private final ArrayList<OtherUser> otherUsers;

    public SearchArrayAdapter(Context context, ArrayList<OtherUser> otherUsers) {
        super(context, R.layout.search_entry_adapter, otherUsers);
        this.context = context;
        this.otherUsers = otherUsers;
    }

    @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.search_entry_adapter, parent, false);

        TextView textViewName = (TextView) rowView.findViewById(R.id.name);
        TextView textViewEmail = (TextView) rowView.findViewById(R.id.email);

        String name = otherUsers.get(position).getFirstName() + " " + otherUsers.get(position).getLastName();
        String email = otherUsers.get(position).getEmail();

        //Set text to name
        textViewName.setText(name);
        //Set email
        textViewEmail.setText(email);

        return rowView;
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
