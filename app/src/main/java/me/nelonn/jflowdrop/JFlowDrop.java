/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.jflowdrop;

import me.nelonn.jflowdrop.impl.NativeJFlowDrop;

public interface JFlowDrop {

    static JFlowDrop getInstance() {
        return NativeJFlowDrop.INSTANCE;
    }

    String generateMD5Id();

    void initDNSSD(DNSSD dnssd);

    Server createServer(DeviceInfo deviceInfo);

    void discover(DiscoverCallback callback, IsStoppedFunction isStoppedFunction);

    SendRequest createSendRequest();

}
