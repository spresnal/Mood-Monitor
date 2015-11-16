package com.example.sam.moodalert.Reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.Toast;

import com.example.sam.moodalert.Entry.NewEntryActivity;
import com.example.sam.moodalert.R;

public class ReminderService extends Service {
    public ReminderService() {
    }

    @Override
    public void onCreate() {
// TODO Auto-generated method stub
        Toast.makeText(this, "MyAlarmService.onCreate()", Toast.LENGTH_LONG).show();

        Intent i = new Intent(this, NewEntryActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, i, 0);

// build notification
// the addAction re-use the same intent to keep the example short
        Notification n = new Notification.Builder(this)
                .setContentTitle("Mood Monitor")
                .setContentText("Time for a new entry")
                .setSmallIcon(R.drawable.new_entry_reminder)
                .setContentIntent(pIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true).build();
//                .addAction(R.drawable.icon, "Call", pIntent)
//                .addAction(R.drawable.icon, "More", pIntent)
//                .addAction(R.drawable.icon, "And more", pIntent)


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);

        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
// TODO Auto-generated method stub
        Toast.makeText(this, "MyAlarmService.onBind()", Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public void onDestroy() {
// TODO Auto-generated method stub
        super.onDestroy();
        Toast.makeText(this, "MyAlarmService.onDestroy()", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
// TODO Auto-generated method stub
        Toast.makeText(this, "MyAlarmService.onStart()", Toast.LENGTH_LONG).show();
        // prepare intent which is triggered if the
// notification is selected


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
// TODO Auto-generated method stub
        Toast.makeText(this, "MyAlarmService.onUnbind()", Toast.LENGTH_LONG).show();
        return super.onUnbind(intent);
    }
}
