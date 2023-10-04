package com.example.recipeapp.components

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun IntegerInputStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = Int.MIN_VALUE,
    maxValue: Int = Int.MAX_VALUE
) {
    Surface(
//        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (value > minValue) {
                        onValueChange(value - 1)
                    }
                },
                modifier = Modifier.width(60.dp).height(IntrinsicSize.Max),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp)
            ) {
                Text("-")
            }

            OutlinedTextField(
                value = value.toString(),
                onValueChange = {
                    val newValue = it.toIntOrNull()
                    if (newValue != null && newValue in minValue..maxValue) {
                        onValueChange(newValue)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                maxLines = 1,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                )
            )

            Button(
                onClick = {
                    if (value < maxValue) {
                        onValueChange(value + 1)
                    }
                },
                modifier = Modifier.width(60.dp).height(IntrinsicSize.Max),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(0.dp, 8.dp, 8.dp, 0.dp)
            ) {
                Text("+")
            }
        }
    }
}

@Preview
@Composable
fun IntegerInputStepperPreview() {
    MaterialTheme {
        IntegerInputStepper(value = 5, onValueChange = {})
    }
}
