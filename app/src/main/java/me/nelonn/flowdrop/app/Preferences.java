/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import androidx.annotation.NonNull;

import me.nelonn.flowdrop.ui.service.ServerForegroundService;

public class Preferences {
    public static final String NAME = "flowdrop";
    public static final String INSTRUCTIONS_VIEWED = "instructions_viewed";
    public static final String RECEIVE_IN_BACKGROUND = "receive_in_background";
    public static final long WAIT_FOR_ACCEPT_MILLIS = 30 * 1000;

    public static void setReceiveInBackground(@NonNull Context context, boolean enabled) {
        SharedPreferences preferences = context.getSharedPreferences(Preferences.NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        boolean isServiceRunning = Util.isServiceRunning(context, ServerForegroundService.class);
        Intent serviceIntent = new Intent(context, ServerForegroundService.class);
        if (enabled) {
            editor.putBoolean(Preferences.RECEIVE_IN_BACKGROUND, true);
            if (!isServiceRunning) {
                context.startService(serviceIntent);
            }
        } else {
            editor.putBoolean(Preferences.RECEIVE_IN_BACKGROUND, false);
            if (isServiceRunning) {
                context.stopService(serviceIntent);
            }
        }
        editor.apply();
    }

    public static String getDestinationDirectory(@NonNull Context context) {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    private Preferences() {
        throw new UnsupportedOperationException();
    }
}
