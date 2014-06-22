package org.organizzy.client.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.organizzy.client.android.alarm.AlarmService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmService.registerService(context);
    }
}
