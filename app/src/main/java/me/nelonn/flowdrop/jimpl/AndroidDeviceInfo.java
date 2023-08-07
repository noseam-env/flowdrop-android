/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop.jimpl;

import android.os.Build;
import android.provider.Settings;

import org.jetbrains.annotations.NotNull;

import me.nelonn.flowdrop.AppController;
import me.nelonn.jflowdrop.DeviceInfo;
import me.nelonn.knotandroidinfo.DeviceModelInfo;
import me.nelonn.knotandroidinfo.DeviceModelInfoProvider;

public final class AndroidDeviceInfo {

    public static @NotNull String getPlatform() {
        return "Android";
    }

    public static @NotNull String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    public static @NotNull String getName() {
        return Settings.Global.getString(AppController.getInstance().getContentResolver(), "device_name");
    }

    public static @NotNull String getModel() {
        DeviceModelInfoProvider provider = new DeviceModelInfoProvider(AppController.getInstance());
        DeviceModelInfo modelInfo = provider.provideModelInfo();
        return modelInfo.getManufacturer() + " " + modelInfo.getName();
    }

    public static DeviceInfo create(String id) {
        return new DeviceInfo(id, getName(), getModel(), getPlatform(), getSystemVersion());
    }

    private AndroidDeviceInfo() {
        throw new UnsupportedOperationException();
    }
}