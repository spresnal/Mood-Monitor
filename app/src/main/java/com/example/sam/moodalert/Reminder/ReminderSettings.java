package com.example.sam.moodalert.Reminder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sam.moodalert.Entry.NewEntryActivity;
import com.example.sam.moodalert.Library.FloatingActionButton;
import com.example.sam.moodalert.MainActivity;
import com.example.sam.moodalert.Model.CurrentUser;
import com.example.sam.moodalert.Model.Entry;
import com.example.sam.moodalert.Model.Reminder;
import com.example.sam.moodalert.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Queue;

public class ReminderSettings extends ActionBarActivity {
    private ListView listView;
    private boolean multiSelect;
    private String TAG = ReminderSettings.class.getSimpleName();
    private Activity activity;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_alarm_settings);

        //Show back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //load reminders from internal storage
        loadReminders();

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
        multiSelect = false;
        activity = this;

        //floating button
        fab = new FloatingActionButton.Builder(activity)
                .withDrawable(getResources().getDrawable(R.drawable.ic_tab_add))
                .withButtonColor(getResources().getColor(R.color.Theme))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, NewReminder.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Load from storage
    public void loadReminders() {
        Log.d(TAG, "Loading Reminders");
        final String FILENAME = "reminders.json";
        BufferedReader reader = null;
        try {
            //Open and read file into a StringBuilder
            InputStream in = openFileInput(FILENAME);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                jsonString.append(line);
            }

            //Parse JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

            //Build array of entries from JSONObjects
            CurrentUser currentUser = CurrentUser.getInstance(this);
            for(int i = 0; i < array.length(); i++){
                currentUser.addReminder(new Reminder(array.getJSONObject(i)));
            }

        } catch (IOException|JSONException e) {
            e.printStackTrace();
        } finally {
            if(reader != null)
                try {
                    reader.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.d(TAG, "onResume");

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.mylist, android.R.id.text1, getReminderStrings());

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, NewReminder.class);

                //bundle with intent
                intent.putExtra("POSITION", position);

                startActivity(intent);
            }
        });

        final int MAX_ALARMS = CurrentUser.getInstance(this).getReminderSize();
        final boolean[] selected = new boolean[MAX_ALARMS];
        for(int i = 0; i < MAX_ALARMS; i++)
            selected[i] = false;

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        // Capture ListView item click
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = listView.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                View item = getViewByPosition(position, listView);
                if(selected[position]){
                    item.setBackgroundColor(0x00000000);
                    selected[position] = false;
                } else {
                    item.setBackgroundColor(0xff33b5e5);
                    selected[position] = true;
                }
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete_entry:
                        // Calls getSelectedIds method from ListViewAdapter Class
                        //TODO: better implementation for this
                        ArrayList<Reminder> removeReminderList = new ArrayList<>();
                        for(int i = 0; i < MAX_ALARMS; i++){
                            if(selected[i]) {
                                removeReminderList.add(CurrentUser.getInstance(activity).getReminder(i));
                                selected[i] = false;
                            }
                        }

                        //Remove reminders in list
                        for(Reminder r : removeReminderList){
                            CurrentUser.getInstance(activity).removeReminder(r);
                        }
                        removeReminderList.clear();

                        finish();
                        startActivity(getIntent());

                        return true;
                    default:
                        return false;
                }
            }

            public View getViewByPosition(int pos, ListView listView) {
                final int firstListItemPosition = listView.getFirstVisiblePosition();
                final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

                if (pos < firstListItemPosition || pos > lastListItemPosition ) {
                    return listView.getAdapter().getView(pos, null, listView);
                } else {
                    final int childIndex = pos - firstListItemPosition;
                    return listView.getChildAt(childIndex);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub
                for(int i = 0; i < CurrentUser.getInstance(activity).getReminderSize(); i++) {
                    View item = getViewByPosition(i, listView);
                    if(selected[i]){
                        item.setBackgroundColor(0x00000000);
                        selected[i] = false;
                    }
                }
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }
        });

        //show fab
        fab.showFloatingActionButton();
    }

    private ArrayList<String> getReminderStrings() {
        CurrentUser currentUser = CurrentUser.getInstance(this);
        ArrayList<Reminder> reminders = currentUser.getReminders();

        ArrayList<String> remindersString = new ArrayList<>();

        if(reminders.size() > 0) {
            for (Reminder r : reminders) {
                remindersString.add(r.getString());
            }
        }
        return remindersString;
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "Saving Reminders");
        final String FILENAME = "reminders.json";
        JSONArray array = new JSONArray();
        Writer writer = null;

        try {
            //Add entries to json array
            CurrentUser currentUser = CurrentUser.getInstance(this);
            ArrayList<Reminder> reminders = currentUser.getReminders();

            for (Reminder r : reminders) {
                array.put(r.toJSON());
            }

            OutputStream out = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());

        }catch(IOException | JSONException e){
            e.printStackTrace();
        }finally{
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        //hide fab
        fab.hideFloatingActionButton();
    }
}
