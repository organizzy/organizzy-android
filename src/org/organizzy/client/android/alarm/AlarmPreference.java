package org.organizzy.client.android.alarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AlarmPreference {
    private static final String SYNC_INTERVAL = "alarm-sync-interval";
    private static final String LAST_SYNC = "alarm-sync-last";
    private static final String CACHED_DATA = "alarm-data-cache";
    private static final String REMINDER_BEFORE = "alarm-reminder-before";
    private static final String REMINDER_INTERVAL = "alarm-reminder-interval";
    private static final String ITEM_READ = "alarm-read-item-";
    private static final String SESSION_ID = "session-id";

    private static final int DEFAULT_SYNC_INTERVAL = 10000; // 5*60*1000

    private static final int DEFAULT_REMINDER_BEFORE = 7200000; // 2*60*60*1000
    private static final int DEFAULT_REMINDER_INTERVAL = 1800000; // 30*60*1000


    SharedPreferences preferences;
    SharedPreferences.Editor editor = null;

    public static AlarmPreference getDefault(Context context) {
        return new AlarmPreference(context);
    }

    private AlarmPreference(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private SharedPreferences.Editor getEditor() {
        if (editor == null) {
            return editor = preferences.edit();
        }
        return editor;
    }

    public void commit() {
        if (editor != null) {
            editor.commit();
            editor = null;
        }
    }

    public int getSyncInterval() {
        return preferences.getInt(SYNC_INTERVAL, DEFAULT_SYNC_INTERVAL);
    }

    public AlarmPreference setSyncInterval(int syncInterval) {
        getEditor().putInt(SYNC_INTERVAL, DEFAULT_SYNC_INTERVAL);
        return this;
    }

    public int getReminderBefore() {
        return preferences.getInt(REMINDER_BEFORE, DEFAULT_REMINDER_BEFORE);
    }

    public AlarmPreference setReminderBefore(int reminderBefore) {
        getEditor().putInt(REMINDER_BEFORE, reminderBefore);
        return  this;
    }

    public int getReminderInterval() {
        return preferences.getInt(REMINDER_INTERVAL, DEFAULT_REMINDER_INTERVAL);
    }

    public AlarmPreference setReminderInterval(int reminderInterval) {
        getEditor().putInt(REMINDER_INTERVAL, reminderInterval);
        return  this;
    }



    public long getLastSync() {
        return preferences.getLong(LAST_SYNC, 0);
    }

    public AlarmPreference setLastSync(long lastSync) {
        getEditor().putLong(LAST_SYNC, lastSync);
        return  this;
    }

    public String getCachedData() {
        return preferences.getString(CACHED_DATA, null);
    }

    public AlarmPreference setCachedData(String data) {
        getEditor().putString(CACHED_DATA, data);
        return this;
    }

    public String getSessionID() {
        return preferences.getString(SESSION_ID, null);
    }

    public AlarmPreference setSessionID(String sessionID) {
        getEditor().putString(SESSION_ID, sessionID);
        return this;
    }

    public boolean isItemRead(int id) {
        return preferences.getBoolean(ITEM_READ + id, false);
    }

    public AlarmPreference setItemRead(int id, boolean read) {
        getEditor().putBoolean(ITEM_READ + id, read);
        return this;
    }

}
