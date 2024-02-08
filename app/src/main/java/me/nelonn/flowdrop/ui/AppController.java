/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop.ui;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import me.nelonn.flowdrop.app.BatteryOptimizationMode;
import me.nelonn.flowdrop.app.Preferences;
import me.nelonn.flowdrop.app.Util;
import me.nelonn.flowdrop.app.background.BackgroundServerService;
import me.nelonn.flowdrop.app.jimpl.AndroidDeviceInfo;
import me.nelonn.flowdrop.app.jimpl.DNSSDFactory;
import me.nelonn.flowdrop.app.jimpl.NsdDNSSD;
import me.nelonn.jflowdrop.DeviceInfo;
import me.nelonn.jflowdrop.JFlowDrop;

public class AppController extends Application {
    private static AppController appController;

    public static AppController getInstance() {
        return appController;
    }

    private DeviceInfo deviceInfo;
    private DNSSDFactory dnssdFactory;

    @Override
    public void onCreate() {
        super.onCreate();

        appController = this;

        deviceInfo = AndroidDeviceInfo.create(JFlowDrop.getInstance().generateMD5Id());

        dnssdFactory = NsdDNSSD.FACTORY;
        JFlowDrop.getInstance().initDNSSD(dnssdFactory.create(this));

        if (BatteryOptimizationMode.get(this) == BatteryOptimizationMode.Unrestricted) {
            SharedPreferences preferences = getSharedPreferences(Preferences.NAME, Context.MODE_PRIVATE);
            boolean receiveInBackground = preferences.getBoolean(Preferences.RECEIVE_IN_BACKGROUND, false);
            if (receiveInBackground && !Util.isServiceRunning(this, BackgroundServerService.class)) {
                startService(new Intent(this, BackgroundServerService.class));
            }
        }
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }
}
