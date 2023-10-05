package com.example.recipeapp.ItemView

import Database.Product
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.input.nestedscroll.nestedScrollModifierNode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipeapp.R
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.text.DateFormat

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemDetailView(item: Product) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        // Item Image
        item {
            Box() {
                Image(
                    painter = painterResource(id = R.drawable.egg),
                    contentDescription = "Product image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                )
                // Best Before
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            color = Color.Red,
                            shape = RoundedCornerShape(16.dp)
                        ),
                ) {
                    Text(
                        text = "Best Before: ${
                            DateFormat.getDateInstance().format(item.bestbefore)
                        }",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
            }
        }
        stickyHeader {
            ElevatedCard(
                modifier = Modifier,
                shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 24.dp
                ),
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
                        text = item.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Box(
                        Modifier
                            .wrapContentSize(Alignment.TopEnd)
                    ) {
                        IconButton(
                            onClick = {
                            }
                        ) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Edit amount"
                            )
                        }
                    }
                }
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
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {},
                        Modifier
                            .background(Color.Black, shape = RoundedCornerShape(16.dp))
                            .size(75.dp),
                    )
                    {
                        Icon(
                            Icons.Filled.ShoppingCart,
                            contentDescription = "Shopping list"
                        )
                    }
                    IconButton(
                        onClick = {},
                        Modifier
                            .background(Color.Black, shape = RoundedCornerShape(16.dp))
                            .size(75.dp),
                    )
                    {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Shopping list"
                        )
                    }
                    IconButton(
                        onClick = {},
                        Modifier
                            .background(Color.Black, shape = RoundedCornerShape(16.dp))
                            .size(75.dp),
                    )
                    {
                        Icon(
                            Icons.Filled.Menu,
                            contentDescription = "Shopping list"
                        )
                    }
                    IconButton(
                        onClick = {},
                        Modifier
                            .background(Color.Black, shape = RoundedCornerShape(16.dp))
                            .size(75.dp),
                    )
                    {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Shopping list"
                        )
                    }
                }
                // Item Description
                Text(
                    text = item.description,
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(vertical = 64.dp)
                )
                Divider(thickness = 1.dp, color = Color.White)
                // Amount
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Amount: ${item.amount.toInt()}",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(vertical = 64.dp)
                    )
                }
                Divider(thickness = 1.dp, color = Color.White)
                // Tags
                if (item.tags.isNotEmpty()) {
                    Text(
                        text = "Tags: ${item.tags.joinToString(", ")}",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(vertical = 64.dp)
                    )
                    Divider(thickness = 1.dp, color = Color.White)
                }
                // Carbon footprint
                Text(
                    text = "Carbon footprint: 1.6 kg CO2e ",
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(vertical = 64.dp)
                )
            }
        }
    }
}