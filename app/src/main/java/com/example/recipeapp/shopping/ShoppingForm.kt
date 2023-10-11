package com.example.recipeapp.shopping

import Database.FavoriteShoppingItem
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingForm(
    onItemAdded: (String, Int) -> Unit
) {
    var newItem by remember { mutableStateOf("") }
    var newAmount by remember { mutableStateOf("") }

    var isItemInvalid by remember { mutableStateOf(false) }
    var isAmountInvalid by remember { mutableStateOf(false) }

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
                            viewModel.incrementAddedCount(newItem)

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
            ShoppingFavorites(favorites)
        }
    }

@Composable
fun ShoppingFavorites(
    favoriteItems: List<FavoriteShoppingItem>
) {
    val context = LocalContext.current
    val viewModel: ShoppingViewModel = viewModel()
    var showDialog by remember { mutableStateOf(false) }
    var selectedFavoriteItem by remember { mutableStateOf<FavoriteShoppingItem?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(2.dp, Color.LightGray)
    ) {
        LazyColumn(
            modifier = Modifier
                .height(200.dp)
        ) {
            item {
                Text(
                    text = "Your Favorites:",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .border(2.dp, Color.LightGray)
                        .padding(top = 8.dp)
                )
            }
            // Shows text if there's no favorites
            if (favoriteItems.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Nothing here yet.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp)
                        )
                        Text(
                            text = "This is automatically generated when you have added an item enough times",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            } else {
                // Shows the favorite items
                items(favoriteItems) { favoriteItem ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                selectedFavoriteItem = favoriteItem
                                showDialog = true
                            }
                    ) {
                        Row {
                            Icon(
                                Icons.Filled.ShoppingCart,
                                contentDescription = "Shopping list",
                                tint = Color.Black
                            )
                            Text(
                                text = favoriteItem.name,
                                modifier = Modifier
                                    .padding(2.dp)
                            )
                        }
                    }
                }
            }
        }
        // Shows a dialog when favorite item is clicked
        if (showDialog && selectedFavoriteItem != null) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    selectedFavoriteItem = null
                },
                title = {
                    Text(
                        text = "Add to Shopping List",
                        style = MaterialTheme.typography.bodyLarge)
                },
                text = {
                    Text(
                        text = "Do you want to add ${selectedFavoriteItem?.name} to your shopping list?",
                        color = Color.Black)
                },
                confirmButton = {
                    Button(
                        colors = ButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = Color.White,
                            disabledContentColor = Color.White,
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer),
                        onClick = {
                            showDialog = false
                            selectedFavoriteItem?.let { item ->
                                viewModel.addFavoriteToShoppingList(item)

                                val productName = item.name
                                val message = "Added $productName to the shopping list"
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                            selectedFavoriteItem = null
                        }
                    ) {
                        Text(text = "Add")
                    }
                },
                dismissButton = {
                    Button(
                        colors = ButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = Color.White,
                            disabledContentColor = Color.White,
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer),
                        onClick = {
                            showDialog = false
                            selectedFavoriteItem = null
                        }
                    ) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    }
}




