package com.example.recipeapp.lib

import Database.Recipeapp
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.recipeapp.R
import java.time.LocalDate

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val db = Recipeapp.getInstance(applicationContext)

        val products = db.RecipeappDao().GetProducts()
        val expiringProducts = mutableListOf<String>()

        products.forEach {product ->
            val bestBeforeDates = product.bestbefore.filterNotNull()
            bestBeforeDates.forEach{date ->
                if (isWithinNextThreeDays(date)) {
                    expiringProducts.add(product.name)
                }
            }
        }

        if (expiringProducts.isNotEmpty()) {
            val builder = NotificationCompat.Builder(applicationContext, EXPIRY_CHANNEL_ID)
                .setSmallIcon(R.drawable.sand_clock_notification)
                .setContentTitle("Expiring products")
                .setContentText("You have ${expiringProducts.size} expiring products")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // TODO: set content intent
            // .setContentIntent( MainActivity.getPendingIntent(applicationContext) )

            sendNotification(applicationContext, 1, builder.build())
        }

        return Result.success()
    }
}

fun isWithinNextThreeDays(date: LocalDate): Boolean {
    val today = LocalDate.now()
    val threeDaysFromNow = today.plusDays(3)
    return !date.isBefore(today) && !date.isAfter(threeDaysFromNow)
}