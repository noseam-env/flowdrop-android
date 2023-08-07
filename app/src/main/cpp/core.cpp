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

std::string GetStringFromField(JNIEnv *env, jobject obj, jfieldID fieldID) {
    auto jstr = (jstring) env->GetObjectField(obj, fieldID);
    if (jstr == nullptr) {
        return "";
    }
    const char *cstr = env->GetStringUTFChars(jstr, nullptr);
    std::string str(cstr);
    env->ReleaseStringUTFChars(jstr, cstr);
    env->DeleteLocalRef(jstr);
    return str;
}

std::optional<std::string> GetOptionalStringFromField(JNIEnv *env, jobject obj, jfieldID fieldID) {
    jobject optionalObject = env->GetObjectField(obj, fieldID);

    if (optionalObject == nullptr) {
        // Value is absent (Optional is null)
        return std::nullopt;
    }

    jclass optionalClass = env->FindClass("java/util/Optional");
    jmethodID isPresentMethodId = env->GetMethodID(optionalClass, "isPresent", "()Z");
    jboolean isPresent = env->CallBooleanMethod(optionalObject, isPresentMethodId);

    if (isPresent == JNI_FALSE) {
        // Value is absent (Optional is empty)
        return std::nullopt;
    }

    jmethodID getMethodId = env->GetMethodID(optionalClass, "get", sig({}, Object_sig).c_str());
    jobject stringObject = env->CallObjectMethod(optionalObject, getMethodId);

    if (stringObject == nullptr) {
        // Value is absent (String object is null)
        return std::nullopt;
    }

    const char* stringValue = env->GetStringUTFChars(static_cast<jstring>(stringObject), nullptr);
    std::optional<std::string> result = stringValue ? std::optional<std::string>(stringValue) : std::nullopt;
    env->ReleaseStringUTFChars(static_cast<jstring>(stringObject), stringValue);
    return result;
}

jstring optionalString2Java(JNIEnv *env, const std::optional<std::string> &optStr) {
    if (!optStr.has_value()) {
        return nullptr;
    }
    return env->NewStringUTF(optStr.value().c_str());
}

flowdrop::DeviceInfo convert_DeviceInfo(JNIEnv *env, jobject deviceInfoObj) {
    jclass DeviceInfo_class = jflowdrop::DeviceInfo::classs()._getHandle()->getHandle();

    jfieldID idField = env->GetFieldID(DeviceInfo_class, "id", String_sig);
    jfieldID nameField = env->GetFieldID(DeviceInfo_class, "name", String_sig);
    jfieldID modelField = env->GetFieldID(DeviceInfo_class, "model", String_sig);
    jfieldID platformField = env->GetFieldID(DeviceInfo_class, "platform", String_sig);
    jfieldID systemVersionField = env->GetFieldID(DeviceInfo_class, "systemVersion", String_sig);

    flowdrop::DeviceInfo deviceInfo;

    deviceInfo.id = GetStringFromField(env, deviceInfoObj, idField);
    deviceInfo.name = GetStringFromField(env, deviceInfoObj, nameField);
    deviceInfo.model = GetStringFromField(env, deviceInfoObj, modelField);
    deviceInfo.platform = GetStringFromField(env, deviceInfoObj, platformField);
    deviceInfo.system_version = GetStringFromField(env, deviceInfoObj, systemVersionField);

    return deviceInfo;
}

