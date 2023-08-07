/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.jflowdrop.impl;

import java.util.function.Consumer;

public class NativeQueryCallback implements Consumer<String> {
    private final long ptr;

    public NativeQueryCallback(long ptr) {
        this.ptr = ptr;
    }

    private native void _accept(long ptr, String ip);

    @Override
    public void accept(String ip) {
        _accept(ptr, ip);
    }
}
