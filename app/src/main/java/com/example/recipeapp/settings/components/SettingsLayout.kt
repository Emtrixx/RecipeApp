package com.example.recipeapp.settings.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsLayout(title: String, description: String ,vararg composables: @Composable () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        item {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
        item {
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
        }
        items(composables.size) { index ->
            composables[index].invoke()
        }
    }
}