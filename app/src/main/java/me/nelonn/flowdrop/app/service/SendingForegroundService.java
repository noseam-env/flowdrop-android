/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop.app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.nelonn.flowdrop.ui.AppController;
import me.nelonn.flowdrop.R;
import me.nelonn.flowdrop.app.jimpl.AndroidFile;
import me.nelonn.jflowdrop.EventListener;
import me.nelonn.jflowdrop.File;
import me.nelonn.jflowdrop.JFlowDrop;
import me.nelonn.jflowdrop.SendRequest;

public class SendingForegroundService extends Service {
    private static final String CHANNEL_ID = "SendingChannel";
    private static final int NOTIFICATION_ID = 1;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    private Handler handler;
    private Runnable progressRunnable;

    private String receiverId;
    private List<AndroidFile> files;

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setFiles(List<AndroidFile> files) {
        this.files = files;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        receiverId = intent.getStringExtra("receiverId");
        files = intent.getParcelableArrayListExtra("files").stream().map(uri -> new AndroidFile(getApplicationContext(), (Uri) uri)).collect(Collectors.toList());

        startForeground(NOTIFICATION_ID, getNotification());
        performBackgroundTask();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sending Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification getNotification() {
        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sending file(s)")
                .setContentText("Resolving receiver...")
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setOngoing(true)
                .setProgress(100, 0, true);
        return notificationBuilder.build();
    }

    private void updateProgress(int progress) {
        notificationBuilder.setProgress(100, progress, false);
        notificationBuilder.setContentText("Sending in progress...");
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void performBackgroundTask() {
        progressRunnable = () -> {
            List<AndroidFile> files = new ArrayList<>();
            SendingForegroundService.this.files.forEach(f -> {
                AndroidFile file = f.clone();
                file.open();
                files.add(file);
            });

            SendRequest sendRequest = JFlowDrop.getInstance().createSendRequest()
                    .setDeviceInfo(AppController.getInstance().getDeviceInfo())
                    .setReceiverId(receiverId)
                    .setFiles(files.stream().map(f -> (File) f).collect(Collectors.toList()))
                    .setEventListener(new ProgressListener())
                    .setResolveTimeout(60 * 1000); // some resolving issues found
            boolean result = sendRequest.execute();
            // todo:
            // if result true we will delete notification
            // if result false we show notification with error
            stopForeground(true);
        };
        handler.post(progressRunnable);
    }

    private class ProgressListener implements EventListener {
        public void onSendingTotalProgress(long totalSize, long currentSize) {
            updateProgress((int) (currentSize / totalSize));
        }
    }
}
