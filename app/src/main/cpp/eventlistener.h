/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */
#pragma once

#include "main.h"
#include "flowdrop/flowdrop.hpp"

class JavaEventListener : public flowdrop::IEventListener {
public:
    JavaEventListener(JIObject *instance);
    ~JavaEventListener();

    // sender
    void onResolving() override;
    void onReceiverNotFound() override;
    void onResolved() override;
    void onAskingReceiver() override;
    void onReceiverDeclined() override;
    void onReceiverAccepted() override;
    void onSendingStart() override;
    void onSendingTotalProgress(std::uint64_t totalSize, std::uint64_t currentSize) override;
    void onSendingFileStart(const flowdrop::FileInfo &fileInfo) override;
    void onSendingFileProgress(const flowdrop::FileInfo &fileInfo, std::uint64_t currentSize) override;
    void onSendingFileEnd(const flowdrop::FileInfo &fileInfo) override;
    void onSendingEnd() override;

    // receiver
    void onReceiverStarted(unsigned short port) override;
    void onSenderAsk(const flowdrop::DeviceInfo &sender) override;
    void onReceivingStart(const flowdrop::DeviceInfo &sender, std::uint64_t totalSize) override;
    void onReceivingTotalProgress(const flowdrop::DeviceInfo &sender, std::uint64_t totalSize, std::uint64_t receivedSize) override;
    void onReceivingFileStart(const flowdrop::DeviceInfo &sender, const flowdrop::FileInfo &fileInfo) override;
    void onReceivingFileProgress(const flowdrop::DeviceInfo &sender, const flowdrop::FileInfo &fileInfo, std::uint64_t receivedSize) override;
    void onReceivingFileEnd(const flowdrop::DeviceInfo &sender, const flowdrop::FileInfo &fileInfo) override;
    void onReceivingEnd(const flowdrop::DeviceInfo &sender, std::uint64_t totalSize, const std::vector<flowdrop::FileInfo> &receivedFiles) override;

private:
    JIObject *_instance;
};
