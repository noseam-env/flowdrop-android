/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

#include <jni.h>
#include <sys/endian.h>
#include <future>
#include "java.h"
#include "main.h"
#include "core.h"
#include "knot/dnssd.h"
#include "atomic"
#include "android/log.h"
#include "thread"

using namespace Java::me::nelonn;

extern "C" JNIEXPORT jboolean JNICALL
Java_me_nelonn_jflowdrop_impl_NativeIsStoppedFunc__1isStopped(JNIEnv *env, jobject /* thiz */, jlong ptr) {
    std::function<bool()>& func = *reinterpret_cast<std::function<bool()>*>(ptr);
    return func();
}

jobject isStoppedFunc2Java(JNIEnv *env, const std::function<bool()> &isStopped) {
    jlong ptr = reinterpret_cast<jlong>(&isStopped);
    jobject instance = jflowdrop::impl::NativeIsStoppedFunc::classs()._getHandle()->newInstance(env, sig({"J"}, "V"), ptr)->getHandle();
    return instance;
}

jobject txt2Java(JNIEnv* env, const std::unordered_map<std::string, std::string>& cppMap) {
    jclass HashMap_class = env->FindClass("java/util/HashMap");
    jmethodID hashmapConstructor = env->GetMethodID(HashMap_class, "<init>", "()V");
    jmethodID hashmapPut = env->GetMethodID(HashMap_class, "put", sig({Object_sig, Object_sig}, Object_sig).c_str());

    jclass String_class = env->FindClass("java/lang/String");
    jmethodID stringConstructor = env->GetMethodID(String_class, "<init>", sig({String_sig}, "V").c_str());

    jobject resultHashMap = env->NewObject(HashMap_class, hashmapConstructor);

    for (const auto& pair : cppMap) {
        jstring key = env->NewStringUTF(pair.first.c_str());
        jstring value = env->NewStringUTF(pair.second.c_str());
        env->CallObjectMethod(resultHashMap, hashmapPut, env->NewObject(String_class, stringConstructor, key), env->NewObject(String_class, stringConstructor, value));
        env->DeleteLocalRef(key);
        env->DeleteLocalRef(value);
    }

    return resultHashMap;
}

std::unordered_map<std::string, std::string> java2Txt(JNIEnv* env, jobject jmap) {
    jclass HashMap_class = env->GetObjectClass(jmap);
    jmethodID hashmapEntrySet = env->GetMethodID(HashMap_class, "entrySet", "()Ljava/util/Set;");
    jclass Set_class = env->FindClass("java/util/Set");
    jmethodID setIterator = env->GetMethodID(Set_class, "iterator", "()Ljava/util/Iterator;");
    jclass Iterator_class = env->FindClass("java/util/Iterator");
    jmethodID iteratorHasNext = env->GetMethodID(Iterator_class, "hasNext", "()Z");
    jmethodID iteratorNext = env->GetMethodID(Iterator_class, "next", sig({}, Object_sig).c_str());
    jclass Map$Entry_class = env->FindClass("java/util/Map$Entry");
    jmethodID mapEntryGetKey = env->GetMethodID(Map$Entry_class, "getKey", sig({}, Object_sig).c_str());
    jmethodID mapEntryGetValue = env->GetMethodID(Map$Entry_class, "getValue", sig({}, Object_sig).c_str());

    jobject entrySet = env->CallObjectMethod(jmap, hashmapEntrySet);
    jobject iterator = env->CallObjectMethod(entrySet, setIterator);

    std::unordered_map<std::string, std::string> cppMap;

    while (env->CallBooleanMethod(iterator, iteratorHasNext)) {
        jobject mapEntry = env->CallObjectMethod(iterator, iteratorNext);
        auto jkey = static_cast<jstring>(env->CallObjectMethod(mapEntry, mapEntryGetKey));
        auto jvalue = static_cast<jstring>(env->CallObjectMethod(mapEntry, mapEntryGetValue));
        const char* key = env->GetStringUTFChars(jkey, nullptr);
        const char* value = env->GetStringUTFChars(jvalue, nullptr);
        cppMap[key] = value;
        env->ReleaseStringUTFChars(jkey, key);
        env->ReleaseStringUTFChars(jvalue, value);
    }

    return cppMap;
}

JIObject *DNSSD_Instance = nullptr;

