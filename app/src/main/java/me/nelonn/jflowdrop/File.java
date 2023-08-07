/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.jflowdrop;

public interface File {

    String getRelativePath();

    long getSize();

    // unix
    long getCreatedTime();

    // unix
    long getModifiedTime();

    //long seek(long pos);

    long read(byte[] buffer, long count);

}
