/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package org.organizzy.client.android;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.Toast;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaActivity;
import org.organizzy.client.android.alarm.AlarmPreference;
import org.organizzy.client.android.alarm.AlarmService;

import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends CordovaActivity
{
    public static final String STARTUP_PAGE = "page";

    /**
     * page to be open when activity start
     * set in {@link #receiveIntent(android.content.Intent)}
     */
    public String startPage = null;

    /**
     * current application version
     */
    private String version = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.init();

        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "1.0";
        }

        WebSettings w = this.appView.getSettings();
        String userAgent = String.format(" OrganizzyMobile/%s (lang=%s) (tz=%s)",
                version, Locale.getDefault().getLanguage(), TimeZone.getDefault().getID());
        w.setUserAgentString(w.getUserAgentString() + userAgent);
        w.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        super.appView.addJavascriptInterface(new JavascriptObject(this), "_organizzy");
        super.loadUrl(Config.getStartUrl());
        receiveIntent(getIntent());

        AlarmService.registerService(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        receiveIntent(intent);
        if (startPage != null)
            appView.sendJavascript("O.navigation.changePage('" + startPage + "')");
    }

    private void receiveIntent(Intent intent) {
        if (intent.hasExtra(STARTUP_PAGE)) {
            startPage = intent.getStringExtra(STARTUP_PAGE);
        } else {
            startPage = null;
        }
    }

    public class JavascriptObject
    {
        public static final String SID = "sid";

        private MainActivity mContext;

        public JavascriptObject(MainActivity context) {
            mContext = context;
        }

        @JavascriptInterface
        public String getVersion() {
            return version;
        }

        @JavascriptInterface
        public String getBaseServer() {return getString(R.string.server);}

        @JavascriptInterface
        public String getStartUpPage() {
            return startPage;
        }

        @JavascriptInterface
        public void login(String sid) {
            AlarmService.registerService(mContext, sid);
        }

        @JavascriptInterface
        public void logout() {
            AlarmPreference.getDefault(mContext).removeSessionID().commit();
            AlarmService.unregisterService(mContext);
            NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancelAll();
        }

        @JavascriptInterface
        public String getSid() {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
            return pref.getString(SID, null);
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void showSettingPage() {
            Intent intent = new Intent(mContext, SettingActivity.class);
            mContext.startActivityIfNeeded(intent, 0);
        }
    }

}

