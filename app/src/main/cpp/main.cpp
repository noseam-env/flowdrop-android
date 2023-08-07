/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

#include <jni.h>
#include "main.h"
#include "java.h"
#include "java_internal.h"
#include "flowdrop/flowdrop.hpp"

extern "C" JNIEXPORT void JNICALL
Java_me_nelonn_jflowdrop_impl_NativeJFlowDrop_nativeInit(JNIEnv *env, jclass /* clazz */) {
    InitJava(env);
    flowdrop::setDebug(true);
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    jiCore = new JICore(vm);
    return JNI_VERSION_1_6;
}