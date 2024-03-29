/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.NonNull;

public enum BatteryOptimizationMode {
    Unknown,
    Unrestricted,
    Optimized,
    Restricted;

    public boolean shouldChange() {
        return this == Optimized || this == Restricted;
    }

    @SuppressLint("QueryPermissionsNeeded")
    public static BatteryOptimizationMode get(@NonNull Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return BatteryOptimizationMode.Unknown;
        }
        String packageName = context.getPackageName();
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager.isIgnoringBatteryOptimizations(packageName)) {
            return BatteryOptimizationMode.Unrestricted;
        }
        if (getDisableIntent(context).resolveActivity(context.getPackageManager()) != null) {
            return BatteryOptimizationMode.Restricted;
        } else {
            return BatteryOptimizationMode.Optimized;
        }
    }

    @SuppressLint("BatteryLife")
    public static Intent getDisableIntent(@NonNull Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        return intent;
    }
}
