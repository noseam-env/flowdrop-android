/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.jflowdrop;

import java.util.List;

public class SendAsk {
    private final DeviceInfo sender;
    private final List<FileInfo> files;

    public SendAsk(DeviceInfo sender, List<FileInfo> files) {
        this.sender = sender;
        this.files = files;
    }

    public DeviceInfo getSender() {
        return sender;
    }

    public List<FileInfo> getFiles() {
        return files;
    }
}
