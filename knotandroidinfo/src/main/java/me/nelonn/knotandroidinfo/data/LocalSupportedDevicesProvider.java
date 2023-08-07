/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.knotandroidinfo.data;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import me.nelonn.knotandroidinfo.LineReader;

public class LocalSupportedDevicesProvider implements SupportedDevicesProvider {
    private static final String CSV_FILENAME = "supported_devices_2023-07-20T19-23-31Z.csv";
    private final Context context;
    private InputStream inputStream;

    public LocalSupportedDevicesProvider(Context context) {
        this.context = context;
    }

    @Override
    public void open() throws IOException {
        if (inputStream != null) {
            throw new IOException("Provider is already open");
        }
        inputStream = context.getAssets().open(CSV_FILENAME);
    }

    @Override
    public BufferedReader reader() throws IOException {
        if (inputStream == null) {
            throw new IOException("Provider is closed");
        }
        return new LineReader(new InputStreamReader(inputStream, StandardCharsets.UTF_16));
    }

    @Override
    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
            inputStream = null;
        }
    }
}
