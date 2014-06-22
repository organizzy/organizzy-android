package org.organizzy.client.android.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import org.organizzy.client.android.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmData data = intent.getParcelableExtra("data");

        Intent targetIntent = new Intent(context, AlarmActivity.class);
        int flag = Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 11) {
            flag |= Intent.FLAG_ACTIVITY_CLEAR_TASK;
        }
        targetIntent.addFlags(flag);
        targetIntent.putExtra("data", data);

        StringBuilder description = new StringBuilder();
        if (data.isEvent()) {
            description.append("Event: ");
        } else if (data.isTask()) {
            description.append("Task: ");
        }

        description.append(DateFormat.getDateTimeInstance().format(new Date(data.time)));

        Notification n = new NotificationCompat.Builder(context)
                .setContentTitle(data.title)
                .setContentText(description.toString())
                .setTicker("Reminder: " + data.title)
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(PendingIntent.getActivity(context, data.id, targetIntent, 0))
                .setAutoCancel(true)
                .build();

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(data.id, n);
    }
}
