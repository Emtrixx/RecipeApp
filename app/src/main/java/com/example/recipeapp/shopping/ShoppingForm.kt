package com.example.recipeapp.shopping

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ExposedDropdownMenuBox
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ShoppingForm(
    onItemAdded: (String, String) -> Unit
) {
    var newItem by remember { mutableStateOf("") }
    var newAmount by remember { mutableStateOf("") }

    var isItemInvalid by remember { mutableStateOf(false) }
    var isAmountInvalid by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var shownAmountType by remember { mutableStateOf("Add") }
    var selectedAmountType by remember { mutableStateOf("") }

    val amountTypes = listOf(" kg", " g", " mg", " l", " dl", " pcs", " pkg")
    val viewModel: ShoppingViewModel = viewModel()
    val favorites by viewModel.getFavoriteItems().observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.fetchShoppingList()
        viewModel.fetchFavoriteItems()
    }

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

            // TextField for adding product name to ShoppingList
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

            // Error handling if name is something else than String
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

            Row(modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically) {

                // TextField for adding the amount of items to ShoppingList
                TextField(
                    modifier = Modifier.weight(1f),
                    value = newAmount,
                    onValueChange = { newAmount = it },
                    placeholder = { Text(text = "e.g. 2") },
                    isError = isAmountInvalid,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(32.dp, 4.dp, 4.dp, 32.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorContainerColor = MaterialTheme.colorScheme.errorContainer,
                        errorIndicatorColor = Color.Transparent
                    )
                )



                // Type of amount
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(0.dp, 32.dp, 32.dp, 0.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ) {
                        OutlinedTextField(
                            value = shownAmountType,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { Icon(imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "dropDown")
                            },
                            colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTextColor = Color.Transparent,
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )

                        ExposedDropdownMenu(
                            modifier = Modifier.height(250.dp),
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            amountTypes.forEach { amountType ->
                                DropdownMenuItem(
                                    text = { Text(text = amountType, color = Color.Black) },
                                    onClick = {
                                        selectedAmountType = amountType
                                        shownAmountType = selectedAmountType
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Error handling for amount
             if (isAmountInvalid) {
                 Text(
                    text = "Invalid amount. Please enter a valid number.",
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Button for adding items to list
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

                        // Checks that the user puts required values
                        if (newItem.isNotBlank() && hasNonNumeric && amountValue != null) {
                            viewModel.incrementAddedCount(newItem)

                            onItemAdded(newItem, StringBuilder().append(amountValue).append(selectedAmountType).toString())

                            newItem = "" // Set empty field
                            newAmount = "" // Set empty field
                            isItemInvalid = false // Reset item name error
                            isAmountInvalid = false // Reset amount error
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
            ShoppingFavorites(favorites)
        }
    }



