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
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import me.nelonn.flowdrop.R;
import me.nelonn.flowdrop.app.AtomicBooleanCallback;
import me.nelonn.flowdrop.app.NotificationChannel;
import me.nelonn.flowdrop.app.Preferences;
import me.nelonn.flowdrop.app.ServerListener;
import me.nelonn.flowdrop.app.Util;
import me.nelonn.flowdrop.app.service.FilesAcceptanceReceiver;
import me.nelonn.flowdrop.ui.AppController;
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

    private MessageHandler messageHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceCompat.startForeground(this, NotificationChannel.FOREGROUND_SERVICE.getIntId(), showNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);

        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_FOREGROUND);
        thread.start();

        Looper serviceLooper = thread.getLooper();
        messageHandler = new MessageHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Message message = messageHandler.obtainMessage();
        message.arg1 = startId;
        messageHandler.sendMessage(message);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        messageHandler = null;
        super.onDestroy();
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

    public PendingIntent makeAcceptanceIntent(int id, String action) {
        Intent intent = new Intent(this, FilesAcceptanceReceiver.class);
        intent.setAction(action);
        intent.putExtra(FilesAcceptanceReceiver.EXTRA_ACCEPTANCE_ID, id);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
    }

    private final class MessageHandler extends Handler {
        private final Handler mainHandler = new Handler(Looper.getMainLooper());

        public MessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            try {
                Server server = JFlowDrop.getInstance().createServer(AppController.getInstance().getDeviceInfo());
                server.setDestDir(Preferences.getDestinationDirectory(ServerForegroundService.this));
                server.setAskCallback(sendAsk -> {
                    AtomicBooleanCallback callback = new AtomicBooleanCallback();
                    int acceptanceId = FilesAcceptanceReceiver.ATOMIC_INTEGER.incrementAndGet();
                    FilesAcceptanceReceiver.CALLBACKS.put(acceptanceId, callback);
                    mainHandler.post(() -> {
                        Context context = getApplicationContext();
                        Intent notificationIntent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        notificationIntent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        notificationIntent.putExtra(Settings.EXTRA_CHANNEL_ID, NotificationChannel.ACCEPTING_FILES.getId());
                        NotificationCompat.Builder builder = Util.createNotification(context, NotificationChannel.ACCEPTING_FILES)
                                .setContentTitle(getString(R.string.notification_ask_title))
                                .setContentText(getString(R.string.notification_ask_subtitle))
                                .setSmallIcon(R.drawable.ic_notification_icon)
                                .setTicker(getString(R.string.notification_ask_title))
                                .setDeleteIntent(makeAcceptanceIntent(acceptanceId, "decline"))
                                .addAction(R.drawable.ic_notification_icon, "Decline", makeAcceptanceIntent(acceptanceId, "decline"))
                                .addAction(R.drawable.ic_notification_icon, "Accept", makeAcceptanceIntent(acceptanceId, "accept"));
                        Notification notification = builder.build();
                        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NotificationChannel.ACCEPTING_FILES.getIntId(), notification);
                    });
                    Optional<Boolean> accepted = callback.await(Preferences.WAIT_FOR_ACCEPT_MILLIS, TimeUnit.MILLISECONDS);
                    if (!accepted.orElse(false)) {
                        mainHandler.post(() -> {
                            // TODO: unique notification id
                            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NotificationChannel.ACCEPTING_FILES.getIntId());
                        });
                        return false;
                    }
                    // TODO: implement userdata in this case
                    // Why is this necessary?
                    // sending a file session does not have a unique ID
                    // we need to update the existing notification
                    return true;
                });
                server.setEventListener(new ServerListener(ServerForegroundService.this));
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
