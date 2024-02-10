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
    public void onReceivingEnd(DeviceInfo sender, long totalSize) {
        Log.i("FLOWDROP", "onReceivingEnd: " + sender);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String senderName = sender.getName().orElse(sender.getModel().orElse(sender.getId()));
                NotificationCompat.Builder builder = Util.createNotification(context, NotificationChannel.RECEIVED_FILES)
                        .setContentTitle(senderName + " sent you file(s)")
                        .setContentText("File(s) saved in downloads directory")
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(NotificationChannel.RECEIVED_FILES.getIntId(), builder.build());
            }
        });
    }
}
