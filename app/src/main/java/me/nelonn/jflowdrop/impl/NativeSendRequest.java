/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.jflowdrop.impl;

import java.util.List;

import me.nelonn.jflowdrop.DeviceInfo;
import me.nelonn.jflowdrop.EventListener;
import me.nelonn.jflowdrop.File;
import me.nelonn.jflowdrop.SendRequest;

public class NativeSendRequest implements SendRequest {
    private DeviceInfo deviceInfo;
    private String receiverId;
    private List<File> files;
    private long resolveTimeout = 1000 * 10;
    private long askTimeout = 1000 * 60;
    private EventListener eventListener;

    @Override
    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    @Override
    public SendRequest setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
        return this;
    }

    @Override
    public String getReceiverId() {
        return receiverId;
    }

    @Override
    public SendRequest setReceiverId(String id) {
        this.receiverId = id;
        return this;
    }

    @Override
    public List<File> getFiles() {
        return files;
    }

    @Override
    public SendRequest setFiles(List<File> files) {
        this.files = files;
        return this;
    }

    @Override
    public long getResolveTimeout() {
        return resolveTimeout;
    }

    @Override
    public SendRequest setResolveTimeout(long timeout) {
        this.resolveTimeout = timeout;
        return this;
    }

    @Override
    public long getAskTimeout() {
        return askTimeout;
    }

    @Override
    public SendRequest setAskTimeout(long timeout) {
        this.askTimeout = timeout;
        return this;
    }

    @Override
    public EventListener getEventListener() {
        return eventListener;
    }

    @Override
    public SendRequest setEventListener(EventListener listener) {
        this.eventListener = listener;
        return this;
    }

    private native boolean _execute(DeviceInfo deviceInfo, String receiverId, List<File> files, long resolveTimeout, long askTimeout, EventListener eventListener);

    @Override
    public boolean execute() {
        return _execute(deviceInfo, receiverId, files, resolveTimeout, askTimeout, eventListener);
    }
}
