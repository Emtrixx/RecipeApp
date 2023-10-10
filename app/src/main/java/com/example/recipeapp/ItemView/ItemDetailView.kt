package com.example.recipeapp.ItemView

import Database.Product
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.recipeapp.HomeView.HomeViewModel
import com.example.recipeapp.Navigation.NavGraph
import com.example.recipeapp.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemDetailView(product: Product) {

    val context = LocalContext.current
    val navController = rememberNavController()
    val viewModel = HomeViewModel(context)
    val productList by viewModel.getProductsLiveData().observeAsState(emptyList())
    val recipeList by viewModel.getRecipesLiveData().observeAsState(emptyList())

    NavGraph(navController = navController, productList = productList, recipeList = recipeList)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemView(product: Product, navController: NavController) {

    val context = LocalContext.current
    val viewModel = HomeViewModel(context)

    viewModel.getProductImage(product, context)
    val storedImage = viewModel.storedImage

    val painter = if (storedImage != null) {
        BitmapPainter(storedImage.asImageBitmap())
    } else {
        painterResource(R.drawable.placeholder)
    }

    val barcodeImage = viewModel.generateBarcodeImage(product.barcode)

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        stickyHeader {
            ElevatedCard(
                modifier = Modifier,
                shape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 24.dp
                ),
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Item Name
                    Text(
                        text = product.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Box(
                        Modifier.wrapContentSize(Alignment.TopEnd)
                    ) {
                        IconButton(onClick = { navController.navigate("add?barcode=${product.barcode}?edit=${true}") }) {
                            Icon(
                                Icons.Filled.Edit, contentDescription = "Edit amount"
                            )
                        }
                    }
                }
            }
        }
        // Item Image
        item {
            Box() {
                Image(
                    painter = painter,
                    contentDescription = "Product image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                )
            }
        }


        item {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // function buttons
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                "${product.name} added to shopping list",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.addToShoppingList(product)
                            navController.navigate("shoppingList")
                        },
                        Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .weight(1f),
                    ) {
                        Icon(
                            Icons.Filled.ShoppingCart,
                            contentDescription = "Shopping list",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = {},
                        Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .weight(1f),
                    ) {
                        Icon(
                            Icons.Filled.Menu,
                            contentDescription = "Shopping list",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.removeProduct(product)
                            navController.navigate("home")
                        },
                        Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .weight(1f),
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Shopping list",
                            tint = Color.Red
                        )
                    }
                }
                // Item Description
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Text(text = "Description", fontWeight = FontWeight.Bold)
                        Text(
                            text = product.description,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    // Amount
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(text = "Amount", fontWeight = FontWeight.Bold)
                            Text(
                                text = "${product.amount.toInt()}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(text = "Tags", fontWeight = FontWeight.Bold)
                            if (product.tags.isNullOrEmpty()) {
                                Text(
                                    text = "No tags",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontStyle = FontStyle.Italic,
                                    modifier = Modifier
                                )
                            } else {
                                Log.d("TAGS", "${product.tags}")
                                Text(
                                    text = product.tags.joinToString(", "),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }
                // List of dates
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Text(text = "Current dates in your fridge", fontWeight = FontWeight.Bold)

                        if (product.bestbefore.all { it.toString() == "null" }) {

                            Text(
                                text = "This product doesn't have expiry dates",
                                fontStyle = FontStyle.Italic
                            )
                        } else {
                            for (date in product.bestbefore.filterNotNull()) {
                                Text(
                                    text = date.toString(),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }
                    }
                }
                if (barcodeImage != null) {
                    Card(
                        Modifier
                            .padding(4.dp)
                            .fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "Scanned Barcode",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Image(
                                bitmap = barcodeImage.asImageBitmap(),
                                contentDescription = "Barcode",
                                modifier = Modifier.size(128.dp)
                            )
                            Text(
                                text = product.barcode,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}
