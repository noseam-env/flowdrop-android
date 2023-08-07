# This file is part of FlowDrop Android.
#
# For license and copyright information please follow this link:
# https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL

cmake_minimum_required(VERSION 3.22.1)

cmake_policy(SET CMP0077 NEW)

project(jflowdrop)

include_directories(include)

set(CMAKE_CXX_STANDARD 17)

set(LIBFLOWDROP_BUILD_SHARED OFF)
set(CURL_ENABLE_SSL OFF)
set(ENABLE_KNOT_DNSSD OFF)

add_subdirectory(libflowdrop)

add_library(jflowdrop SHARED
        jni_lib/java_core.cpp
        jni_lib/java_core.h
        jni_lib/java_core_internal.h
        jni_lib/jni_core.cpp
        jni_lib/jni_core.h
        core.cpp
        core.h
        dnssd.cpp
        eventlistener.cpp
        eventlistener.h
        java.cpp
        java.h
        java_internal.h
        main.cpp
        main.h
        server.cpp)

target_link_libraries(jflowdrop PRIVATE
        libflowdrop_static
        android
        log)
