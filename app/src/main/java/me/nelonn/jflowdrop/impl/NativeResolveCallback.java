/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.jflowdrop.impl;

import java.util.function.Consumer;

import me.nelonn.jflowdrop.DNSSD;

public class NativeResolveCallback implements Consumer<DNSSD.ResolveReply> {
    private final long ptr;

    public NativeResolveCallback(long ptr) {
        this.ptr = ptr;
    }

    private native void _accept(long ptr, DNSSD.ResolveReply resolveReply);

    @Override
    public void accept(DNSSD.ResolveReply resolveReply) {
        _accept(ptr, resolveReply);
    }
}
