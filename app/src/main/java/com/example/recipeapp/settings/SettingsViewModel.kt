package com.example.recipeapp.settings

import android.app.Activity
import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.R
import com.example.recipeapp.dataStore
import com.example.recipeapp.lib.EXPIRY_CHANNEL_ID
import com.example.recipeapp.lib.sendNotification
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class DarkModeState(val value: String) {
    LIGHT("light"),
    DARK("dark"),
}

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    var notificationsState by mutableStateOf(false)
        private set

    var darkModeState: String? by mutableStateOf(null)
        private set

    init {
        getNotificationState()
        getDarkModeState()
    }

    private fun getDarkModeState() {
        val context = getApplication<Application>().applicationContext
        viewModelScope.launch {
            darkModeState = context.dataStore.data.first()[DARK_MODE]
        }
    }

    fun setDarkModeState(context: Activity, state: String) {
        viewModelScope.launch {
            context.dataStore.edit { settings ->
                settings[DARK_MODE] = state
                darkModeState = state
            }
        }
    }

    private fun getNotificationState() {
        val context = getApplication<Application>().applicationContext
        viewModelScope.launch {
            notificationsState = context.dataStore.data.first()[NOTIFICATIONS_STATE] ?: false
        }
    }

    fun setNotificationState(context: Activity ,state: Boolean) {
//        val context = getApplication<Application>().applicationContext
        viewModelScope.launch {
            if (state) {
                val builder = NotificationCompat.Builder(context, EXPIRY_CHANNEL_ID)
                    .setSmallIcon(R.drawable.sand_clock_notification)
                    .setContentTitle("Notifications enabled!")
                    .setContentText("You have enabled notifications")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                sendNotification(context, 1, builder.build())
            }
            context.dataStore.edit { settings ->
                settings[NOTIFICATIONS_STATE] = state
                // set new state in UI
                notificationsState = state
            }
        }
    }

    fun testNotification() {
        val context = getApplication<Application>().applicationContext

        viewModelScope.launch {
            val builder = NotificationCompat.Builder(context, EXPIRY_CHANNEL_ID)
                .setSmallIcon(R.drawable.sand_clock_notification)
                .setContentTitle("Notifications enabled!")
                .setContentText("You have enabled notifications")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
                sendNotification(context, 1, builder.build())
        }
    }
}