jobject convert_DeviceInfo(JNIEnv *env, const flowdrop::DeviceInfo &deviceInfo) {
    /*jstring id = string2Java(env, deviceInfo.id);
    jstring uuid = optionalString2Java(env, deviceInfo.uuid);
    jstring name = optionalString2Java(env, deviceInfo.name);
    jstring model = optionalString2Java(env, deviceInfo.model);
    jstring platform = optionalString2Java(env, deviceInfo.platform);
    jstring systemVersion = optionalString2Java(env, deviceInfo.system_version);

    JIObject *instance = jflowdrop::DeviceInfo::classs()._getHandle()->newInstance(env,
                                                     sig({String_sig, String_sig, String_sig,
                                                          String_sig, String_sig, String_sig}, "V"),
                                                     id, uuid, name, model, platform, systemVersion);

    env->DeleteLocalRef(id);
    env->DeleteLocalRef(uuid);
    env->DeleteLocalRef(name);
    env->DeleteLocalRef(model);
    env->DeleteLocalRef(platform);
    env->DeleteLocalRef(systemVersion);

    return instance->getHandle();*/

    jflowdrop::DeviceInfo jdeviceInfo(deviceInfo.id,
                                      deviceInfo.name,
                                      deviceInfo.model,
                                      deviceInfo.platform,
                                      deviceInfo.system_version);
    return jdeviceInfo._getHandle();
}

jobject convert_FileInfo(JNIEnv *env, const flowdrop::FileInfo &fileInfo) {
    jstring name = string2Java(env, fileInfo.name);
    jlong size = static_cast<jlong>(fileInfo.size);

    JIObject *instance = jflowdrop::FileInfo::classs()._getHandle()->newInstance(env, sig({String_sig, "J"}, "V"), name, size);

    env->DeleteLocalRef(name);

    return instance->getHandle();
}

JavaFile::JavaFile(JIObject *instance) : _instance(instance) {}
JavaFile::~JavaFile() = default;

std::string JavaFile::getRelativePath() const {
    JavaThread thread = attachJavaVM();

    jmethodID method = thread.env->GetMethodID(jflowdrop::File::classs()._getHandle()->getHandle(), "getRelativePath",
                                        sig({}, String_sig).c_str());
    jstring jresult = (jstring) thread.env->CallObjectMethod(_instance->getHandle(), method);
    std::string result = java2String(thread.env, jresult);

    detachJavaVM(thread);

    return result;
}
std::uint64_t JavaFile::getSize() const {
    JavaThread thread = attachJavaVM();

    jmethodID method = thread.env->GetMethodID(jflowdrop::File::classs()._getHandle()->getHandle(), "getSize",
                                               sig({}, "J").c_str());
    jlong jresult = thread.env->CallLongMethod(_instance->getHandle(), method);

    detachJavaVM(thread);

    return static_cast<std::uint64_t>(jresult);
}
std::uint64_t JavaFile::getCreatedTime() const {
    JavaThread thread = attachJavaVM();

    jmethodID method = thread.env->GetMethodID(jflowdrop::File::classs()._getHandle()->getHandle(), "getCreatedTime",
                                        sig({}, "J").c_str());
    jlong jresult = thread.env->CallLongMethod(_instance->getHandle(), method);

    detachJavaVM(thread);

    return static_cast<std::uint64_t>(jresult);
}
std::uint64_t JavaFile::getModifiedTime() const {
    JavaThread thread = attachJavaVM();

    jmethodID method = thread.env->GetMethodID(jflowdrop::File::classs()._getHandle()->getHandle(), "getModifiedTime",
                                        sig({}, "J").c_str());
    jlong jresult = thread.env->CallLongMethod(_instance->getHandle(), method);

    detachJavaVM(thread);

    return static_cast<std::uint64_t>(jresult);
}
std::filesystem::perms JavaFile::getPermissions() const {
    return std::filesystem::perms::owner_read | std::filesystem::perms::owner_write;
}
void JavaFile::seek(std::uint64_t pos) {

}
std::uint64_t JavaFile::read(char *buffer, std::uint64_t count) {
    JavaThread thread = attachJavaVM();

    jsize size = static_cast<jsize>(count);

    jbyteArray byteArray = thread.env->NewByteArray(size);

    jmethodID File_method_read = thread.env->GetMethodID(jflowdrop::File::classs()._getHandle()->getHandle(), "read",
                                                         sig({"[", byte_sig, long_sig}, long_sig).c_str());
    jlong bytesRead = thread.env->CallLongMethod(_instance->getHandle(), File_method_read, byteArray, static_cast<jlong>(count));

    jbyte* bufferData = thread.env->GetByteArrayElements(byteArray, nullptr);

    std::memcpy(buffer, bufferData, count);

    thread.env->ReleaseByteArrayElements(byteArray, bufferData, JNI_ABORT);
    thread.env->DeleteLocalRef(byteArray);

    detachJavaVM(thread);

    return static_cast<std::uint64_t>(bytesRead);
}


