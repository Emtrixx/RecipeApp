package com.example.recipeapp.lib

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.example.recipeapp.MainActivity

const val EXPIRY_CHANNEL_ID = "expiry"

fun sendNotification(context: Context, notificationId: Int, notification: Notification) {
    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("NotificationSettingsView", "No permission")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    context as MainActivity,
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