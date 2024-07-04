package me.nelonn.flowdrop.app;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import me.nelonn.flowdrop.R;
import me.nelonn.jflowdrop.DeviceInfo;
import me.nelonn.jflowdrop.EventListener;

public class ServerListener implements EventListener {
    private final Context context;

    public ServerListener(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public void onReceivingStart(DeviceInfo sender, long totalSize) {
        Log.i("FLOWDROP", "onReceivingStart: " + sender);
    }

    @Override
    public void onReceivingTotalProgress(DeviceInfo sender, long totalSize, long receivedSize) {
        Log.i("FLOWDROP", "onReceivingTotalProgress: " + sender + " " + totalSize + " " + receivedSize);
        new Handler(Looper.getMainLooper()).post(() -> {
            String senderName = sender.getName().orElse(sender.getModel().orElse(sender.getId()));
            int progress = (int) ((receivedSize * 100) / totalSize);
            Log.i("FLOWDROP", "Progress: " + progress);
            NotificationCompat.Builder builder = Util.createNotification(context, NotificationChannel.ACCEPTING_FILES)
                    .setContentTitle("Receiving file(s) from " + senderName)
                    .setContentText("File(s) saved in downloads directory")
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setProgress(100, progress, false);
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(NotificationChannel.ACCEPTING_FILES.getIntId(), builder.build());
        });
    }

    @Override
    public void onReceivingEnd(DeviceInfo sender, long totalSize) {
        Log.i("FLOWDROP", "onReceivingEnd: " + sender);
        new Handler(Looper.getMainLooper()).post(() -> {
            String senderName = sender.getName().orElse(sender.getModel().orElse(sender.getId()));
            NotificationCompat.Builder builder = Util.createNotification(context, NotificationChannel.ACCEPTING_FILES)
                    .setContentTitle(senderName + " sent you file(s)")
                    .setContentText("File(s) saved in downloads directory")
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(NotificationChannel.ACCEPTING_FILES.getIntId(), builder.build());
        });
    }
}
