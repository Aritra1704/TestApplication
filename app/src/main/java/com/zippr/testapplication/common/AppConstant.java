package com.zippr.testapplication.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.text.TextUtils;
import android.util.Log;

import com.arpaul.utilitieslib.CalendarUtils;
import com.arpaul.utilitieslib.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.zippr.testapplication.BuildConfig.DEBUG;

/**
 * Created by aritrapal on 30/06/17.
 */

public class AppConstant {

    private static final String TAG                         = "AppConstant";

    public static final String EXTERNAL_FOLDER              = "/TestApp/";

    public static final String PUBLIC_KEY                   = "Zs1ePcPrReDt@017";
    public static final String SALT                         = "4348415041204841532041204655434B494E47204C4F414E";
    public static final String INITIAL_VECTOR               = "87657654674399043534359809897544";

    public static final String BUNDLE_LOC                   = "BUNDLE_LOC";

    public static final String INTENT_LOC                   = "INTENT_USER";

    public static final String INTENT_REFRESH_LIST          = "com.zippr.testapplication.common";

    public static final String SEARCH_POST                  = "SEARCH_POST";
    public static final String SEARCH_NEIGHBOUR             = "SEARCH_NEIGHBOUR";

    public static final String POST_CREATED                 = "POST_CREATED";
    public static final String POST_EDITED                  = "POST_EDITED";
    public static final String POST_UPVOTED                 = "POST_UPVOTED";

    public static final String DATE_TIME_FORMAT_UTC         = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_TIME_FORMAT_LOCAL       = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_LOCAL            = "dd MMM yyyy";

    public static final boolean isLOGENABLED                = true;
    public static LogUtils LOG                              = new LogUtils(isLOGENABLED);

    public static final int UPLOAD_DONE                     = 300;
    public static final int UPLOAD_PENDING                  = 301;

    public static final int NO_INTERNET                     = 404;

    public static final long LOCATION_UPDATES_IN_SECONDS    = 60;

    public static final int PERM_READ_PHONE_STATE = 1;
    public static final int PERM_READ_EXTERNAL_STORAGE = 23;
    public static final int PERM_GET_ACCOUNTS = 24;

    public static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static String getProperTime(String utcTime) {
        String indianTime = CalendarUtils.getTimeInTimeZone(utcTime, DATE_TIME_FORMAT_UTC, "UTC", "Asia/Calcutta");
        indianTime = CalendarUtils.getDateinPattern(indianTime, DATE_TIME_FORMAT_UTC, DATE_TIME_FORMAT_LOCAL);
        return indianTime;
    }

    public static void logTime(String log) {
        if(DEBUG) LOG.debugLog(TAG, log + " : " + CalendarUtils.getDateinPattern(DATE_TIME_FORMAT_LOCAL));
    }
}
