package org.organizzy.client.android;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

abstract public class Service extends android.app.Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    abstract protected Object execute(Intent intent);

    protected void afterExecute(Object o) {}

    private class PollTask extends AsyncTask<Object, Object, Object> {
        Intent intent;

        @Override
        protected Object doInBackground(Object... intents) {
            return Service.this.execute((Intent) intents[0]);
        }

        @Override
        protected void onPostExecute(Object o) {
            Service.this.afterExecute(o);
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new PollTask().execute(intent);
        return START_NOT_STICKY;
    }
}
