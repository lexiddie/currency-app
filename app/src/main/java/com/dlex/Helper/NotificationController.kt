package com.dlex.Helper

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import com.dlex.Activity.MainController
import com.dlex.R

class NotificationController {
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var notificationBuilder: Notification.Builder
    private val channelID = "com.lexiddie"
    private val description = "Price Alert Notifications"

    fun show(context: Context, details: String, notificationID: Int) {
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(context, MainController::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelID, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(false)
            notificationManager .createNotificationChannel(notificationChannel)

            notificationBuilder = Notification.Builder(context, channelID)
                    .setContentTitle("Price Alert Notification")
                    .setContentText(details)
                    .setSmallIcon(R.drawable.logo)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.logo))
                    .setContentIntent(pendingIntent)
        } else {
            notificationBuilder = Notification.Builder(context)
                    .setContentTitle("Price Alert Notification")
                    .setContentText(details)
                    .setSmallIcon(R.drawable.logo)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.logo))
                    .setContentIntent(pendingIntent)
        }
        notificationManager.notify(notificationID, notificationBuilder.build())
    }
}