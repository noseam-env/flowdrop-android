/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.jflowdrop.impl;

public class NativeUtil {
    public static void ptrValidate(long ptr) {
        if (ptr == 0) throw new RuntimeException("The class you called is deallocated");
    }
}
