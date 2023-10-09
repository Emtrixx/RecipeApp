package com.example.recipeapp.AllItems

import Database.Product
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.example.recipeapp.ItemView.ItemDetailView
import com.example.recipeapp.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllItems() {
    val navController = rememberNavController()

    val context = LocalContext.current

    val allItemsViewModel = AllItemsViewModel(context)

    val productList by allItemsViewModel.getProductsLiveData().observeAsState(emptyList())

    LaunchedEffect(Unit) {
        allItemsViewModel.getProductsLiveData()
    }

    // Set up the navigation route
    NavHost(navController, startDestination = "allItemsList") {
        composable("allItemsList") {
            Scaffold(
                content = {
                    AllItemsListView(productList = productList, navController = navController)
                },
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
                ItemDetailView(product = selectedItem)
            } else {
                Text("Item not found")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun AllItemsListView(productList: List<Product>?, navController: NavController) {

    var searchText by remember { mutableStateOf("") }

    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf<String?>(null) }

    if (productList.isNullOrEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "You don't have any products saved yet.",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
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
                    color = Color.Blue, // Set the text color to blue
                )
            )
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.background),
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchText,
                                onValueChange = { newQuery ->
                                    searchText = newQuery
                                },
                                label = { Text("Search Products") },
                                modifier = Modifier
                                    //.fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .weight(1f),
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Search,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    unfocusedBorderColor = Color.White,
                                    textColor = Color.White
                                ),
                            )
                            Box(
                                modifier = Modifier
                                    .weight(0.5f)
                                    .padding(4.dp)
                            ) {
                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = {
                                        expanded = !expanded
                                    }
                                ) {
                                    OutlinedTextField(
                                        value = selectedTag ?: "Tags",
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { Icon(imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "dropDown")},
                                        shape = RoundedCornerShape(8.dp),
                                        colors = TextFieldDefaults.textFieldColors(
                                            textColor = Color.Black,
                                            disabledTextColor = Color.Transparent,
                                            backgroundColor = Color.White,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent
                                        )
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        productList.flatMap { it.tags }.distinct().forEach { tag ->
                                            DropdownMenuItem(
                                                text = { Text(text = tag, color = Color.Black) },
                                                onClick = {
                                                    selectedTag = tag
                                                    expanded = false
                                                    Toast.makeText(context, tag, Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                val filteredProductList = productList.filter { product ->
                    product.name.contains(searchText, ignoreCase = true)
                }

                val filteredProductListWithTag = if (selectedTag != null) {
                    filteredProductList.filter { product ->
                        product.tags.contains(selectedTag)
                    }
                } else {
                    filteredProductList
                }

                items(filteredProductListWithTag) { item ->
                    ItemCard(
                        product = item,
                        navController = navController
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

    val allItemsViewModel = AllItemsViewModel(context)

    allItemsViewModel.getProductImage(product, context)

    val storedImage = allItemsViewModel.storedImage

    val painter = if (storedImage != null) {
        rememberAsyncImagePainter(storedImage)
    } else {
        rememberAsyncImagePainter(R.drawable.egg)
    }

    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("itemDetail/${product.barcode}")
            }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = product.name,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(8.dp)
            )
            Image(
                painter = painter,
                contentDescription = "Product image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(75.dp)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

