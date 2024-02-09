/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop.ui.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import me.nelonn.flowdrop.app.NotificationChannel;
import me.nelonn.flowdrop.app.Util;
import me.nelonn.flowdrop.ui.AppController;
import me.nelonn.flowdrop.R;
import me.nelonn.jflowdrop.DeviceInfo;
import me.nelonn.jflowdrop.EventListener;
import me.nelonn.jflowdrop.JFlowDrop;
import me.nelonn.jflowdrop.Server;

public class ServerForegroundService extends Service {
    public static void start(Context context, Boolean restart) {
        Intent intent = new Intent(context, ServerForegroundService.class);
        if (restart) {
            context.stopService(intent);
        }
        context.startForegroundService(intent);
    }

    private ServiceHandler serviceHandler;
    private String destDir;

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceCompat.startForeground(this, NotificationChannel.FOREGROUND_SERVICE.getIntId(), showNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);

        destDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        Log.i("FLOWDROP", "destDir: " + destDir);

        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        Looper serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        return START_STICKY;
    }

    public Notification showNotification() {
        Intent notificationIntent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        notificationIntent.putExtra(Settings.EXTRA_CHANNEL_ID, NotificationChannel.FOREGROUND_SERVICE.getId());
        NotificationCompat.Builder builder = Util.createNotification(this, NotificationChannel.FOREGROUND_SERVICE)
                .setContentTitle(getString(R.string.notification_service_foreground_title))
                .setContentText(getString(R.string.notification_service_foreground_subtitle))
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(
                        this,
                        NotificationChannel.FOREGROUND_SERVICE.getIntId(),
                        notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE
                ))
                .setTicker(getString(R.string.notification_service_foreground_title));
        Notification notification = builder.build();
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NotificationChannel.FOREGROUND_SERVICE.getIntId(), notification);
        return notification;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            try {
                Server server = JFlowDrop.getInstance().createServer(AppController.getInstance().getDeviceInfo());
                server.setDestDir(destDir);
                server.setAskCallback(sendAsk -> {
                    /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Context context = getApplication();

                            Intent intent = new Intent(context, ShareActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    });*/
                    return true;
                });
                server.setEventListener(new Listener());
                server.run();
                server.close();
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    private class Listener implements EventListener {
        public void onReceivingStart(DeviceInfo sender, long totalSize) {
            Log.i("FLOWDROP", "onReceivingStart: " + sender);
        }

        public void onReceivingEnd(DeviceInfo sender, long totalSize) {
            Log.i("FLOWDROP", "onReceivingEnd: " + sender);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String senderName = sender.getName().orElse(sender.getModel().orElse(sender.getId()));
                    NotificationCompat.Builder builder = Util.createNotification(ServerForegroundService.this, NotificationChannel.RECEIVED_FILE)
                            .setContentTitle(senderName + " sent you file(s)")
                            .setContentText("File(s) saved in downloads directory")
                            .setSmallIcon(R.drawable.ic_notification_icon)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NotificationChannel.RECEIVED_FILE.getIntId(), builder.build());
                }
            });
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
