package com.example.recipeapp.AllItems

import Database.Product
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.recipeapp.R

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun AllItemsListView(productList: List<Product>?, navController: NavController) {

    // Initialize and manage search text
    var searchText by remember { mutableStateOf("") }

    // Get the current context
    val context = LocalContext.current

    // Initialize and manage dropdown menu state
    var expanded by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf<String?>(null) }

    if (productList.isNullOrEmpty()) {
        // Display a message when the product list is empty
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Inform the user that no products are saved
            Text(
                text = "You don't have any products saved yet.",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                modifier = Modifier,
                fontStyle = FontStyle.Italic
            )

            // Provide an option to add the first product
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
    } else {
        // Display the product list
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            // Create a sticky header with search and dropdown
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Sticky header with search bar and dropdown
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
                            // Search bar
                            OutlinedTextField(
                                value = searchText,
                                label = { Text(text = "Search Products") },
                                singleLine = true,
                                shape = CircleShape,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(bottom = 8.dp),
                                onValueChange = { newQuery -> searchText = newQuery },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done
                                ),
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            )
                            // Dropdown menu for tags
                            Box(
                                modifier = Modifier
                                    .weight(0.5f)
                                    .padding(4.dp)
                                    .clip(CircleShape),
                                contentAlignment = Alignment.Center
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
                                            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            disabledTextColor = Color.Transparent,
                                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent
                                        )
                                    )
                                    // Dropdown menu for tags
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

                // List items (product cards)
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
                    // Display individual product cards
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
    // Get the current context
    val context = LocalContext.current

    // Initialize and manage view model for all items
    val allItemsViewModel = AllItemsViewModel(context)

    // Load the product image
    allItemsViewModel.getProductImage(product, context)

    // Get the stored image, or use a placeholder if not available
    val storedImage = allItemsViewModel.storedImage

    // Create a painter for the product image
    val painter = if (storedImage != null) {
        BitmapPainter(storedImage.asImageBitmap())
    } else {
        painterResource(R.drawable.placeholder)
    }

    // Display an individual product card
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable {
                // Navigate to the product details when clicked
                navController.navigate("itemDetail/${product.barcode}")
            }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Display the product name
            Text(
                text = product.name,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(8.dp)
            )
            // Display the product image
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