void registerService(const char *serviceName, const char *regType, const char *domain, unsigned short port, const std::unordered_map<std::string, std::string>& txt, const std::function<bool()> &isStopped) {
    JNIEnv* env;
    AttachResult attachResult = oldAttach(&env);
    if (attachResult == FAILED) return;

    //jmethodID method = env->GetMethodID(jflowdrop::DNSSD::classs()._getHandle()->getHandle(), "registerService", sig({String_sig, String_sig, String_sig, "I", csig("java/util/Map"), csig(jflowdrop::IsStoppedFunction::classs().getName())}, "V").c_str());
    jmethodID method = env->GetMethodID(jflowdrop::DNSSD::classs()._getHandle()->getHandle(), "registerService", sig({String_sig, String_sig, String_sig, "I", csig("java/util/Map"), csig(jflowdrop::IsStoppedFunction::classs().getName())}, "V").c_str());
    env->CallVoidMethod(DNSSD_Instance->getHandle(), method,
                        string2Java(env, serviceName),
                        string2Java(env, regType),
                        string2Java(env, domain),
                        reinterpret_cast<jint>(static_cast<int>(port)),
                        txt2Java(env, txt),
                        isStoppedFunc2Java(env, isStopped));

    oldDetach(attachResult);
}

void findService(const char *regType, const char *domain, const findCallback &callback, const std::function<bool()> &isStopped) {
    JavaThread thread = attachJavaVM();

    jobject jcallback = jGlobal(thread.env, jflowdrop::impl::NativeDiscoverCallback::classs()._getHandle()->newInstance(thread.env, sig({"J"}, "V"), reinterpret_cast<jlong>(&callback))->getHandle());

    jmethodID method = thread.env->GetMethodID(jflowdrop::DNSSD::classs()._getHandle()->getHandle(), "discoverServices", sig({String_sig, String_sig, csig("java/util/function/Consumer"), csig(jflowdrop::IsStoppedFunction::classs().getName())}, "V").c_str());
    thread.env->CallVoidMethod(DNSSD_Instance->getHandle(), method,
                        string2Java(thread.env, regType), string2Java(thread.env, domain), jcallback, isStoppedFunc2Java(thread.env, isStopped));

    detachJavaVM(thread);
}

void resolveService(const char *serviceName, const char *regType, const char *domain, const resolveCallback &callback) {
    JavaThread thread = attachJavaVM();

    jlong ptr = reinterpret_cast<jlong>(new resolveCallback(callback)); // wtf idk why this needed

    //jobject jcallback = jGlobal(thread.env, jflowdrop::impl::NativeResolveCallback::classs()._getHandle()->newInstance(thread.env, sig({"J"}, "V"), reinterpret_cast<jlong>(&callback))->getHandle());
    jobject jcallback = jGlobal(thread.env, jflowdrop::impl::NativeResolveCallback::classs()._getHandle()->newInstance(thread.env, sig({"J"}, "V"), ptr)->getHandle());

    jmethodID method = thread.env->GetMethodID(jflowdrop::DNSSD::classs()._getHandle()->getHandle(), "resolveService", sig({String_sig, String_sig, String_sig, csig("java/util/function/Consumer")}, "V").c_str());
    thread.env->CallVoidMethod(DNSSD_Instance->getHandle(), method,
                               string2Java(thread.env, serviceName), string2Java(thread.env, regType), string2Java(thread.env, domain), jcallback);

    detachJavaVM(thread);
}

extern "C" JNIEXPORT void JNICALL
Java_me_nelonn_jflowdrop_impl_NativeJFlowDrop_initDNSSD(JNIEnv *env, jobject /* clazz */, jobject dnssd) {
    DNSSD_Instance = new JIObject(jGlobal(env, dnssd));
}

extern "C" JNIEXPORT void JNICALL
Java_me_nelonn_jflowdrop_impl_NativeDiscoverCallback__1accept(JNIEnv *env, jobject /* thiz */,
                                                              jlong ptr, jobject find_reply) {
    findCallback &callback = *reinterpret_cast<findCallback *>(ptr);
    jclass clazz = jflowdrop::DNSSD::DiscoverReply::classs()._getHandle()->getHandle();

    jfieldID field_serviceName = env->GetFieldID(clazz, "serviceName", String_sig);
    jfieldID field_regType = env->GetFieldID(clazz, "regType", String_sig);
    jfieldID field_replyDomain = env->GetFieldID(clazz, "replyDomain", String_sig);

    std::string serviceName = GetStringFromField(env, find_reply, field_serviceName);
    std::string regType = GetStringFromField(env, find_reply, field_regType);
    std::string replyDomain = GetStringFromField(env, find_reply, field_replyDomain);

    callback({serviceName.c_str(), regType.c_str(), replyDomain.c_str()});
}

// wtf idk why this needed
resolveCallback jlongToStdFunction(jlong callbackPtr) {
    auto callback = reinterpret_cast<resolveCallback *>(callbackPtr);
    resolveCallback result = *callback;
    delete callback; // probably this is potential bug
    return result;
}

