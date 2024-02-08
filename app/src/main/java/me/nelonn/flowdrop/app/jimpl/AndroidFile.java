/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop.app.jimpl;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import me.nelonn.jflowdrop.File;

public class AndroidFile implements File, Cloneable, Serializable {
    private final Context context;
    private final Uri uri;
    private final String relativePath;
    private final long size;
    private final long createdTime;
    private final long modifiedTime;
    private InputStream inputStream;

    public AndroidFile(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;

        ContentResolver contentResolver = context.getContentResolver();
        try (InputStream tempStream = contentResolver.openInputStream(uri)) {
            if (tempStream == null) throw new IllegalArgumentException("Invalid file");
            size = tempStream.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        relativePath = getFileNameFromUri(contentResolver, uri);

        String[] projection = {MediaStore.MediaColumns.DATE_ADDED, MediaStore.MediaColumns.DATE_MODIFIED};

        long createdTime = 0;
        long modifiedTime = 0;
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex;
            columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED);
            if (columnIndex != -1) {
                createdTime = cursor.getLong(columnIndex);
            }
            columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED);
            if (columnIndex != -1) {
                modifiedTime = cursor.getLong(columnIndex);
            }
            cursor.close();
        }
        if (createdTime <= 0) {
            createdTime = System.currentTimeMillis() / 1000;
        }
        if (modifiedTime <= 0) {
            modifiedTime = createdTime;
        }
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
    }

    public void open() {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            inputStream = contentResolver.openInputStream(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Uri getUri() {
        return uri;
    }

    @Override
    public String getRelativePath() {
        return relativePath;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public long getModifiedTime() {
        return modifiedTime;
    }

    @Override
    public long read(byte[] buffer, long count) {
        try {
            if (inputStream != null) {
                return inputStream.read(buffer, 0, (int) count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void close() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFileNameFromUri(ContentResolver contentResolver, Uri uri) {
        String fileName = null;

        if (uri.getScheme().equals("file")) {
            fileName = uri.getLastPathSegment();
        } else if (uri.getScheme().equals("content")) {
            try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                    if (displayNameIndex != -1) {
                        fileName = cursor.getString(displayNameIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return fileName;
    }

    @NonNull
    @Override
    public AndroidFile clone() {
        try {
            return (AndroidFile) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
