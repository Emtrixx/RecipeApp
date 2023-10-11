package com.example.recipeapp.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SettingItem(
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
            verticalAlignment = Alignment.CenterVertically,
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