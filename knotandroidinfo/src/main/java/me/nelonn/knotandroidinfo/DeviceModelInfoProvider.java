/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.knotandroidinfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;

import com.google.gson.Gson;

import java.io.BufferedReader;

import me.nelonn.knotandroidinfo.data.LocalSupportedDevicesProvider;
import me.nelonn.knotandroidinfo.data.NetworkSupportedDevicesProvider;
import me.nelonn.knotandroidinfo.data.SupportedDevicesProvider;

public class DeviceModelInfoProvider {
    private static final String SHARED_PREF_NAME = "knotandroidinfo";
    private static final Gson GSON = new Gson();

    private final Context context;

    public DeviceModelInfoProvider(Context context) {
        this.context = context;
    }

    private static DeviceModelInfo fallbackModelInfo() {
        // samsung -> Samsung
        return new DeviceModelInfo(Util.capitalize(Build.MANUFACTURER), null, Build.DEVICE, Build.MODEL);
    }

    public DeviceModelInfo provideModelInfo() {
        return provideModelInfo(Build.DEVICE, Build.MODEL);
    }

    public DeviceModelInfo provideModelInfo(String codename, String model) {
        try {
            return new Request(context).execute(codename, model).get();
        } catch (Exception e) {
            return fallbackModelInfo();
        }
    }

    private static class Request extends AsyncTask<String, Void, DeviceModelInfo> {
        @SuppressLint("StaticFieldLeak")
        private final Context context;

        public Request(Context context) {
            this.context = context;
        }

        @Override
        protected DeviceModelInfo doInBackground(String... strings) {
            String codename = strings[0];
            String model = strings[1];
            SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

            String savedJson = preferences.getString(SHARED_PREF_NAME, null);
            if (savedJson != null) {
                return GSON.fromJson(savedJson, DeviceModelInfo.class);
            }

            try (SupportedDevicesProvider supportedDevicesProvider = isInternetAvailable(context) ? new NetworkSupportedDevicesProvider() : new LocalSupportedDevicesProvider(context)) {
                supportedDevicesProvider.open();
                try (BufferedReader reader = supportedDevicesProvider.reader()) {
                    String line;
                    reader.readLine(); // skip header
                    while ((line = reader.readLine()) != null) {
                        if (line.length() == 0) continue;
                        String[] records = line.split(",", 4);
                        if (records.length != 4) continue;
                        if (!records[2].equalsIgnoreCase(codename) || !records[3].equalsIgnoreCase(model)) continue;
                        String manufacturer = records[0];
                        String marketingName = records[1];
                        // "Xiaomi" "Xiaomi 11T" -> "Xiaomi" "11T"
                        if (marketingName.startsWith(manufacturer + " ")) {
                            marketingName = marketingName.substring(manufacturer.length() + 1);
                        }
                        String jsonStr = GSON.toJson(new DeviceModelInfo(manufacturer, marketingName, records[2], records[3]), DeviceModelInfo.class);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(SHARED_PREF_NAME, jsonStr);
                        editor.apply();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return fallbackModelInfo();
            }

            savedJson = preferences.getString(SHARED_PREF_NAME, null);
            if (savedJson != null) {
                return GSON.fromJson(savedJson, DeviceModelInfo.class);
            }

            return fallbackModelInfo();
        }
    }

    private static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

}
