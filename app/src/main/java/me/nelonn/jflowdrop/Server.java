/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.jflowdrop;

public interface Server extends AutoCloseable {

    DeviceInfo getDeviceInfo();

    String getDestDir();

    void setDestDir(String destDir);

    void setAskCallback(SendAskCallback callback);

    void setEventListener(EventListener listener);

    void run();

    void stop();

}
