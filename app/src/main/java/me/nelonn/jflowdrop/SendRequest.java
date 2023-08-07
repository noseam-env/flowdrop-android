/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.jflowdrop;

import java.util.List;

public interface SendRequest {

    DeviceInfo getDeviceInfo();

    SendRequest setDeviceInfo(DeviceInfo deviceInfo);

    String getReceiverId();

    SendRequest setReceiverId(String id);

    List<File> getFiles();

    SendRequest setFiles(List<File> files);

    /**
     * Resolve timeout in milliseconds
     * @return milliseconds
     */
    long getResolveTimeout();

    /**
     * Resolve timeout in milliseconds
     * @param timeout in milliseconds
     * @return this
     */
    SendRequest setResolveTimeout(long timeout);

    /**
     * Ask timeout in milliseconds
     * @return milliseconds
     */
    long getAskTimeout();

    /**
     * Ask timeout in milliseconds
     * @param timeout milliseconds
     * @return this
     */
    SendRequest setAskTimeout(long timeout);

    EventListener getEventListener();

    SendRequest setEventListener(EventListener listener);

    boolean execute();

}
