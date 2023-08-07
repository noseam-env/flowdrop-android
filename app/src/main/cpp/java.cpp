/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

#include "java.h"
#include "java_internal.h"
#include "jni_lib/java_core_internal.h"

namespace Java {
    namespace me::nelonn::jflowdrop {

        namespace impl {

            static const std::string $$NativeDiscoverCallback = "me/nelonn/jflowdrop/impl/NativeDiscoverCallback";
            Classs NativeDiscoverCallback::classs() {
                return {jiCore->import($$NativeDiscoverCallback), $$NativeDiscoverCallback};
            }

            static const std::string $$NativeIsStoppedFunc = "me/nelonn/jflowdrop/impl/NativeIsStoppedFunc";
            Classs NativeIsStoppedFunc::classs() {
                return {jiCore->import($$NativeIsStoppedFunc), $$NativeIsStoppedFunc};
            }

            static const std::string $$NativeQueryCallback = "me/nelonn/jflowdrop/impl/NativeQueryCallback";
            Classs NativeQueryCallback::classs() {
                return {jiCore->import($$NativeQueryCallback), $$NativeQueryCallback};
            }

            static const std::string $$NativeResolveCallback = "me/nelonn/jflowdrop/impl/NativeResolveCallback";
            Classs NativeResolveCallback::classs() {
                return {jiCore->import($$NativeResolveCallback), $$NativeResolveCallback};
            }
        }

        static const std::string $$DeviceInfo = "me/nelonn/jflowdrop/DeviceInfo";
        Classs DeviceInfo::classs() {
            return {jiCore->import($$DeviceInfo), $$DeviceInfo};
        }
        Internal::Object *$$$DeviceInfo_constructor(const std::string &id,
                                                   const std::optional<std::string> &name,
                                                   const std::optional<std::string> &model,
                                                   const std::optional<std::string> &platform,
                                                   const std::optional<std::string> &systemVersion) {
            JavaThread thread = attachJavaVM();
            jobject obj = DeviceInfo::classs()._getHandle()->newInstance(thread.env, sig({String_sig, String_sig, String_sig, String_sig, String_sig}, "V"),
                    String(id)._getHandle(),
                    name.has_value() ? String(name.value())._getHandle() : nullptr,
                    model.has_value() ? String(model.value())._getHandle() : nullptr,
                    platform.has_value() ? String(platform.value())._getHandle() : nullptr,
                    systemVersion.has_value() ? String(systemVersion.value())._getHandle() : nullptr)->getHandle();
            auto iobj = new Internal::Object(obj, false);
            detachJavaVM(thread);
            return iobj;
        }
        DeviceInfo::DeviceInfo(const std::string &id,
                               const std::optional<std::string> &name,
                               const std::optional<std::string> &model,
                               const std::optional<std::string> &platform,
                               const std::optional<std::string> &systemVersion)
                               : Object($$$DeviceInfo_constructor(id, name, model, platform, systemVersion)) {}

        static const std::string $$DNSSD = "me/nelonn/jflowdrop/DNSSD";
        Classs DNSSD::classs() {
            return {jiCore->import($$DNSSD), $$DNSSD};
        }

        static const std::string $$DNSSD$DiscoverReply = "me/nelonn/jflowdrop/DNSSD$DiscoverReply";
        Classs DNSSD::DiscoverReply::classs() {
            return {jiCore->import($$DNSSD$DiscoverReply), $$DNSSD$DiscoverReply};
        }

        static const std::string $$DNSSD$IPType = "me/nelonn/jflowdrop/DNSSD$IPType";
        Classs DNSSD::IPType::classs() {
            return {jiCore->import($$DNSSD$IPType), $$DNSSD$IPType};
        }
        std::unique_ptr<Object> DNSSD::IPType::IPv6;
        std::unique_ptr<Object> DNSSD::IPType::IPv4;

        static const std::string $$DNSSD$IPAddress = "me/nelonn/jflowdrop/DNSSD$IPAddress";
        Classs DNSSD::IPAddress::classs() {
            return {jiCore->import($$DNSSD$IPAddress), $$DNSSD$IPAddress};
        }

