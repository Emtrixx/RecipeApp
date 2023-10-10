package com.example.recipeapp.lib

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getString
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.recipeapp.R
import java.util.concurrent.TimeUnit

const val EXPIRY_CHANNEL_ID = "expiry"

fun setupNotifications(context: Context) {
    // Create notification manager
    val name = getString(context, R.string.expiry_channel_name)
    val descriptionText = getString(context, R.string.expiry_channel_description)
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel("expiry", name, importance).apply {
        description = descriptionText
    }
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)

    // Schedule notifications worker
    val dailyWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS).build()
    WorkManager.getInstance(context).enqueue(dailyWorkRequest)
}

fun sendNotification(context: Context, notificationId: Int, notification: Notification) {
    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (context is Activity) {
                Log.d("NotificationSettingsView", "No permission")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1
                    )
                } else {
                    val toast = Toast.makeText(
                        context,
                        "Please enable notifications in your settings",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                    return
                }
            }
        }
        notify(notificationId, notification)
    }
}

//fun createNotificationManager() {
//    val name = getString(R.string.expiry_channel_name)
//    val descriptionText = getString(R.string.expiry_channel_description)
//    val importance = NotificationManager.IMPORTANCE_DEFAULT
//    val channel = NotificationChannel("expiry", name, importance).apply {
//        description = descriptionText
//    }
//    val notificationManager: NotificationManager =
//        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    notificationManager.createNotificationChannel(channel)
//}