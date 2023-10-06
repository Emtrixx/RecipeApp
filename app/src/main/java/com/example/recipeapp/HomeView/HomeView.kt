package com.example.recipeapp.HomeView

import Database.Product
import Database.ShoppingItem
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.recipeapp.Navigation.NavGraph
import com.example.recipeapp.Navigation.BottomNavigationBar
import com.example.recipeapp.R
import com.example.recipeapp.components.camera.getImageFromInternalStorage
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
        //homeViewModel.addProduct()
    }

    val topAppBarTitle =
        when (navController.currentBackStackEntryAsState().value?.destination?.route) {
            "home" -> "Home"
            "itemDetail/{itemId}" -> "Product"
            "recipe" -> "Recipes"
            "settings" -> "Settings"
            "allItems" -> "Your Items"
            "shoppingList" -> "hide"
            "add?barcode={barcode}" -> "hide"
            else -> "hide"
        }

    Scaffold(
        modifier = Modifier
            //.nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        topBar = {
            if (topAppBarTitle != "hide") {
                TopAppBar(
                    title = { Text(text = topAppBarTitle) },
                    //scrollBehavior = scrollBehavior,
                )
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            NavGraph(navController = navController, filteredProducts)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeListView(productList: List<Product>?, navController: NavController) {

    val context = LocalContext.current
    val homeViewModel = HomeViewModel(context)

    val onDeleteItem: (Product) -> Unit = { item ->
        homeViewModel.removeProduct(item.barcode)
    }

    if (productList.isNullOrEmpty()) {
        Box(
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "You don't have any products saved yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier,
                    fontStyle = FontStyle.Italic

                )
                ClickableText(
                    text = AnnotatedString("Click here to add your first one"),
                    modifier = Modifier.padding(8.dp),
                    onClick = { offset ->
                        if (offset in 0..21) {
                            navController.navigate("scanner")
                        }
                    },
                    style = TextStyle(
                        color = Color.Blue,
                    )
                )
            }
        }
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
                stickyHeader {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Time to get more",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .padding(4.dp)
                        )
                        Button(
                            onClick = { navController.navigate("allItems") },
                            modifier = Modifier
                                .padding(4.dp),
                            colors = ButtonColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                                disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                        ) {
                            Text("See all (21)")
                        }
                    }
                }
                items(productList) { item ->
                    ItemCard(
                        product = item,
                        navController = navController,
                        onDeleteItem = onDeleteItem
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
    navController: NavController,
    onDeleteItem: (Product) -> Unit
) {
    val context = LocalContext.current
    val homeViewModel = HomeViewModel(context)
    //val dateFormatter = SimpleDateFormat("dd.MM", Locale.getDefault())
    val openDialog = remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }

    homeViewModel.getProductImage(product, context)

    val storedImage = homeViewModel.storedImage

    val painter = if (storedImage != null) {
        BitmapPainter(storedImage.asImageBitmap())
    } else {
        painterResource(id = R.drawable.egg)
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("itemDetail/${product.barcode}")
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        ),
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
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                    onDeleteItem(product)
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
                                    navController.navigate("recipe")
                                },
                                content = {
                                    Text(
                                        text = "Create a recipe",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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

                                    homeViewModel.addToShoppingList(product)
                                },
                                content = {
                                    Text(
                                        text = "Add to shopping list",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                painter = painter,
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
