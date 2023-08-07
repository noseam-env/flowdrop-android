/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.jflowdrop;

public interface EventListener {

    // sender
    default void onResolving() {}
    default void onReceiverNotFound() {}
    default void onResolved() {}
    default void onAskingReceiver() {}
    default void onReceiverDeclined() {}
    default void onReceiverAccepted() {}
    default void onSendingStart() {}
    default void onSendingTotalProgress(long totalSize, long currentSize) {}
    default void onSendingFileStart(FileInfo fileInfo) {}
    default void onSendingFileProgress(FileInfo fileInfo, long currentSize) {}
    default void onSendingFileEnd(FileInfo fileInfo) {}
    default void onSendingEnd() {}

    // receiver
    default void onReceiverStarted(int port) {}
    default void onSenderAsk(DeviceInfo sender) {}
    default void onReceivingStart(DeviceInfo sender, long totalSize) {}
    default void onReceivingTotalProgress(DeviceInfo sender, long totalSize, long receivedSize) {}
    default void onReceivingFileStart(DeviceInfo sender, FileInfo fileInfo) {}
    default void onReceivingFileProgress(DeviceInfo sender, FileInfo fileInfo, long receivedSize) {}
    default void onReceivingFileEnd(DeviceInfo sender, FileInfo fileInfo) {}
    default void onReceivingEnd(DeviceInfo sender, long totalSize) {}
    
}
