/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop.background;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.concurrent.ThreadLocalRandom;

import me.nelonn.flowdrop.AppController;
import me.nelonn.flowdrop.R;
import me.nelonn.jflowdrop.DeviceInfo;
import me.nelonn.jflowdrop.EventListener;
import me.nelonn.jflowdrop.JFlowDrop;
import me.nelonn.jflowdrop.Server;

public class BackgroundServerService extends Service {
    private static final String CHANNEL_ID = "ReceiveChannel";
    private ServiceHandler serviceHandler;
    private String destDir;

    @Override
    public void onCreate() {
        super.onCreate();

        destDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        Log.i("FLOWDROP", "destDir: " + destDir);

        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        Looper serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);

        //startForeground(NOTIFICATION_ID, new Notification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
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
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Received files notification", NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }
                    String senderName = sender.getName().orElse(sender.getModel().orElse(sender.getId()));
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(BackgroundServerService.this, CHANNEL_ID)
                            .setContentTitle(senderName + " sent you file(s)")
                            .setContentText("File(s) saved in downloads directory")
                            .setSmallIcon(R.drawable.ic_notification_icon)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    int notificationId = ThreadLocalRandom.current().nextInt();
                    notificationManager.notify(notificationId, builder.build());
                }
            });
        }
    }

}
