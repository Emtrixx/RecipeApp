package com.example.recipeapp.HomeView

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme.colors
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recipeapp.Navigation.BottomNavGraph
import com.example.recipeapp.Navigation.BottomNavigationBar
import com.example.recipeapp.R

data class Item(val name: String, val quantity: String, val expiryDate: String)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        content = {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeListView()
                }
            }
        }
    )
}

@Composable
fun HomeListView() {

    val itemList = listOf(
        Item(name = "Egg", quantity = "7 qty", expiryDate = "19.9.2023"),
        Item(name = "Egg", quantity = "7 qty", expiryDate = "19.9.2023"),
        Item(name = "Egg", quantity = "7 qty", expiryDate = "19.9.2023")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            item {
                Text(text = "In your fridge")
            }
            items(itemList) { item ->
                ItemCard(
                    itemName = item.name,
                    itemQuantity = item.quantity,
                    expiryDate = item.expiryDate
                )
            }
            item {
                Text(text = "expiring soon")
            }
            items(itemList) { item ->
                ExpiringItemCard(
                    itemName = item.name,
                    itemQuantity = item.quantity,
                    expiryDate = item.expiryDate
                )
            }
        }
    }
}
@Composable
fun ItemCard(
    itemName: String,
    itemQuantity: String,
    expiryDate: String,
) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.egg), // Replace with your image resource
                contentDescription = "Product image",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply{setToScale(0.5f,0.5f,0.5f,1f)}),
                modifier = Modifier
                    .height(200.dp)
                    .blur(
                        radiusX = 5.dp,
                        radiusY = 5.dp,
                    )
            )

            // Card Content
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = itemName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                }

                Text(
                    text = itemQuantity,
                    fontSize = 16.sp,
                    color = Color.White
                )

                Text(
                    text = "Expiring in $expiryDate",
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .padding(end = 4.dp), // Add padding here
                        colors = ButtonDefaults.buttonColors(
                            colors.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Edit")
                    }

                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .padding(start = 4.dp), // Add padding here
                        colors = ButtonDefaults.buttonColors(
                            colors.error,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Remove")
                    }
                }
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