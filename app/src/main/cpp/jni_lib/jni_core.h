/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */
#pragma once

#include <jni.h>
#include <iostream>
#include <string>

std::string sig(const std::initializer_list<std::string> &, const std::string &);

std::string csig(const std::string &);

bool isGlobal(JNIEnv* env, jobject);

std::string java2String(JNIEnv* env, jstring jStr);

jstring string2Java(JNIEnv *env, const std::string &str);

jobject jGlobal(JNIEnv *env, jobject obj);

jclass jGlobal(JNIEnv *env, jclass clazz);

static const char *Object_sig = "Ljava/lang/Object;";
static const char *String_sig = "Ljava/lang/String;";
static const char *Optional_sig = "Ljava/util/Optional;";

static const char *boolean_sig = "Z";
static const char *byte_sig = "B";
static const char *char_sig = "C";
static const char *short_sig = "S";
static const char *int_sig = "I";
static const char *long_sig = "J";
static const char *float_sig = "F";
static const char *double_sig = "D";

enum AttachResult {
    FAILED,
    ALREADY,
    ATTACHED
};

struct JavaThread {
    JNIEnv *env;
    AttachResult attachResult;
};

JavaThread attachJavaVM();

void detachJavaVM(JavaThread thread);

AttachResult oldAttach(JNIEnv** p_env);

void oldDetach(AttachResult result);

class JIObject {
public:
    explicit JIObject(jobject handle);
    ~JIObject();

    jobject getHandle();

private:
    class Impl;
    std::unique_ptr<Impl> pImpl;
};

class JIClass {
public:
    explicit JIClass(jclass handle);
    ~JIClass();

    JIObject *newInstance(JNIEnv *env, const std::string &sig, ...);

    jclass getHandle();

private:
    class Impl;
    std::unique_ptr<Impl> pImpl;
};

class JICore {
public:
    explicit JICore(JavaVM *javaVm);
    ~JICore();

    JavaVM *getJavaVM();

    JIClass *require(JNIEnv *env, const std::string &className);

    // before import you need to require class in JNI_OnLoad
    JIClass *import(const std::string &className);

private:
    class Impl;
    std::unique_ptr<Impl> pImpl;
};

extern JICore* jiCore;
