package com.example.recipeapp.HomeView

import Database.Product
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recipeapp.ItemView.ItemDetailView
import com.example.recipeapp.Navigation.BottomNavGraph
import com.example.recipeapp.Navigation.BottomNavigationBar
import com.example.recipeapp.R
import java.util.Date


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView() {
    val navController = rememberNavController()

    val homeViewModel: HomeViewModel = viewModel()

    val productList: List<Product>? by homeViewModel.getProductsLiveData().observeAsState()

    LaunchedEffect(Unit) {
        homeViewModel.fetchProducts()
        homeViewModel.addProduct()
    }

    // Set up the navigation route
    NavHost(navController, startDestination = "home") {
        composable("home") {
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(navController = navController)
                },
                content = {
                    HomeListView(productList = productList, navController = navController)
                }
            )
        }
        composable(
            route = "itemDetail/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Extract the itemId from the route and find the corresponding item
            val itemId = backStackEntry.arguments?.getString("itemId")
            val selectedItem = productList?.find { it.barcode == itemId }

            if (selectedItem != null) {
                ItemDetailView(item = selectedItem)
            } else {
                Text("Item not found")
            }
        }
    }
}

@Composable
fun HomeListView(productList : List<Product>?, navController: NavController) {

    if(productList.isNullOrEmpty()) {
        Text(text = "No products in your fridge yet")
    } else {
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
                    Text(
                        text = "In your Fridge",
                        fontSize = 24.sp,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                    Divider(thickness = 1.dp, color = Color.White)
                }
                items(productList) { item ->
                    ItemCard(
                        product = item,
                        navController = navController
                    )
                }
                item {
                    Button(
                        onClick = {/* TODO */ },
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Text("See all (21)")
                    }
                }
                item {
                    Text(
                        text = "expiring soon",
                        fontSize = 24.sp,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
                items(productList) { item ->
                    ExpiringItemCard(
                        itemName = item.name,
                        itemQuantity = item.amount,
                        expiryDate = item.bestbefore
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }
}

@Composable
fun ItemCard(
    product: Product,
    navController : NavController) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("itemDetail/${product.barcode}")
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.egg),
                contentDescription = "Product image",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply {
                    setToScale(
                        0.5f,
                        0.5f,
                        0.5f,
                        1f
                    )
                }),
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
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                }

                Text(
                    text = product.amount.toString(),
                    fontSize = 16.sp,
                    color = Color.White
                )

                Text(
                    text = "Expiring in ${product.bestbefore}",
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
                            colors.primary,
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
fun ExpiringItemCard(itemName: String, itemQuantity: Double, expiryDate: Date) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF4444),
        ),
        modifier = Modifier
            .padding(12.dp)
    ) {
        Column {
            Column(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = itemName, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = itemQuantity.toString(), fontSize = 12.sp)
                Text(text = expiryDate.toString())
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}