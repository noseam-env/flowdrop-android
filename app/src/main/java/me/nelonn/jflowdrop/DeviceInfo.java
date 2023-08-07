/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.jflowdrop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

public class DeviceInfo {
    private final String id;
    private final String name;
    private final String model;
    private final String platform;
    private final String systemVersion;

    public DeviceInfo(@NonNull String id,
                      @Nullable String name,
                      @Nullable String model,
                      @Nullable String platform,
                      @Nullable String systemVersion) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.platform = platform;
        this.systemVersion = systemVersion;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @NonNull
    public Optional<String> getModel() {
        return Optional.ofNullable(model);
    }

    @NonNull
    public Optional<String> getPlatform() {
        return Optional.ofNullable(platform);
    }

    @NonNull
    public Optional<String> getSystemVersion() {
        return Optional.ofNullable(systemVersion);
    }

    @NonNull
    @Override
    public String toString() {
        return "DeviceInfo{" +
                "id='" + id + '\'' +
                ", name=" + name +
                ", model=" + model +
                ", platform=" + platform +
                ", systemVersion=" + systemVersion +
                '}';
    }
}
