/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */
#pragma once

#include "jni_lib/java_core.h"
#include "optional" // optional

namespace Java {
    namespace me::nelonn::jflowdrop {
        namespace impl {

            class NativeDiscoverCallback : public Object {
            public:
                static Classs classs();
            };

            class NativeIsStoppedFunc : public Object {
            public:
                static Classs classs();
            };

            class NativeQueryCallback : public Object {
            public:
                static Classs classs();
            };

            class NativeResolveCallback : public Object {
            public:
                static Classs classs();
            };

        }

        class DeviceInfo : public Object {
        public:
            static Classs classs();

            DeviceInfo(const std::string &id,
                       const std::optional<std::string> &name,
                       const std::optional<std::string> &model,
                       const std::optional<std::string> &platform,
                       const std::optional<std::string> &systemVersion);
            ~DeviceInfo() = default;
        };

        class DNSSD : public Object {
        public:
            static Classs classs();

            class DiscoverReply : public Object {
            public:
                static Classs classs();
            };

            class IPType : public Object {
            public:
                static Classs classs();

                static std::unique_ptr<Object> IPv6;
                static std::unique_ptr<Object> IPv4;
            };

            class IPAddress : public Object {
            public:
                static Classs classs();
            };

            class ResolveReply : public Object {
            public:
                static Classs classs();
            };
        };

        class EventListener : public Object {
        public:
            static Classs classs();
        };

        class File : public Object {
        public:
            static Classs classs();
        };

        class FileInfo : public Object {
        public:
            static Classs classs();
        };

        class DiscoverCallback : public Object {
        public:
            static Classs classs();
        };

        class IsStoppedFunction : public Object {
        public:
            static Classs classs();
        };

        class SendAsk : public Object {
        public:
            static Classs classs();
        };

        class SendAskCallback : public Object {
        public:
            static Classs classs();
        };
    }
}