extern "C" JNIEXPORT jstring JNICALL
Java_me_nelonn_jflowdrop_impl_NativeJFlowDrop_generateMD5Id(JNIEnv *env, jobject /* thiz */) {
    return string2Java(env, flowdrop::generate_md5_id());
}

extern "C" JNIEXPORT void JNICALL
Java_me_nelonn_jflowdrop_impl_NativeJFlowDrop_discover(JNIEnv *env, jobject /* thiz */,
                                                       jobject callback,
                                                       jobject stop_flag) {
    jobject DiscoverCallback_instance = jGlobal(env, callback);
    jobject IsStoppedFunction_instance = jGlobal(env, stop_flag);
    jmethodID IsStoppedFunction_method_isStopped = env->GetMethodID(jflowdrop::IsStoppedFunction::classs()._getHandle()->getHandle(), "isStopped", sig({}, "Z").c_str());

    auto isStopped = [IsStoppedFunction_instance, IsStoppedFunction_method_isStopped]() {
        JNIEnv* env;
        AttachResult attachResult = oldAttach(&env);
        if (attachResult == FAILED) return true;

        jboolean result = env->CallBooleanMethod(IsStoppedFunction_instance, IsStoppedFunction_method_isStopped);

        oldDetach(attachResult);

        return static_cast<bool>(result);
    };

    flowdrop::discover([DiscoverCallback_instance](const flowdrop::DeviceInfo &deviceInfo){
        JavaThread thread = attachJavaVM();

        jmethodID DiscoverCallback_method_onDiscovered = thread.env->GetMethodID(jflowdrop::DiscoverCallback::classs()._getHandle()->getHandle(), "onDiscovered", sig({csig(jflowdrop::DeviceInfo::classs().getName())}, "V").c_str());
        thread.env->CallVoidMethod(DiscoverCallback_instance, DiscoverCallback_method_onDiscovered, convert_DeviceInfo(thread.env, deviceInfo));

        detachJavaVM(thread);
    }, isStopped);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_me_nelonn_jflowdrop_impl_NativeSendRequest__1execute(JNIEnv *env, jobject /* thiz */,
                                                          jobject device_info,
                                                          jstring receiver_id,
                                                          jobject files,
                                                          jlong resolve_timeout,
                                                          jlong ask_timeout,
                                                          jobject event_listener) {
    jclass listClass = env->GetObjectClass(files);
    jmethodID sizeMethod = env->GetMethodID(listClass, "size", "()I");
    jint listSize = env->CallIntMethod(files, sizeMethod);
    jmethodID getMethod = env->GetMethodID(listClass, "get", sig({"I"}, Object_sig).c_str());
    std::vector<flowdrop::File *> javaFiles;
    for (int i = 0; i < listSize; ++i) {
        jobject element = env->CallObjectMethod(files, getMethod, i);
        auto *javaFile = new JavaFile(new JIObject(element));
        javaFiles.push_back(javaFile);
    }

    auto *sendRequest = new flowdrop::SendRequest();
    sendRequest->setDeviceInfo(convert_DeviceInfo(env, device_info));
    sendRequest->setReceiverId(java2String(env, receiver_id));
    sendRequest->setFiles(javaFiles);
    sendRequest->setResolveTimeout(std::chrono::milliseconds(resolve_timeout));
    sendRequest->setAskTimeout(std::chrono::milliseconds(ask_timeout));
    sendRequest->setEventListener(event_listener != nullptr ? new JavaEventListener(new JIObject(event_listener)) : nullptr);
    bool result = sendRequest->execute();
    return static_cast<jboolean>(result);
}