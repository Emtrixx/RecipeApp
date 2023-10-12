package com.example.recipeapp.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

// Settings DataStore keys

val NOTIFICATIONS_STATE = booleanPreferencesKey("notifications_state")
val DARK_MODE = stringPreferencesKey("dark_mode")