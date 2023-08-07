/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.jflowdrop.impl;

import java.util.function.Consumer;

import me.nelonn.jflowdrop.DNSSD;

public class NativeDiscoverCallback implements Consumer<DNSSD.DiscoverReply> {
    private final long ptr;

    public NativeDiscoverCallback(long ptr) {
        this.ptr = ptr;
    }

    private native void _accept(long ptr, DNSSD.DiscoverReply discoverReply);

    @Override
    public void accept(DNSSD.DiscoverReply discoverReply) {
        _accept(ptr, discoverReply);
    }
}
