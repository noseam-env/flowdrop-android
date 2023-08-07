/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.jflowdrop.impl;

import me.nelonn.jflowdrop.DNSSD;
import me.nelonn.jflowdrop.DeviceInfo;
import me.nelonn.jflowdrop.DiscoverCallback;
import me.nelonn.jflowdrop.JFlowDrop;
import me.nelonn.jflowdrop.SendRequest;
import me.nelonn.jflowdrop.Server;
import me.nelonn.jflowdrop.IsStoppedFunction;

public class NativeJFlowDrop implements JFlowDrop {
    public static final NativeJFlowDrop INSTANCE = new NativeJFlowDrop();

    private static native void nativeInit();

    static {
        System.loadLibrary("jflowdrop");
        nativeInit();
    }

    private NativeJFlowDrop() {
    }

    @Override
    public native String generateMD5Id();

    @Override
    public native void initDNSSD(DNSSD dnssd);

    @Override
    public Server createServer(DeviceInfo deviceInfo) {
        return new NativeServer(deviceInfo);
    }

    @Override
    public native void discover(DiscoverCallback callback, IsStoppedFunction isStoppedFunction);

    @Override
    public SendRequest createSendRequest() {
        return new NativeSendRequest();
    }
}
