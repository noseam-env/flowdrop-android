/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

#include <jni.h>
#include "java.h"
#include "main.h"
#include "core.h"
#include "eventlistener.h"

using namespace Java::me::nelonn;

extern "C" JNIEXPORT jlong JNICALL
Java_me_nelonn_jflowdrop_impl_NativeServer__1create_1Server(JNIEnv *env, jobject /* thiz */, jobject deviceInfoObj) {
    auto *server = new flowdrop::Server(convert_DeviceInfo(env, deviceInfoObj));
    return reinterpret_cast<jlong>(server);
}

extern "C" JNIEXPORT void JNICALL
Java_me_nelonn_jflowdrop_impl_NativeServer__1release_1Server(JNIEnv *env, jobject /* thiz */, jlong ptr) {
    auto *server = reinterpret_cast<flowdrop::Server*>(ptr);
    delete server;
}

extern "C" JNIEXPORT jobject JNICALL
Java_me_nelonn_jflowdrop_impl_NativeServer__1getDeviceInfo(JNIEnv *env, jobject /* thiz */, jlong ptr) {
    auto *server = reinterpret_cast<flowdrop::Server*>(ptr);
    return convert_DeviceInfo(env, server->getDeviceInfo());
}

extern "C" JNIEXPORT jstring JNICALL
Java_me_nelonn_jflowdrop_impl_NativeServer__1getDestDir(JNIEnv *env, jobject /* thiz */, jlong ptr) {
    auto *server = reinterpret_cast<flowdrop::Server*>(ptr);
    return string2Java(env, server->getDestDir());
}

extern "C" JNIEXPORT void JNICALL
Java_me_nelonn_jflowdrop_impl_NativeServer__1setAskCallback(JNIEnv *env, jobject /* thiz */, jlong ptr, jobject callback) {
    auto *server = reinterpret_cast<flowdrop::Server*>(ptr);
    jobject SendAskCallback_instance = jGlobal(env, callback);
    server->setAskCallback([SendAskCallback_instance](const flowdrop::SendAsk &sendAsk){
        JNIEnv* env;
        AttachResult attachResult = oldAttach(&env);
        if (attachResult == FAILED) return false;

        JIClass ArrayList_class(env->FindClass("java/util/ArrayList"));

        jmethodID ArrayList_method_add = env->GetMethodID(ArrayList_class.getHandle(), "add", sig({Object_sig}, "Z").c_str());

        jobject ArrayList_instance = ArrayList_class.newInstance(env, sig({}, "V"))->getHandle();
        for (const flowdrop::FileInfo& file : sendAsk.files) {
            jobject FileInfo_instance = convert_FileInfo(env, file);
            env->CallBooleanMethod(ArrayList_instance, ArrayList_method_add, FileInfo_instance);
            env->DeleteLocalRef(FileInfo_instance);
        }

        jobject SendAsk_instance = jflowdrop::SendAsk::classs()._getHandle()->newInstance(env, sig({csig(jflowdrop::DeviceInfo::classs().getName()), csig("java/util/List")}, "V"),
                                                            convert_DeviceInfo(env, sendAsk.sender), ArrayList_instance)->getHandle();

        jmethodID SendAskCallback_method_onAsk = env->GetMethodID(jflowdrop::SendAskCallback::classs()._getHandle()->getHandle(), "onAsk", sig({csig(jflowdrop::SendAsk::classs().getName())}, "Z").c_str());

        bool result = env->CallBooleanMethod(SendAskCallback_instance, SendAskCallback_method_onAsk, SendAsk_instance);

        oldDetach(attachResult);

        return result;
    });
}

extern "C" JNIEXPORT void JNICALL
Java_me_nelonn_jflowdrop_impl_NativeServer__1setDestDir(JNIEnv *env, jobject /* thiz */, jlong ptr, jstring destDir) {
    auto *server = reinterpret_cast<flowdrop::Server*>(ptr);
    server->setDestDir(java2String(env, destDir));
}

extern "C" JNIEXPORT void JNICALL
Java_me_nelonn_jflowdrop_impl_NativeServer__1setEventListener(JNIEnv *env, jobject /* thiz */, jlong ptr, jobject listener) {
    auto *server = reinterpret_cast<flowdrop::Server*>(ptr);
    jobject EventListener_instance = jGlobal(env, listener);
    server->setEventListener(new JavaEventListener(new JIObject(EventListener_instance)));
}

extern "C" JNIEXPORT void JNICALL
Java_me_nelonn_jflowdrop_impl_NativeServer__1run(JNIEnv *env, jobject /* thiz */, jlong ptr) {
    auto *server = reinterpret_cast<flowdrop::Server*>(ptr);
    server->run();
}

extern "C" JNIEXPORT void JNICALL
Java_me_nelonn_jflowdrop_impl_NativeServer__1stop(JNIEnv *env, jobject /* thiz */, jlong ptr) {
    auto *server = reinterpret_cast<flowdrop::Server*>(ptr);
    server->stop();
}

