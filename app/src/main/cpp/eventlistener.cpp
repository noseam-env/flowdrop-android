/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

#include "java.h"
#include "core.h"
#include "eventlistener.h"

using namespace Java::me::nelonn;

JavaEventListener::JavaEventListener(JIObject *instance) : _instance(instance) {}
JavaEventListener::~JavaEventListener() = default;

// server
void JavaEventListener::onResolving() {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onResolving", sig({}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method);

    oldDetach(attachResult);
}
void JavaEventListener::onReceiverNotFound() {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onReceiverNotFound",
                                        sig({}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method);

    oldDetach(attachResult);
}
void JavaEventListener::onResolved() {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onResolved", sig({}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method);

    oldDetach(attachResult);
}
void JavaEventListener::onAskingReceiver() {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onAskingReceiver",
                                        sig({}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method);

    oldDetach(attachResult);
}
void JavaEventListener::onReceiverDeclined() {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onReceiverDeclined",
                                        sig({}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method);

    oldDetach(attachResult);
}
void JavaEventListener::onReceiverAccepted() {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onReceiverAccepted",
                                        sig({}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method);

    oldDetach(attachResult);
}
void JavaEventListener::onSendingStart() {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onSendingStart",
                                        sig({}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method);

    oldDetach(attachResult);
}
void JavaEventListener::onSendingTotalProgress(std::uint64_t totalSize, std::uint64_t currentSize) {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onSendingTotalProgress",
                                        sig({"J", "J"}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method, static_cast<jlong>(totalSize), static_cast<jlong>(currentSize));

    oldDetach(attachResult);
}
void JavaEventListener::onSendingFileStart(const flowdrop::FileInfo &fileInfo) {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onSendingFileStart",
                                        sig({csig(jflowdrop::FileInfo::classs().getName())}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method, convert_FileInfo(env, fileInfo));

    oldDetach(attachResult);
}
void JavaEventListener::onSendingFileProgress(const flowdrop::FileInfo &fileInfo, std::uint64_t currentSize) {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onSendingFileProgress",
                                        sig({csig(jflowdrop::FileInfo::classs().getName()), "J"}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method, convert_FileInfo(env, fileInfo), static_cast<jlong>(currentSize));

    oldDetach(attachResult);
}
void JavaEventListener::onSendingFileEnd(const flowdrop::FileInfo &fileInfo) {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onSendingFileEnd",
                                        sig({csig(jflowdrop::FileInfo::classs().getName())}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method, convert_FileInfo(env, fileInfo));

    oldDetach(attachResult);
}
void JavaEventListener::onSendingEnd() {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onSendingEnd", sig({}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method);

    oldDetach(attachResult);
}

// receiver
void JavaEventListener::onReceiverStarted(unsigned short port) {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onReceiverStarted",
                                        sig({"I"}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method, static_cast<jint>(port));

    oldDetach(attachResult);
}
void JavaEventListener::onSenderAsk(const flowdrop::DeviceInfo &sender) {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onSenderAsk",
                                        sig({csig(jflowdrop::DeviceInfo::classs().getName())}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method, convert_DeviceInfo(env, sender));

    oldDetach(attachResult);
}
void JavaEventListener::onReceivingStart(const flowdrop::DeviceInfo &sender, std::uint64_t totalSize) {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onReceivingStart",
                                        sig({csig(jflowdrop::DeviceInfo::classs().getName()), "J"}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method, convert_DeviceInfo(env, sender), static_cast<jlong>(totalSize));

    oldDetach(attachResult);
}
void JavaEventListener::onReceivingTotalProgress(const flowdrop::DeviceInfo &sender, std::uint64_t totalSize, std::uint64_t receivedSize) {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onReceivingTotalProgress",
                                        sig({csig(jflowdrop::DeviceInfo::classs().getName()), "J", "J"}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method, convert_DeviceInfo(env, sender), static_cast<jlong>(totalSize), static_cast<jlong>(receivedSize));

    oldDetach(attachResult);
}
void JavaEventListener::onReceivingFileStart(const flowdrop::DeviceInfo &sender, const flowdrop::FileInfo &fileInfo) {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onReceivingFileStart",
                                        sig({csig(jflowdrop::DeviceInfo::classs().getName()), csig(jflowdrop::FileInfo::classs().getName())}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method, convert_DeviceInfo(env, sender), convert_FileInfo(env, fileInfo));

    oldDetach(attachResult);
}
void JavaEventListener::onReceivingFileProgress(const flowdrop::DeviceInfo &sender, const flowdrop::FileInfo &fileInfo, std::uint64_t receivedSize) {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onReceivingFileProgress",
                                        sig({csig(jflowdrop::DeviceInfo::classs().getName()), csig(jflowdrop::FileInfo::classs().getName()), "J"}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method, convert_DeviceInfo(env, sender), convert_FileInfo(env, fileInfo), static_cast<jlong>(receivedSize));

    oldDetach(attachResult);
}
void JavaEventListener::onReceivingFileEnd(const flowdrop::DeviceInfo &sender, const flowdrop::FileInfo &fileInfo) {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onReceivingFileEnd",
                                        sig({csig(jflowdrop::DeviceInfo::classs().getName()), csig(jflowdrop::FileInfo::classs().getName())}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method, convert_DeviceInfo(env, sender), convert_FileInfo(env, fileInfo));

    oldDetach(attachResult);
}
void JavaEventListener::onReceivingEnd(const flowdrop::DeviceInfo &sender, std::uint64_t totalSize, const std::vector<flowdrop::FileInfo> &receivedFiles) {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    jmethodID method = env->GetMethodID(jflowdrop::EventListener::classs()._getHandle()->getHandle(), "onReceivingEnd",
                                        sig({csig(jflowdrop::DeviceInfo::classs().getName()), "J"}, "V").c_str());
    env->CallVoidMethod(_instance->getHandle(), method, convert_DeviceInfo(env, sender), static_cast<jlong>(totalSize));

    oldDetach(attachResult);
}