extern "C" JNIEXPORT void JNICALL
Java_me_nelonn_jflowdrop_impl_NativeResolveCallback__1accept(JNIEnv *env, jobject /* thiz */,
                                                             jlong ptr, jobject resolve_reply) {
    //resolveCallback &callback = *reinterpret_cast<resolveCallback *>(ptr);
    resolveCallback callback = jlongToStdFunction(ptr); // wtf idk why this needed
    jclass clazz = jflowdrop::DNSSD::ResolveReply::classs()._getHandle()->getHandle();

    jfieldID field_hostName = env->GetFieldID(clazz, "hostName", String_sig);
    jfieldID field_ip = env->GetFieldID(clazz, "ip", csig(jflowdrop::DNSSD::IPAddress::classs().getName()).c_str());
    jfieldID field_port = env->GetFieldID(clazz, "port", "I");
    jfieldID field_txt = env->GetFieldID(clazz, "txt", csig("java/util/Map").c_str());

    std::string hostName = GetStringFromField(env, resolve_reply, field_hostName);
    std::optional<std::string> hostNameOpt = !hostName.empty() ? std::optional<std::string>(hostName) : std::nullopt;

    std::optional<IPAddress> ipOpt;
    auto IPAddress_instance = env->GetObjectField(resolve_reply, field_ip);
    if (IPAddress_instance != nullptr) {
        jfieldID IPAddress_field_type = env->GetFieldID(jflowdrop::DNSSD::IPAddress::classs()._getHandle()->getHandle(), "type", csig(jflowdrop::DNSSD::IPType::classs().getName()).c_str());
        jfieldID IPAddress_field_value = env->GetFieldID(jflowdrop::DNSSD::IPAddress::classs()._getHandle()->getHandle(), "value", String_sig);
        auto IPType_instance = env->GetObjectField(IPAddress_instance, IPAddress_field_type);
        IPType type = env->IsSameObject(IPType_instance, jflowdrop::DNSSD::IPType::IPv6->_getHandle()) ? IPv6 : IPv4;
        std::string ipstring = GetStringFromField(env, IPAddress_instance, IPAddress_field_value);
        ipOpt = {type, ipstring};
    } else {
        ipOpt = std::nullopt;
    }

    jint jint_port = env->GetIntField(resolve_reply, field_port);
    auto port = static_cast<unsigned short>(reinterpret_cast<int>(jint_port));

    jobject txtObj = env->GetObjectField(resolve_reply, field_txt);
    std::unordered_map<std::string, std::string> txt = java2Txt(env, txtObj);

    callback({{hostNameOpt, ipOpt, port, txt}});
}

/*class FunctionHolder {
public:
    void setFunction(queryCallback func) {
        std::lock_guard<std::mutex> lock(mutex_);
        function_ = std::move(func);
    }

    void callFunction(const std::optional<IPAddress> &arg) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (function_) {
            function_(arg);
        }
    }

    jlong getFunctionPointer() {
        std::lock_guard<std::mutex> lock(mutex_);
        return reinterpret_cast<jlong>(&function_);
    }

private:
    queryCallback function_;
    std::mutex mutex_;
};*/

void queryIPv6Address(const char *hostName, const queryCallback &callback) {
    // Not needed.
    // Android does not support IPv6
}

void queryIPv4Address(const char *hostName, const queryCallback &callback) {
    JavaThread thread = attachJavaVM();

    jobject jcallback = jGlobal(thread.env, jflowdrop::impl::NativeQueryCallback::classs()._getHandle()->newInstance(thread.env, sig({"J"}, "V"), reinterpret_cast<jlong>(new queryCallback(callback)))->getHandle());
    //jobject jcallback = jGlobal(thread.env, jflowdrop::impl::NativeQueryCallback::classs()._getHandle()->newInstance(thread.env, sig({"J"}, "V"), reinterpret_cast<jlong>(functionHolder))->getHandle());

    jmethodID method = thread.env->GetMethodID(jflowdrop::DNSSD::classs()._getHandle()->getHandle(), "queryIPv4Address", sig({String_sig, csig("java/util/function/Consumer")}, "V").c_str());
    thread.env->CallVoidMethod(DNSSD_Instance->getHandle(), method, string2Java(thread.env, hostName), jcallback);

    detachJavaVM(thread);
}

queryCallback jlongToStdFunction2(jlong callbackPtr) {
    auto callback = reinterpret_cast<queryCallback *>(callbackPtr);
    queryCallback result = *callback;
    delete callback; // probably this is potential bug
    return result;
}

extern "C" JNIEXPORT void JNICALL
Java_me_nelonn_jflowdrop_impl_NativeQueryCallback__1accept(JNIEnv *env, jobject /* thiz */, jlong ptr,
                                                           jstring j_ip) {
    std::string ip = java2String(env, j_ip);
    queryCallback callback = jlongToStdFunction2(ptr);
    callback({{IPv4, ip}});
    /*FunctionHolder *functionHolder = reinterpret_cast<FunctionHolder *>(ptr);
    functionHolder->callFunction({{IPv4, ip}});*/
}