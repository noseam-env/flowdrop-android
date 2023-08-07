/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.jflowdrop.impl;

import java.io.IOException;

import me.nelonn.jflowdrop.DeviceInfo;
import me.nelonn.jflowdrop.EventListener;
import me.nelonn.jflowdrop.Server;
import me.nelonn.jflowdrop.SendAskCallback;

public class NativeServer implements Server {
    private long ptr;

    private native long _create_Server(DeviceInfo deviceInfo);
    private native void _release_Server(long ptr);

    public NativeServer(DeviceInfo deviceInfo) {
        ptr = _create_Server(deviceInfo);
    }

    @Override
    public void close() throws IOException {
        stop();

        _release_Server(ptr);
        ptr = 0;
    }

    private native DeviceInfo _getDeviceInfo(long ptr);

    @Override
    public DeviceInfo getDeviceInfo() {
        NativeUtil.ptrValidate(ptr);
        return _getDeviceInfo(ptr);
    }

    private native String _getDestDir(long ptr);

    @Override
    public String getDestDir() {
        NativeUtil.ptrValidate(ptr);
        return _getDestDir(ptr);
    }

    private native void _setDestDir(long ptr, String destDir);

    @Override
    public void setDestDir(String destDir) {
        NativeUtil.ptrValidate(ptr);
        _setDestDir(ptr, destDir);
    }

    private native void _setAskCallback(long ptr, SendAskCallback callback);

    @Override
    public void setAskCallback(SendAskCallback callback) {
        NativeUtil.ptrValidate(ptr);
        _setAskCallback(ptr, callback);
    }

    private native void _setEventListener(long ptr, EventListener listener);

    @Override
    public void setEventListener(EventListener listener) {
        NativeUtil.ptrValidate(ptr);
        _setEventListener(ptr, listener);
    }

    private native void _run(long ptr);

    @Override
    public void run() {
        NativeUtil.ptrValidate(ptr);
        _run(ptr);
    }

    private native void _stop(long ptr);

    @Override
    public void stop() {
        NativeUtil.ptrValidate(ptr);
        _stop(ptr);
    }
}
