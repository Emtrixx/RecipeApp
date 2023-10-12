package com.example.recipeapp.shopping

import Database.FavoriteShoppingItem
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

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
            .border(2.dp, Color.LightGray, shape = RoundedCornerShape(16.dp))
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
                        .border(2.dp, Color.LightGray,
                            shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)
                        )
                        .padding(top = 8.dp)
                )
            }
            // Shows text if there's no favorites
            if (favoriteItems.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
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
