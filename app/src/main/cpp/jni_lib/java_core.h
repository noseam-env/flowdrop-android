/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */
#pragma once

#include "jni_core.h"

namespace Java {
    class Classs;

    class String;

    class IObject {
        virtual signed int hashCode() = 0;

        virtual String toString() = 0;
    };

    namespace Internal {
        class Object {
        public:
            Object(jobject handle, bool global);

            ~Object();

            jobject handle();

            bool isGlobal();

        private:
            jobject _handle;
            bool _global;
        };
    }

    class Object : public IObject {
    public:
        Object(Internal::Object *);

        ~Object();

        jobject _getHandle();

        signed int hashCode() override;

        String toString() override;

    protected:
        Internal::Object *_handle;
    };

    class String final : public IObject {
    public:
        String(const std::string &handle);

        String(jstring handle);

        ~String();

        jstring _getHandle();

        std::string _unwrap();

        signed int hashCode() override;

        String toString() override;

    private:
        jstring _handle;
    };

    class Classs {
    public:
        Classs(JIClass *handle, const std::string &name);

        ~Classs();

        JIClass *_getHandle();

        const std::string &getName();

    private:
        JIClass *_handle;
        const std::string &_name;
    };

    namespace util {
        class Optional final : public Object {
        public:
            static Classs classs();

            Optional(jobject handle, bool global = false);

            ~Optional();

            bool isPresent();

        private:
            JIObject *_handle;
        };
    }
}
