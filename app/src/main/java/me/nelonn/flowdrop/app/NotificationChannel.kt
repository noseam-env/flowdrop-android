package me.nelonn.flowdrop.app

import android.app.NotificationManager
import me.nelonn.flowdrop.R

enum class NotificationChannel(
        val id: String?,
        val importance: Int?,
        val titleRes: Int?,
        val descRes: Int?
) {
    FOREGROUND_SERVICE(
            "foreground_service",
            NotificationManager.IMPORTANCE_LOW,
            R.string.notification_channel_service_foreground_title,
            R.string.notification_channel_service_foreground_subtitle
    ),
    RECEIVED_FILE(
            "received_file",
            NotificationManager.IMPORTANCE_LOW,
            R.string.notification_channel_service_foreground_title,
            R.string.notification_channel_service_foreground_subtitle
    );

    fun getIntId(): Int {
        return this.ordinal + 1;
    }
}