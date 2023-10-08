package com.example.recipeapp.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun SettingsPage(navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        items(getSettingsItems()) { item ->
            SettingItem(name = item.name, icon = item.icon) {
                navController.navigate("settings/${item.name}")
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun SettingItem(name: String, icon: @Composable () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        icon()
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = name, style = MaterialTheme.typography.headlineSmall)
    }
}

data class Setting(val name: String, val icon: @Composable () -> Unit)

fun getSettingsItems(): List<Setting> {
    return listOf(
        Setting("General") {
            Icon(
                Icons.Default.Settings,
                contentDescription = "General settings"
            )
        },
        Setting("Appearance") {
            Icon(
                Icons.Default.Star,
                contentDescription = "Appearance settings"
            )
        },
        Setting("Notifications") {
            Icon(
                Icons.Default.Notifications,
                contentDescription = "Notification settings"
            )
        },
        Setting("Dev") { Icon(Icons.Default.Build, contentDescription = "Developer settings") },
    )
}

@Preview
@Composable
fun SettingsPagePreview() {
    val navController = rememberNavController()
    SettingsPage(navController)
}

