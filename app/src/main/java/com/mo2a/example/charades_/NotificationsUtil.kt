package com.mo2a.example.charades_

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.Settings.Global.getString
import androidx.core.app.NotificationCompat


// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    // Create the content intent for the notification, which launches
    // this activity
//    val contentIntent = Intent(applicationContext, GameFragment::class.java)
//    val contentPendingIntent = PendingIntent.getActivity(
//        applicationContext,
//        NOTIFICATION_ID,
//        contentIntent,
//        PendingIntent.FLAG_UPDATE_CURRENT
//    )

    val icon = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.mipmap.icon
    )
//    val bigPicStyle = NotificationCompat.BigPictureStyle()
//        .bigLargeIcon(null)

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.app_notification_channel_id)
    )
        .setLargeIcon(icon)
        .setSmallIcon(R.mipmap.icon)
        .setContentTitle(applicationContext
            .getString(R.string.app_name))
        .setContentText(messageBody)
        .setAutoCancel(true)
        .setChannelId("app notification")
//        .setContentIntent(contentPendingIntent)


        .setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(NOTIFICATION_ID, builder.build())
}


fun NotificationManager.cancelNotifications() {
    cancelAll()
}
