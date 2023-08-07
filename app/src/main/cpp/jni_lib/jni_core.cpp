/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */
#include "jni_core.h"

#include <sstream>
#include <cstring>
#include <unordered_map>

JICore *jiCore = nullptr;

std::string sig(const std::initializer_list<std::string> &i, const std::string &o) {
    std::string result = "(";
    for (const std::string& s : i) {
        result += s;
    }
    return result + ")" + o;
}

std::string csig(const std::string &className) {
    return "L" + className + ";";
}

bool isGlobal(JNIEnv* env, jobject obj) {
    return env->IsSameObject(obj, obj);
}

std::string java2String(JNIEnv *env, jstring jStr) {
    const char *strChars = env->GetStringUTFChars(jStr, nullptr);
    std::string result(strChars);
    env->ReleaseStringUTFChars(jStr, strChars);
    return result;
}

jstring string2Java(JNIEnv *env, const std::string &str) {
    if (str.empty()) {
        return nullptr;
    }
    return env->NewStringUTF(str.c_str());
}

jobject jGlobal(JNIEnv *env, jobject obj) {
    return env->NewGlobalRef(obj);
}

jclass jGlobal(JNIEnv *env, jclass clazz) {
    return static_cast<jclass>(env->NewGlobalRef(clazz));
}

JavaThread attachJavaVM() {
    JNIEnv* env;
    AttachResult result = oldAttach(&env);
    if (result == FAILED) {
        throw std::runtime_error("Failed to attach JavaVM");
    }
    return {env, result};
}

void detachJavaVM(JavaThread thread) {
    oldDetach(thread.attachResult);
}

AttachResult oldAttach(JNIEnv** p_env) {
    jint result = jiCore->getJavaVM()->GetEnv((void**)p_env, JNI_VERSION_1_6);

    if (result == JNI_EDETACHED) {
        jint res = jiCore->getJavaVM()->AttachCurrentThread(p_env, nullptr);
        if (res < 0) {
            fprintf(stderr, "Attach failed\n");
            return FAILED;
        }
        return ATTACHED;
    } else if (result == JNI_OK) {
        return ALREADY;
    } else if (result == JNI_EVERSION) {
        return FAILED;
    }
    return FAILED;
}

void oldDetach(AttachResult result) {
    if (result != ATTACHED) return;
    jiCore->getJavaVM()->DetachCurrentThread();
}

class JIObject::Impl {
public:
    explicit Impl(jobject handle) : _handle(handle) {}
    ~Impl() = default;

    jobject getHandle() {
        return _handle;
    }

private:
    jobject _handle;
};

JIObject::JIObject(jobject handle) : pImpl(new Impl(handle)) {}
JIObject::~JIObject() = default;
jobject JIObject::getHandle() {
    return pImpl->getHandle();
}

template<typename T>
jobject convertToJNI(JNIEnv* env, T arg) {
    if constexpr (std::is_same_v<T, JIObject>) {
        return ((JIObject) arg).getHandle();
    } else if constexpr (std::is_same_v<T, jobject> || std::is_same_v<T, jstring>) {
        return arg;
    } else if constexpr (std::is_same_v<T, jint>) {
        jclass Integer_class = env->FindClass("java/lang/Integer");
        jmethodID Integer_constructor = env->GetMethodID(Integer_class, "<init>", "(I)V");
        return env->NewObject(Integer_class, Integer_constructor, (jint) arg);
    } else if constexpr (std::is_same_v<T, jdouble>) {
        jclass Double_class = env->FindClass("java/lang/Double");
        jmethodID Double_constructor = env->GetMethodID(Double_class, "<init>", "(D)V");
        return env->NewObject(Double_class, Double_constructor, (jdouble) arg);
    } else if constexpr (std::is_same_v<T, jfloat>) {
        jclass Float_class = env->FindClass("java/lang/Float");
        jmethodID Float_constructor = env->GetMethodID(Float_class, "<init>", "(F)V");
        return env->NewObject(Float_class, Float_constructor, (jfloat) arg);
    } else if constexpr (std::is_same_v<T, jbyte>) {
        jclass Byte_class = env->FindClass("java/lang/Byte");
        jmethodID Byte_constructor = env->GetMethodID(Byte_class, "<init>", "(B)V");
        return env->NewObject(Byte_class, Byte_constructor, (jbyte) arg);
    } else if constexpr (std::is_same_v<T, jshort>) {
        jclass Short_class = env->FindClass("java/lang/Short");
        jmethodID Short_constructor = env->GetMethodID(Short_class, "<init>", "(S)V");
        return env->NewObject(Short_class, Short_constructor, (jshort) arg);
    } else if constexpr (std::is_same_v<T, jlong>) {
        jclass Long_class = env->FindClass("java/lang/Long");
        jmethodID Long_constructor = env->GetMethodID(Long_class, "<init>", "(J)V");
        return env->NewObject(Long_class, Long_constructor, (jlong) arg);
    } else if constexpr (std::is_same_v<T, jboolean>) {
        jclass Boolean_class = env->FindClass("java/lang/Boolean");
        jmethodID Boolean_constructor = env->GetMethodID(Boolean_class, "<init>", "(Z)V");
        return env->NewObject(Boolean_class, Boolean_constructor, (jboolean) arg);
    } else if constexpr (std::is_same_v<T, jchar>) {
        jclass Character_class = env->FindClass("java/lang/Character");
        jmethodID Character_constructor = env->GetMethodID(Character_class, "<init>", "(C)V");
        return env->NewObject(Character_class, Character_constructor, (jchar) arg);
    } else {
        return nullptr;
    }
}

