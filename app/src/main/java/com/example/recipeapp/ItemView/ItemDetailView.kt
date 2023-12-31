package com.example.recipeapp.ItemView

import Database.Product
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.recipeapp.HomeView.HomeViewModel
import com.example.recipeapp.Navigation.NavGraph
import com.example.recipeapp.R
import java.time.format.DateTimeFormatter

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

    val openDialog = remember { mutableStateOf(false) }

    val onDeleteItem: (Product) -> Unit = { item ->
        viewModel.removeProduct(product = item)
    }

    val allDatesNoRepetition = product.bestbefore.filterNotNull().distinct().sorted()

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
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp))
                    .shadow(elevation = 12.dp)
            ) {
                Image(
                    painter = painter,
                    contentDescription = "Product image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
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
                    // add to shopping list button
                    Button(
                        onClick = {
                            Toast.makeText(
                                context,
                                "${product.name} added to shopping list",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.addToShoppingList(product)
                            navController.navigate("shoppingList")
                        },
                        Modifier.weight(1f),
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(75.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.ShoppingCart,
                                contentDescription = "Shopping list",
                                tint = Color.White
                            )
                            Text(
                                text = "Shopping list",
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    // generate a recipe button
                    Button(
                        onClick = {
                            navController.navigate("recipe")
                        },
                        Modifier.weight(1f),
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(75.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = "Generate recipe",
                                tint = Color.White
                            )
                            Text(text = "Recipe", fontSize = 10.sp)
                        }
                    }
                    //delete button
                    Button(
                        onClick = {
                            openDialog.value = true
                        },
                        Modifier.weight(1f),
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(75.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                            Text(text = "Delete", fontSize = 10.sp, color = Color.Red)
                        }
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
                        if (product.description.isEmpty()) {
                            Text(
                                text = "No description",
                                fontStyle = FontStyle.Italic
                            )
                        } else {
                            Text(
                                text = product.description,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    // Amount
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .fillMaxHeight(),
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxHeight(),
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
                            if (product.tags.isEmpty()) {
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
                        Text(
                            text = "Current dates in your fridge",
                            fontWeight = FontWeight.Bold
                        )

                        if (allDatesNoRepetition.isEmpty()) {
                            Text(
                                text = "This product doesn't have expiry dates",
                                fontStyle = FontStyle.Italic
                            )
                        } else {
                            for (date in allDatesNoRepetition) {
                                Text(
                                    text = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }
                    }
                }
                // Carbon footprint
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Text(text = "Carbon footprint", fontWeight = FontWeight.Bold)
                        if (product.carbonFootprint == null) {
                            Text(
                                text = "No carbon footprint data available",
                                fontStyle = FontStyle.Italic
                            )
                        } else {
                            Text(
                                text = product.carbonFootprint,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                }
                // Barcode image
                if (barcodeImage != null) {
                    Card(
                        Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                    ) {
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
                if (openDialog.value) {
                    AlertDialog(
                        onDismissRequest = {
                            openDialog.value = false
                        },
                        title = {
                            Text(
                                text = "Are you sure you want to delete this item?",
                                color = Color.Black
                            )
                        },
                        text = {
                            Text(
                                "This will remove ${product.name} permanently",
                                color = Color.Black
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    openDialog.value = false
                                    onDeleteItem(product)
                                    navController.navigate("home")
                                },
                                colors = ButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = Color.White,
                                    disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                                    disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                                ),
                            ) {
                                Text("Confirm", color = Color.White)
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    openDialog.value = false
                                },
                                colors = ButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.Black,
                                    disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                                    disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                                ),
                                border = BorderStroke(
                                    2.dp,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Text("Cancel", color = Color.Black)
                            }
                        }
                    )
                }
            }
        }
    }
}

