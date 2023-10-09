package com.example.recipeapp.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
            NotificationSettingItem(
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
            NotificationSettingItem(
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

@Composable
fun NotificationSettingItem(
    name: String,
    icon: @Composable () -> Unit,
    input: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val background = if (enabled) {
        Color.Transparent
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    if (enabled) {
                        onClick()
                    }
                }),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Row {
                icon()
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = name, style = MaterialTheme.typography.headlineSmall)
            }
            Spacer(modifier = Modifier.width(16.dp))
            input?.invoke()
        }
        Spacer(
            modifier = Modifier.height(8.dp),
        )
    }
}