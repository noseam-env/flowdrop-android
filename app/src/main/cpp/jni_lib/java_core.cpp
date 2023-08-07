/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

#include "java_core.h"
#include "java_core_internal.h"

namespace Java {
    namespace Internal {
        Object::Object(jobject handle, bool global) : _handle(handle), _global(global) {
            if (handle == nullptr) {
                throw std::runtime_error("Passing null jobject");
            }
        }

        Object::~Object() {
            if (_handle == nullptr) return;
            JavaThread thread = attachJavaVM();
            if (_global) {
                thread.env->DeleteGlobalRef(_handle);
            } else {
                thread.env->DeleteLocalRef(_handle);
            }
            detachJavaVM(thread);
        }

        jobject Object::handle() {
            return _handle;
        }

        bool Object::isGlobal() {
            return _global;
        }
    }

    Object::Object(Internal::Object *handle) : _handle(handle) {}

    Object::~Object() = default;

    jobject Object::_getHandle() {
        return _handle->handle();
    }

    signed int Object::hashCode() {
        JavaThread thread = attachJavaVM();
        jclass clazz = thread.env->GetObjectClass(_getHandle());
        jmethodID method = thread.env->GetMethodID(clazz, "hashCode", sig({}, "I").c_str());
        jint result = thread.env->CallIntMethod(_getHandle(), method);
        detachJavaVM(thread);
        return static_cast<signed int>(result);
    }

    String Object::toString() {
        JavaThread thread = attachJavaVM();
        jclass clazz = thread.env->GetObjectClass(_getHandle());
        jmethodID method = thread.env->GetMethodID(clazz, "toString", sig({}, String_sig).c_str());
        jobject obj = thread.env->CallObjectMethod(_getHandle(), method);
        const char *stringValue = thread.env->GetStringUTFChars((jstring) obj, nullptr);
        detachJavaVM(thread);
        return {stringValue};
    }

    String::String(const std::string &value) {
        JavaThread thread = attachJavaVM();
        _handle = thread.env->NewStringUTF(value.c_str());
        detachJavaVM(thread);
    }

    String::String(jstring value) {
        if (value == nullptr) {
            throw std::runtime_error("Trying to create null String");
        }
        _handle = value;
    }

    String::~String() {
        JavaThread thread = attachJavaVM();
        thread.env->DeleteLocalRef(_handle);
        detachJavaVM(thread);
    }

    jstring String::_getHandle() {
        return _handle;
    }

    std::string String::_unwrap() {
        JavaThread thread = attachJavaVM();
        const char *strChars = thread.env->GetStringUTFChars(_handle, nullptr);
        std::string result(strChars);
        thread.env->ReleaseStringUTFChars(_handle, strChars);
        detachJavaVM(thread);
        return result;
    }

    signed int String::hashCode() {
        return 0; // TODO: implement
    }

    String String::toString() {
        return *this;
    }

    Classs::Classs(JIClass *handle, const std::string &name) : _handle(handle), _name(name) {
    }

    Classs::~Classs() {
    }

    JIClass *Classs::_getHandle() {
        return _handle;
    }

    const std::string &Classs::getName() {
        return _name;
    }

    namespace util {
        static const std::string $$Optional = "java/util/Optional";

        Classs Optional::classs() {
            return {jiCore->import($$Optional), $$Optional};
        }
    }
}

void InitJavaCore(JNIEnv *env) {
    jiCore->require(env, Java::util::$$Optional);
}

Java::Object *getStaticFieldValue(JNIEnv *env, Java::Classs classs, const std::string& name) {
    jclass clazz = classs._getHandle()->getHandle();
    jfieldID fieldId = env->GetStaticFieldID(clazz, name.c_str(), csig(std::string(classs.getName())).c_str());
    jobject localObj = env->GetStaticObjectField(clazz, fieldId);
    jobject globalObj = jGlobal(env, localObj);
    auto *iobj = new Java::Internal::Object(globalObj, true);
    return new Java::Object(iobj);
}
