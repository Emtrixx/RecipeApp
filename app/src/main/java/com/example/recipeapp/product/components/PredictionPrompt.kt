package com.example.recipeapp.product.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun PredictionDialog(
    predictedName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        title = { Text(text = "Product prediction", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        text = {
            Text("Is the predicted name $predictedName correct?", color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("No")
            }
        }
    )
}