        static const std::string $$DNSSD$ResolveReply = "me/nelonn/jflowdrop/DNSSD$ResolveReply";
        Classs DNSSD::ResolveReply::classs() {
            return {jiCore->import($$DNSSD$ResolveReply), $$DNSSD$ResolveReply};
        }

        static const std::string $$EventListener = "me/nelonn/jflowdrop/EventListener";
        Classs EventListener::classs() {
            return {jiCore->import($$EventListener), $$EventListener};
        }

        static const std::string $$File = "me/nelonn/jflowdrop/File";
        Classs File::classs() {
            return {jiCore->import($$File), $$File};
        }

        static const std::string $$FileInfo = "me/nelonn/jflowdrop/FileInfo";
        Classs FileInfo::classs() {
            return {jiCore->import($$FileInfo), $$FileInfo};
        }

        static const std::string $$DiscoverCallback = "me/nelonn/jflowdrop/DiscoverCallback";
        Classs DiscoverCallback::classs() {
            return {jiCore->import($$DiscoverCallback), $$DiscoverCallback};
        }

        static const std::string $$IsStoppedFunction = "me/nelonn/jflowdrop/IsStoppedFunction";
        Classs IsStoppedFunction::classs() {
            return {jiCore->import($$IsStoppedFunction), $$IsStoppedFunction};
        }

        static const std::string $$SendAsk = "me/nelonn/jflowdrop/SendAsk";
        Classs SendAsk::classs() {
            return {jiCore->import($$SendAsk), $$SendAsk};
        }

        static const std::string $$SendAskCallback = "me/nelonn/jflowdrop/SendAskCallback";
        Classs SendAskCallback::classs() {
            return {jiCore->import($$SendAskCallback), $$SendAskCallback};
        }
    }
}

void InitJava(JNIEnv *env) {
    jiCore->require(env, Java::me::nelonn::jflowdrop::impl::$$NativeDiscoverCallback);
    jiCore->require(env, Java::me::nelonn::jflowdrop::impl::$$NativeIsStoppedFunc);
    jiCore->require(env, Java::me::nelonn::jflowdrop::impl::$$NativeQueryCallback);
    jiCore->require(env, Java::me::nelonn::jflowdrop::impl::$$NativeResolveCallback);
    jiCore->require(env, Java::me::nelonn::jflowdrop::$$DeviceInfo);
    jiCore->require(env, Java::me::nelonn::jflowdrop::$$DNSSD);
    jiCore->require(env, Java::me::nelonn::jflowdrop::$$DNSSD$DiscoverReply);
    jiCore->require(env, Java::me::nelonn::jflowdrop::$$DNSSD$IPType);
    Java::me::nelonn::jflowdrop::DNSSD::IPType::IPv6.reset(getStaticFieldValue(env, Java::me::nelonn::jflowdrop::DNSSD::IPType::classs(), "IPv6"));
    Java::me::nelonn::jflowdrop::DNSSD::IPType::IPv4.reset(getStaticFieldValue(env, Java::me::nelonn::jflowdrop::DNSSD::IPType::classs(), "IPv4"));
    jiCore->require(env, Java::me::nelonn::jflowdrop::$$DNSSD$IPAddress);
    jiCore->require(env, Java::me::nelonn::jflowdrop::$$DNSSD$ResolveReply);
    jiCore->require(env, Java::me::nelonn::jflowdrop::$$EventListener);
    jiCore->require(env, Java::me::nelonn::jflowdrop::$$File);
    jiCore->require(env, Java::me::nelonn::jflowdrop::$$FileInfo);
    jiCore->require(env, Java::me::nelonn::jflowdrop::$$DiscoverCallback);
    jiCore->require(env, Java::me::nelonn::jflowdrop::$$IsStoppedFunction);
    jiCore->require(env, Java::me::nelonn::jflowdrop::$$SendAsk);
    jiCore->require(env, Java::me::nelonn::jflowdrop::$$SendAskCallback);
}
