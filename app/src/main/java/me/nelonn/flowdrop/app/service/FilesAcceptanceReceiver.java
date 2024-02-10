package me.nelonn.flowdrop.app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import me.nelonn.flowdrop.app.AtomicBooleanCallback;

public class FilesAcceptanceReceiver extends BroadcastReceiver {
    public static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
    public static final ConcurrentMap<Integer, AtomicBooleanCallback> CALLBACKS = new ConcurrentHashMap<>(2);
    public static final String EXTRA_ACCEPTANCE_ID = "flowdrop.acceptance_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        boolean value;
        if ("accept".equals(action)) {
            value = true;
        } else if ("decline".equals(action)) {
            value = false;
        } else {
            return;
        }
        int acceptanceId = intent.getIntExtra(EXTRA_ACCEPTANCE_ID, -1);
        if (acceptanceId == -1) return;
        AtomicBooleanCallback callback = CALLBACKS.remove(acceptanceId);
        if (callback == null) return;
        callback.set(value);
        Log.i("FLOWDROP", "Acceptance: " + acceptanceId + " " + value);
    }
}