template<std::size_t I = 0, typename... Args>
void convertAllToJNI(JNIEnv* env, jobjectArray jArgs, Args&&... args) {
    if constexpr (I < sizeof...(Args)) {
        jobject jArg = convertToJNI(env, std::get<I>(std::forward_as_tuple(args...)));
        env->SetObjectArrayElement(jArgs, I, jArg);
        convertAllToJNI<I + 1>(env, jArgs, std::forward<Args>(args)...);
    }
}

class JIClass::Impl {
public:
    explicit Impl(jclass handle) : _handle(handle) {}
    ~Impl() = default;

    template<typename... Args>
    JIObject *newInstance(JNIEnv *env, const std::string &sig, va_list args) {
        jmethodID constructor = env->GetMethodID(_handle, "<init>", sig.c_str());
        jobject instance = env->NewObjectV(_handle, constructor, args);
        return new JIObject(instance);
    }

    jclass getHandle() {
        return _handle;
    }

private:
    jclass _handle;
};

JIClass::JIClass(jclass handle) : pImpl(new Impl(handle)) {}
JIClass::~JIClass() = default;
JIObject *JIClass::newInstance(JNIEnv *env, const std::string &sig, ...) {
    va_list args;
    va_start(args, sig);
    auto *result = pImpl->newInstance(env, sig, args);
    va_end(args);
    return result;
}
jclass JIClass::getHandle() {
    return pImpl->getHandle();
}

std::string normalizeClassName(const std::string& input) {
    std::string result = input;
    std::replace(result.begin(), result.end(), '.', '/');
    return result;
}

class JICore::Impl {
public:
    explicit Impl(JavaVM *javaVm) : _javaVm(javaVm) {}
    ~Impl() = default;

    JavaVM *getJavaVM() {
        return _javaVm;
    }

    JIClass *require(JNIEnv *env, const std::string &className) {
        std::string path = normalizeClassName(className);
        if (_loadedClasses.count(path) > 0) return _loadedClasses[path];
        jclass localClass = env->FindClass(path.c_str());
        jclass globalClass = jGlobal(env, localClass);
        auto *jiClass = new JIClass(globalClass);
        _loadedClasses[path] = jiClass;
        return jiClass;
    }

    JIClass *import(const std::string &className) {
        std::string path = normalizeClassName(className);
        if (_loadedClasses.count(path) == 0) return nullptr;
        return _loadedClasses[path];
    }

private:
    JavaVM *_javaVm;
    std::unordered_map<std::string, JIClass *> _loadedClasses;
};

JICore::JICore(JavaVM *javaVm) : pImpl(new Impl(javaVm)) {}
JICore::~JICore() = default;
JavaVM *JICore::getJavaVM() {
    return pImpl->getJavaVM();
}
JIClass *JICore::require(JNIEnv *env, const std::string &className) {
    return pImpl->require(env, className);
}
JIClass *JICore::import(const std::string &className) {
    return pImpl->import(className);
}
