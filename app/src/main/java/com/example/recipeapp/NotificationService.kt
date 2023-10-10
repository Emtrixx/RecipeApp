package com.example.recipeapp

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.recipeapp.lib.EXPIRY_CHANNEL_ID
import com.example.recipeapp.lib.sendNotification

// NOT IN USE: NotificationWorker is used instead
class NotificationService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val context = this

        val builder = NotificationCompat.Builder(context, EXPIRY_CHANNEL_ID)
            .setSmallIcon(R.drawable.sand_clock_notification)
            .setContentTitle("Expiry Alert!")
            .setContentText("Your product is about to go bad! Use it with one of our wonderful recipe ideas!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

//        startForeground()
        sendNotification(context, 1, builder.build())
        // Stop the service once the notification is sent
        stopSelf()

        return START_NOT_STICKY
    }
}

