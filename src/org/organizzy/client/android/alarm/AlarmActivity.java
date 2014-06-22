package org.organizzy.client.android.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.organizzy.client.android.MainActivity;
import org.organizzy.client.android.R;

public class AlarmActivity extends Activity {
    private AlarmData data;
    private AlarmPreference preference;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);


        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        TextView txtDescription = (TextView) findViewById(R.id.txtDescription);

        data = getIntent().getParcelableExtra("data");

        txtTitle.setText(data.title);
        txtDescription.setText(data.getDescription());


        findViewById(R.id.btnView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnViewOnClick(view);
            }
        });

        findViewById(R.id.btnDismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDismissOnClick(view);
            }
        });

        findViewById(R.id.btnRepeat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRepeatOnClick(view);
            }
        });

        preference = AlarmPreference.getDefault(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        preference.setItemRead(data.id, false).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        preference.setItemRead(data.id, true).commit();

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(data.id);
    }

    private void btnViewOnClick(View view) {
        preference.setItemRead(data.id, false).commit();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.STARTUP_PAGE, data.url);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityIfNeeded(intent, data.id);
        finish();
    }

    private void btnDismissOnClick(View view) {
        finish();
    }

    private void btnRepeatOnClick(View view) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("data", data);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, data.id, intent, 0);
        Long time = System.currentTimeMillis() + preference.getReminderInterval();

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 10) {
            am.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
        else {
            am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }

        Toast.makeText(this, "OK", Toast.LENGTH_LONG).show();

        finish();
    }

}