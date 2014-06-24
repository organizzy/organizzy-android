package org.organizzy.client.android.alarm;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.webkit.CookieManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.organizzy.client.android.R;
import org.organizzy.client.android.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.TimeZone;

public class AlarmService extends Service {


    public static void registerService(Context context) {
        registerService(context, null);
    }

    public static void registerService(Context context, String sid) {
        AlarmPreference preference = AlarmPreference.getDefault(context);

        if (! preference.isEnable()) {
            return;
        }

        if (sid != null) {
            preference.setSessionID(sid).setLastSync(0).commit();
        } else if (preference.getSessionID() == null) {
            return;
        }

        long lastRun = preference.getLastSync();
        int recurrence = preference.getSyncInterval();

        long nextRun = lastRun + recurrence;
        long now = SystemClock.elapsedRealtime();
        if (lastRun > now || nextRun < now) {
            nextRun = now;
        }


        Intent intent = new Intent(context, AlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                nextRun, recurrence, pendingIntent);

    }

    public static void unregisterService(Context context) {
        Intent intent = new Intent(context, AlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
    }

    private AlarmManager am;
    private AlarmPreference preference;

    @Override
    protected Object execute(Intent intent) {
        preference = AlarmPreference.getDefault(this);

        if (!preference.isEnable()) {
            return null;
        }

        preference.setLastSync(SystemClock.elapsedRealtime()).commit();

        Calendar calendar = Calendar.getInstance();
        String date = String.format("%04d-%02d-%02d",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DATE));

        HttpClient client = new DefaultHttpClient();
        String url = getString(R.string.server) + "/activity/get?date=" + date;
        HttpUriRequest req = new HttpGet(url);
        String cookie = CookieManager.getInstance().getCookie(url);
        req.addHeader("Cookie", cookie);
        req.addHeader("User-Agent", "AlarmFetcher/1.0 (tz=" + TimeZone.getDefault().getID() + ")");
        try {
            HttpResponse response = client.execute(req);

            BufferedReader rd = new BufferedReader
                    (new InputStreamReader(response.getEntity().getContent()));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuilder.append(line);
            }

            String data = stringBuilder.toString();

            if (processData(data))
                preference.setCachedData(data).commit();

        }
        catch (IOException e) {
            e.printStackTrace();
            String data = preference.getCachedData();
            if (data != null) processData(data);
        }
        return null;
    }

    private boolean processData(String data) {
        try {
            JSONObject jsonResponse = new JSONObject(data);
            JSONArray results = jsonResponse.getJSONArray("result");
            am = (AlarmManager) getSystemService(ALARM_SERVICE);
            for(int i=0; i<results.length(); i++) {
                registerAlarm(results.getJSONObject(i));
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void registerAlarm(JSONObject item) throws JSONException {
        int id = item.getInt("id");
        if (preference.isItemRead(id)) {
            return;
        }

        Intent i = new Intent(this, AlarmReceiver.class);

        AlarmData data = new AlarmData(id);
        data.title = item.getString("title");
        data.url = item.getString("url");
        data.type = item.getString("type");
        data.time = item.getLong("datetime");
        i.putExtra("data", data);
        i.putExtra("sid", preference.getSessionID());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, i, 0);
        Long time = data.time;
        Long delta = System.currentTimeMillis() - time;
        if (delta > 0) {
            int reminderBefore = preference.getReminderBefore();
            if (delta > reminderBefore) {
                time -= reminderBefore;
            } else {
                int reminderInterval = preference.getReminderInterval();
                if (delta > reminderInterval) {
                    time = time - reminderBefore + reminderInterval;
                }
            }
        }

        if (Build.VERSION.SDK_INT >= 10) {
            am.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
        else {
            am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }
}

