package com.example.recipeapp.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import com.example.recipeapp.settings.components.SettingItem
import com.example.recipeapp.settings.components.SettingsLayout

@Composable
fun GeneralSettings() {
    SettingsLayout(
        "General", "General Settings for the app",
        { SettingItem(name = "About", icon = { Icons.Default.Info }, onClick = { }) },
    )
}