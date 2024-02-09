/*
 * This file is part of FlowDrop Android.
 *
 * For license and copyright information please follow this link:
 * https://github.com/noseam-env/flowdrop-android/blob/master/LEGAL
 */

package me.nelonn.flowdrop.app;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class Util {
    private Util() {
        throw new UnsupportedOperationException();
    }

    public static void openUrl(@NonNull Context context, @NonNull String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static boolean isServiceRunning(@NonNull Context context, @NonNull Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static NotificationCompat.Builder createNotification(Context context, NotificationChannel channel) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        android.app.NotificationChannel notificationChannel = new android.app.NotificationChannel(
                channel.getId(),
                context.getString(channel.getTitleRes()),
                channel.getImportance()
        );
        notificationChannel.setDescription(context.getString(channel.getDescRes()));
        notificationManager.createNotificationChannel(notificationChannel);
        return new NotificationCompat.Builder(context, channel.getId());
    }
}
