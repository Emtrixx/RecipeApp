package com.example.recipeapp.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.recipeapp.settings.components.SettingLink
import com.example.recipeapp.settings.components.SettingLinkItem

@Composable
fun SettingsPage(navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        item {
            Text(
                text = "Manage your settings here",
                style = MaterialTheme.typography.bodySmall
            )
        }
        item {
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
        }
        items(getSettingsItems()) { item ->
            SettingLinkItem(name = item.name, icon = item.icon) {
                navController.navigate("settings/${item.name}")
            }
            Spacer(
                modifier = Modifier.height(8.dp),
            )
        }
    }
}

private fun getSettingsItems(): List<SettingLink> {
    return listOf(
        SettingLink("General") {
            Icon(
                Icons.Default.Settings,
                contentDescription = "General settings"
            )
        },
        SettingLink("Appearance") {
            Icon(
                Icons.Default.Star,
                contentDescription = "Appearance settings"
            )
        },
        SettingLink("Notifications") {
            Icon(
                Icons.Default.Notifications,
                contentDescription = "Notification settings"
            )
        },
        SettingLink("Dev") { Icon(Icons.Default.Build, contentDescription = "Developer settings") },
    )
}

@Preview
@Composable
fun SettingsPagePreview() {
    val navController = rememberNavController()
    SettingsPage(navController)
}

