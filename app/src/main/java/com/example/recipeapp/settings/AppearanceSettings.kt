package com.example.recipeapp.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.recipeapp.MainActivity
import com.example.recipeapp.R
import com.example.recipeapp.settings.components.SettingItem
import com.example.recipeapp.settings.components.SettingsLayout

@Composable
fun AppearanceSettings(viewModel: SettingsViewModel) {
    val context = LocalContext.current

    val isDarkModeEnabled =
        viewModel.darkModeState?.let { it == DarkModeState.DARK.value } ?: isSystemInDarkTheme()
    val onChangeValue =
        if (isDarkModeEnabled) DarkModeState.LIGHT.value else DarkModeState.DARK.value

    SettingsLayout(
        "Appearance", "Appearance Settings for the app",
        {
            SettingItem(
                name = "Dark Mode",
                icon = {
                    Icon(
                        Icons.Default.Face,
                        contentDescription = "Theme settings"
                    )
                },
                onClick = {
                    context.setTheme(R.style.Theme_RecipeApp)
                },
                input = {
                    Switch(
                        checked = isDarkModeEnabled,
                        onCheckedChange = {
                            viewModel.setDarkModeState(
                                context as MainActivity,
                                onChangeValue
                            )
                        }
                    )
                },
            )
        },
    )
}