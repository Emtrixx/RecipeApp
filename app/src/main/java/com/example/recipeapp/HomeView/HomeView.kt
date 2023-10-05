package com.example.recipeapp.HomeView

import Database.Product
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.recipeapp.Navigation.NavGraph
import com.example.recipeapp.Navigation.BottomNavigationBar
import com.example.recipeapp.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView() {
    val navController = rememberNavController()

    val context = LocalContext.current

    val homeViewModel = HomeViewModel(context)

    val productList by homeViewModel.getProductsLiveData().observeAsState(emptyList())

    var filteredProducts = productList.take(3)

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(Unit) {
        homeViewModel.getProductsLiveData()
        homeViewModel.addProduct()
    }

    val topAppBarTitle = when (navController.currentBackStackEntryAsState().value?.destination?.route) {
        "home" -> "Home"
        "itemDetail/{itemId}" -> "Product"
        "add?barcode={barcode}" -> "Scan item"
        "recipe" -> "Recipes"
        "settings" -> "Settings"
        "allItems" -> "Your Items"
        else -> "RecipeApp"
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = topAppBarTitle) },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
    ) { innerPadding ->
        Column (modifier = Modifier.padding(innerPadding)){
            NavGraph(navController = navController, filteredProducts)
        }
    }
}

@Composable
fun HomeListView(productList: List<Product>?, navController: NavController) {

    if (productList.isNullOrEmpty()) {
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Time to get more",
                            fontSize = 20.sp,
                        )
                        Button(
                            onClick = { navController.navigate("allItems") },
                            modifier = Modifier
                                .padding(4.dp)
                        ) {
                            Text("See all (21)")
                        }
                    }
                }
                items(productList) { item ->
                    ItemCard(
                        product = item,
                        navController = navController
                    )
                }
                item {
                    Text(
                        text = "Recipes",
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
            }
        }
    }
}

@Composable
fun ItemCard(
    product: Product,
    navController: NavController
) {
    val context = LocalContext.current
    val homeViewModel = HomeViewModel(context)
    //val dateFormatter = SimpleDateFormat("dd.MM", Locale.getDefault())
    val openDialog = remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }


    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("itemDetail/${product.barcode}")
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Card Content
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
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

                    Box(
                        modifier = Modifier
                            .background(
                                color = Color.Gray,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(4.dp)
                    ) {
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Best before date",
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                //text = dateFormatter.format(product.bestbefore),
                                text = product.bestbefore.toString(),
                                fontSize = 10.sp,
                                modifier = Modifier
                                    .padding(start = 4.dp),
                                color = Color.White
                            )
                        }
                    }

                    Box(
                        Modifier
                            .wrapContentSize(Alignment.TopEnd)
                    ) {
                        IconButton(
                            onClick = {
                                expanded = true
                            }
                        ) {
                            Icon(
                                Icons.Filled.MoreVert,
                                contentDescription = "More Menu"
                            )
                        }
                    }
                }

                if (openDialog.value) {
                    AlertDialog(
                        onDismissRequest = {
                            openDialog.value = false
                        },
                        title = {
                            Text(text = "Are you sure you want to delete this item?")
                        },
                        text = {
                            Text("This will remove ${product.name} permanently")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    openDialog.value = false
                                    homeViewModel.removeProduct(product.barcode)
                                }) {
                                Text("Confirm")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    openDialog.value = false
                                }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
                if (expanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.TopEnd)
                    ) {

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    expanded = false
                                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
                                },
                                content = {
                                    Text(
                                        text = "Create a recipe",
                                        color = Color.Black,
                                    )
                                }
                            )
                            DropdownMenuItem(
                                onClick = {
                                    expanded = false
                                    Toast.makeText(
                                        context,
                                        "${product.name} added to shopping list",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                content = {
                                    Text(
                                        text = "Add to shopping list",
                                        color = Color.Black,
                                    )
                                }
                            )
                            DropdownMenuItem(
                                onClick = {
                                    openDialog.value = true
                                    expanded = false
                                },
                                content = {
                                    Text(
                                        text = "Delete",
                                        color = Color.Red,
                                    )
                                }
                            )
                        }
                    }
                }

            }
            Image(
                painter = painterResource(id = R.drawable.egg),
                contentDescription = "Product image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(120.dp)
            )
        }
    }
}

@Composable
fun ExpiringItemCard(itemName: String, itemQuantity: Int, expiryDate: List<LocalDate?>) {
    val dateFormatter = SimpleDateFormat("MMM dd", Locale.getDefault())
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
                //Text(text = dateFormatter.format(expiryDate))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
