package com.example.recipeapp.shopping

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recipeapp.ui.theme.RecipeAppTheme


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ShoppingList() {
    val items = remember { mutableStateListOf<ShoppingItem>() }
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.LightGray
                ),
                title = {
                    Text(
                        "Shopping List",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("shoppingForm")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null)
                    }
                }
            )
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
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(top = 60.dp),
                            contentPadding = PaddingValues(16.dp)
                        ){
                            items(items) { shoppingItem ->
                                ShoppingCard(
                                    item = shoppingItem
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
                composable("shoppingForm") {
                    ShoppingForm(onItemAdded = { itemName, itemAmount ->
                        items.add(ShoppingItem(itemName, itemAmount))
                        navController.popBackStack()
                    })
                }
            }
        }
    )
}

@Composable
fun ShoppingCard(item: ShoppingItem) {
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
                )
        ) {
            Checkbox(
                checked = checkedState,
                onCheckedChange = null
            )
            Text(
                text = item.name,
                modifier = Modifier
                    .padding(start = 16.dp),
                color = if (checkedState) Color.Gray else Color.Black
            )
            Text(
                text = item.amount.toString(),
                modifier = Modifier
                    .padding(start = 16.dp),
                color = if (checkedState) Color.Gray else Color.Black
            )
        }
    }
}

data class ShoppingItem(
    val name: String,
    val amount: Int,
)

@Preview
@Composable
fun ShoppingListPreview() {
    RecipeAppTheme {
        ShoppingList()
    }
}