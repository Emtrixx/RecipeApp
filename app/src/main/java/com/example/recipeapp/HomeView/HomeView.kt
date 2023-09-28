package com.example.recipeapp.HomeView

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.recipeapp.Navigation.BottomNavGraph
import com.example.recipeapp.Navigation.ShitPissBar
import com.example.recipeapp.R

data class Item(val name: String, val quantity: String, val expiryDate: String)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView() {

    val navController = rememberNavController()

    val itemList = listOf(
        Item(name = "Egg", quantity = "7 qty", expiryDate = "19.9.2023"),
        Item(name = "Egg", quantity = "7 qty", expiryDate = "19.9.2023"),
        Item(name = "Egg", quantity = "7 qty", expiryDate = "19.9.2023"),
        Item(name = "Egg", quantity = "7 qty", expiryDate = "19.9.2023"),
        Item(name = "Egg", quantity = "7 qty", expiryDate = "19.9.2023"),
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // First LazyColumn, filling half of the screen
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    items(itemList) { item ->
                        ItemCard(itemName = item.name,
                            itemQuantity = item.quantity,
                            expiryDate =  item.expiryDate)
                    }
                    item {
                        Text(text = "expiring soon")
                    }
                    items(itemList) { item ->
                        ExpiringItemCard(itemName = item.name,
                            itemQuantity = item.quantity,
                            expiryDate =  item.expiryDate)
                    }
                }
            }
            BottomNavGraph(navController = navController)
        }, bottomBar = {
            ShitPissBar(navController = navController)
        }
    )
}

@Composable
fun ItemCard(itemName: String, itemQuantity: String, expiryDate: String) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier
            .padding(12.dp)
    ) {
        Row (modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween){
            Column(modifier = Modifier.padding(all = 8.dp)) {
                Text(text = itemName, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = itemQuantity)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Expiring in $expiryDate", modifier = Modifier
                    .padding(10.dp))
            }
            Card(modifier = Modifier
                .padding(8.dp)
                .widthIn(0.dp, 100.dp)) {
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
}

@Composable
fun ExpiringItemCard(itemName: String, itemQuantity: String, expiryDate: String) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = Color.Red,
        ),
        modifier = Modifier
            .padding(12.dp)
    ) {
        Column {
            Column(modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth()) {
                Text(text = itemName, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = itemQuantity, fontSize = 12.sp)
                Text(text = expiryDate)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}