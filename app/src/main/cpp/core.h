/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */
#pragma once

#include "main.h"
#include "flowdrop/flowdrop.hpp"

std::string GetStringFromField(JNIEnv *env, jobject obj, jfieldID fieldID);

std::optional<std::string> GetOptionalStringFromField(JNIEnv *env, jobject obj, jfieldID fieldID);

flowdrop::DeviceInfo convert_DeviceInfo(JNIEnv *env, jobject deviceInfoObj);

jobject convert_DeviceInfo(JNIEnv *env, const flowdrop::DeviceInfo &deviceInfo);

jobject convert_FileInfo(JNIEnv *env, const flowdrop::FileInfo &fileInfo);

class JavaFile : public flowdrop::File {
public:
    JavaFile(JIObject *instance);
    ~JavaFile() override;
    [[nodiscard]] std::string getRelativePath() const override;
    [[nodiscard]] std::uint64_t getSize() const override;
    [[nodiscard]] std::uint64_t getCreatedTime() const override;
    [[nodiscard]] std::uint64_t getModifiedTime() const override;
    [[nodiscard]] std::filesystem::perms getPermissions() const override;
    void seek(std::uint64_t pos) override;
    std::uint64_t read(char *buffer, std::uint64_t count) override;

private:
    JIObject *_instance;
};
