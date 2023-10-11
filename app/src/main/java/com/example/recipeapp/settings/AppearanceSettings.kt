package com.example.recipeapp.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.example.recipeapp.settings.components.SettingItem
import com.example.recipeapp.settings.components.SettingsLayout

@Composable
fun AppearanceSettings() {
    SettingsLayout(
        "Appearance", "Appearance Settings for the app",
        {
            SettingItem(name = "Theme", icon = {
                Icon(
                    Icons.Default.Face,
                    contentDescription = "Theme settings"
                )
            }, onClick = { })
        },
    )
}