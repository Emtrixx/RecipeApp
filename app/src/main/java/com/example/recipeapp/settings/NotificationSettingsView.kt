package com.example.recipeapp.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recipeapp.settings.components.SettingItem

@Composable
fun NotificationSettingsView(viewModel: SettingsViewModel) {
    val notificationState = viewModel.notificationsState

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        item {
            Text(
                text = "Enable notifications to receive alerts when your products are about to expire",
                style = MaterialTheme.typography.bodySmall
            )
        }
        item {
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
        }
        item {
            SettingItem(
                name = "Activate notifications",
                icon = {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "General settings"
                    )
                },
                input = {
                    Switch(
                        checked = notificationState,
                        onCheckedChange = { viewModel.setNotificationState(!notificationState) }
                    )
                },
                onClick = { viewModel.setNotificationState(!notificationState) },
            )
        }
        item {
            SettingItem(
                name = "Test notification",
                icon = {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Appearance settings"
                    )
                },
                onClick = { viewModel.testNotification() },
                enabled = notificationState
            )
        }
    }

//        val intent = Intent(context, NotificationService::class.java)
//        context.startService(intent)
}

