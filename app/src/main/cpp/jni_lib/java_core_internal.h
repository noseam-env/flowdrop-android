/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

#include "java_core.h"

void InitJavaCore(JNIEnv *env);

Java::Object *getStaticFieldValue(JNIEnv *env, Java::Classs classs, const std::string& name);
