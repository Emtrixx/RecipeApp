package com.example.recipeapp.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.recipeapp.MainActivity
import com.example.recipeapp.R
import com.example.recipeapp.lib.EXPIRY_CHANNEL_ID

@Composable
fun NotificationSettingsView() {
    val context = LocalContext.current

    Button(onClick = {
        val builder = NotificationCompat.Builder(context, EXPIRY_CHANNEL_ID)
            .setSmallIcon(R.drawable.sand_clock_notification)
            .setContentTitle("Expiry Alert!")
            .setContentText("Your product is about to go bad! Use it with one of our wonderful recipe ideas!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

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
                }
                return@Button
            }
            notify(1, builder.build())
        }
    }) {
        Text("Show Notification")
    }
}