package com.example.sam.moodalert.Follow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sam.moodalert.Model.Entry;
import com.example.sam.moodalert.Model.Follower;
import com.example.sam.moodalert.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sam on 5/15/2015.
 */
public class FollowerArrayAdapter extends ArrayAdapter<Follower> {
    private final Context context;
    private final ArrayList<Follower> followers;

    public FollowerArrayAdapter(Context context, ArrayList<Follower> followers) {
        super(context, R.layout.follower_entry_adapter, followers);
        this.context = context;
        this.followers = followers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.follower_entry_adapter, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.name);
        TextView textViewDate = (TextView) rowView.findViewById(R.id.date);

        Follower follower = followers.get(position);

        //get name
        String name = follower.getFirstName() + " " + follower.getLastName();
        //Set text to message
        textView.setText(name);

        //today
        Date today = new Date();
        //date
        Date date = follower.getLastUpdated();
        //week old date
        long DAY_IN_MS = 1000 * 60 * 60 * 24;

        //Date no time in millis
        long todayNoTime = getDateNoTime(today).getTimeInMillis();
        long yesterdayNoTime = getDateNoTime(new Date(today.getTime() - (1 * DAY_IN_MS))).getTimeInMillis();
        long weekAgoNoTime = getDateNoTime(new Date(today.getTime() - (7 * DAY_IN_MS))).getTimeInMillis();
        long dateNoTime = getDateNoTime(date).getTimeInMillis();

        //format according to older than a week or not
        String dateString = "error";

        if(todayNoTime == dateNoTime){ //today
            //get date
            String formattedDate = (String) android.text.format.DateFormat.format("MM/dd/yyyy h:mm a", date);
            SimpleDateFormat inFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
            try {
                Date inDate = inFormat.parse(formattedDate);
                SimpleDateFormat outFormat = new SimpleDateFormat("h:mm a");
                dateString = "Today " + outFormat.format(inDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if(yesterdayNoTime == dateNoTime){ //yesterday
            //get date
            String formattedDate = (String) android.text.format.DateFormat.format("MM/dd/yyyy h:mm a", date);
            SimpleDateFormat inFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
            try {
                Date inDate = inFormat.parse(formattedDate);
                SimpleDateFormat outFormat = new SimpleDateFormat("h:mm a");
                dateString = "Yesterday " + outFormat.format(inDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if(weekAgoNoTime < dateNoTime){
            //get date
            String formattedDate = (String) android.text.format.DateFormat.format("MM/dd/yyyy h:mm a", date);
            SimpleDateFormat inFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
            try {
                Date inDate = inFormat.parse(formattedDate);
                SimpleDateFormat outFormat = new SimpleDateFormat("EEEE h:mm a");
                dateString = outFormat.format(inDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            //get date
            String formattedDate = (String) android.text.format.DateFormat.format("MM/dd/yyyy h:mm a", date);
            dateString = formattedDate;
        }
        //set date
        textViewDate.setText("Last Online: " + dateString);

        return rowView;
    }

    private Calendar getDateNoTime(Date date) {
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }
}
