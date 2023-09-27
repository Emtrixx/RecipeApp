package com.example.recipeapp.HomeView

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.recipeapp.R

data class Item(val name: String, val quantity: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView() {

    val itemList = listOf(
        Item(name = "Egg", quantity = "7 qty"),
        Item(name = "Milk", quantity = "2 liters"),
        Item(name = "Milk", quantity = "2 liters"),
        Item(name = "Milk", quantity = "2 liters"),
        Item(name = "Milk", quantity = "2 liters"),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "RecipeApp",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
            )
        },
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(itemList) { item ->
                    ItemCard(itemName = item.name, itemQuantity = item.quantity)
                }
            }
            Text(text = "expiring soon")
        }
    )
}

@Composable
fun ItemCard(itemName: String, itemQuantity: String) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier
            .padding(12.dp)
    ) {
        Column {
            Column(modifier = Modifier.padding(all = 8.dp)) {
                Text(text = itemName, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = itemQuantity)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(R.drawable.egg),
                contentDescription = "Product image",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
            )
        }
    }
}