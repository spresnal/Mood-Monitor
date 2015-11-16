package com.example.sam.moodalert.Model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by sam on 5/29/2015.
 */
public class Reminder implements Comparable<Reminder> {
    private Date date;
    private final String JSON_DATE = "date";
    private final String TAG = Reminder.class.getSimpleName();
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    public Reminder(Date date){
        this.date = date;
    }

    //get values from JSON object
    public Reminder(JSONObject json) throws JSONException {
        date = new Date(json.getLong(JSON_DATE));
    }

    //turn values into JSON
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_DATE, date.getTime());

        return json;
    }

    @Override
    public int compareTo(Reminder r){
        return (new Integer(getHours()*60+getMinutes())).compareTo(new Integer(r.getHours() * 60 + r.getMinutes()));
    }

    @Override
    public boolean equals(Object o){
        Reminder r = (Reminder)o;
        if(r.getDateTime().getTime() == getDateTime().getTime())
            return true;
        return false;
    }

    public void setPendingIntent(PendingIntent pendingIntent, AlarmManager alarmManager){
        this.pendingIntent = pendingIntent;
        this.alarmManager = alarmManager;
    }

    public void startReminder(){
        Log.d(TAG, "Creating Reminder for:" + getHours() + ":" + getMinutes());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, getHours());
        calendar.set(Calendar.MINUTE, getMinutes());
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar nowCalendar = Calendar.getInstance();

        //check if equal to current time then set to tomorrow if so
        if(nowCalendar.getTimeInMillis() >= calendar.getTimeInMillis())
            calendar.add(Calendar.DATE, 1);

        this.date = calendar.getTime();

        Log.d(TAG, "New Reminder:" + (String) android.text.format.DateFormat.format("hh:mm:ss", getDateTime()));

        long delay = 24 * 60 * 60 * 1000;
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), delay, pendingIntent);
    }

    public void close(){
        Log.d(TAG, "Destroying Reminder");
        alarmManager.cancel(pendingIntent);
    }

    public String getString(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDateTime());
        if (cal.get(Calendar.AM_PM) == Calendar.PM) {
            String time = (String) android.text.format.DateFormat.format("hh:mm", getDateTime());
            time += " PM";
            return time;
        }
        String time = (String) android.text.format.DateFormat.format("hh:mm", getDateTime());
        time += " AM";
        return time;
    }

    public int getHours(){
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinutes(){
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        return calendar.get(Calendar.MINUTE);
    }

    public Date getDateTime(){
        return date;
    }
}
