package com.example.recipeapp.shopping

import Database.ShoppingItem
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recipeapp.R


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ShoppingList() {
    val viewModel: ShoppingViewModel = viewModel()
    val navController = rememberNavController()
    val shoppingList by viewModel.getShoppingListLiveData().observeAsState(emptyList())

    val onDeleteItem: (ShoppingItem) -> Unit = { item ->
        viewModel.deleteShoppingItem(item)
    }

    // Fetch the shopping list data when the composable is first displayed
    LaunchedEffect(Unit) {
        viewModel.fetchShoppingList()
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.bread
                    ),
                    contentDescription = "TopAppBar background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
                TopAppBar(
                    title = {
                        Text(
                            text = "Shopping List",
                            style = TextStyle(
                                fontSize = 26.sp
                            ),
                            color = Color.White
                        )
                    },
                    actions = {
                        FilledTonalIconButton(
                            colors = IconButtonDefaults.filledIconButtonColors(MaterialTheme.colorScheme.onSurfaceVariant),
                            onClick = {
                                navController.navigate("shoppingForm")
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                imageVector = Icons.Filled.Add,
                                tint = Color.Black,
                                contentDescription = "Add button icon"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        content = {
            // Nav routes
            NavHost(navController, startDestination = "shoppingList") {
                composable("shoppingList") {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        if (shoppingList.isEmpty()) {
                            Text(
                                text = "The shopping list is empty. Add something from top right corner",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 300.dp),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(top = 65.dp),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                items(shoppingList) { shoppingItem ->
                                    ShoppingCard(
                                        item = shoppingItem,
                                        onDeleteItem = onDeleteItem
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
                composable("shoppingForm") {
                    ShoppingForm { newItemName, newItemAmount ->

                        viewModel.addShoppingItem(newItemName, newItemAmount)

                        navController.popBackStack()
                    }
                }
            }
        }
    )
}

@Composable
fun ShoppingCard(item: ShoppingItem, onDeleteItem: (ShoppingItem) -> Unit) {
    val (checkedState, onStateChange) = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .toggleable(
                    value = checkedState,
                    onValueChange = { onStateChange(!checkedState) },
                    role = Role.Checkbox
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checkedState,
                onCheckedChange = null
            )
            Text(
                text = item.name,
                modifier = Modifier
                    .padding(start = 16.dp),
                color = if (checkedState) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "qty: ${item.amount}",
                modifier = Modifier
                    .padding(start = 16.dp),
                color = if (checkedState) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurfaceVariant
            )
            IconButton(
                onClick = { onDeleteItem(item) },
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp),
                ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red)

            }
        }
    }
}
