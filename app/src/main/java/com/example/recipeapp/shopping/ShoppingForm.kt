package com.example.recipeapp.shopping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingForm(
    onItemAdded: (String, Int) -> Unit
) {
    var newItem by remember { mutableStateOf("") }
    var newAmount by remember { mutableStateOf("") }

    var isItemInvalid by remember { mutableStateOf(false) }
    var isAmountInvalid by remember { mutableStateOf(false) }


        Column(
            modifier = Modifier
                .padding(16.dp, 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.padding(top = 65.dp))

            Text(
                text = "Item name",
                style = MaterialTheme.typography.bodyLarge
            )

            //TextField for adding items to ShoppingList
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = newItem,
                onValueChange = {
                    newItem = it
                },
                placeholder = { Text(text = "e.g. Milk") },
                isError = isItemInvalid,
                singleLine = true,
                shape = CircleShape,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorContainerColor = MaterialTheme.colorScheme.errorContainer,
                    errorIndicatorColor = Color.Transparent
                )
            )

            //Error handling
            if (isItemInvalid) {
                Text(
                    text = "Invalid item. Please enter a valid item name.",
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Text(
                text = "Quantity",
                style = MaterialTheme.typography.bodyLarge
            )

            //TextField for adding the amount of items to ShoppingList
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = newAmount,
                onValueChange = { newAmount = it },
                placeholder = { Text(text = "e.g. 2") },
                isError = isAmountInvalid,
                singleLine = true,
                shape = CircleShape,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorContainerColor = MaterialTheme.colorScheme.errorContainer,
                    errorIndicatorColor = Color.Transparent
                )
            )

            //Error handling
            if (isAmountInvalid) {
                Text(
                    text = "Invalid amount. Please enter a valid number.",
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }


            //Button for adding items to list
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Spacer(modifier = Modifier.fillMaxWidth())

                Button(
                    onClick = {
                        val amountValue = newAmount.toIntOrNull()
                        val hasNonNumeric = newItem.any { it.isLetter() }

                        //Checks that the user puts required values
                        if (newItem.isNotBlank() && hasNonNumeric && amountValue != null) {

                            onItemAdded(newItem, amountValue)

                            newItem = ""
                            newAmount = ""
                            isItemInvalid = false // Reset item name error flag
                            isAmountInvalid = false // Reset amount error flag
                        } else {
                            isItemInvalid = newItem.isBlank() || !hasNonNumeric
                            isAmountInvalid = amountValue == null
                        }
                    },
                    modifier = Modifier,
                    shape = RoundedCornerShape(50),
                    colors = ButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = Color.White,
                        disabledContentColor = Color.White,
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Text (text = "Save")
                }
            }
        }
    }

