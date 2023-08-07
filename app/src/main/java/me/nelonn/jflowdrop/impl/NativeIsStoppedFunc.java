/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.jflowdrop.impl;

import me.nelonn.jflowdrop.IsStoppedFunction;

public class NativeIsStoppedFunc implements IsStoppedFunction {
    private final long ptr;

    public NativeIsStoppedFunc(long ptr) {
        this.ptr = ptr;
    }

    private native boolean _isStopped(long ptr);

    @Override
    public boolean isStopped() {
        return _isStopped(ptr);
    }
}
