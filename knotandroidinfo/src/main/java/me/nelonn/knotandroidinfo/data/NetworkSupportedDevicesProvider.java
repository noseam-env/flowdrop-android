/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.knotandroidinfo.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import me.nelonn.knotandroidinfo.LineReader;

public class NetworkSupportedDevicesProvider implements SupportedDevicesProvider {
    private static final String SUPPORTED_DEVICES_URL = "https://storage.googleapis.com/play_public/supported_devices.csv";
    private HttpURLConnection connection;

    @Override
    public void open() throws IOException {
        if (connection != null) {
            throw new IOException("Provider is already open");
        }
        URL url = new URL(SUPPORTED_DEVICES_URL);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
    }

    @Override
    public BufferedReader reader() throws IOException {
        if (connection == null) {
            throw new IOException("Provider is closed");
        }
        InputStream inputStream = connection.getInputStream();
        return new LineReader(new InputStreamReader(inputStream, StandardCharsets.UTF_16));
    }

    @Override
    public void close() throws IOException {
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }
}
