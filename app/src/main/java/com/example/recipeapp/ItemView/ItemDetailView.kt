package com.example.recipeapp.ItemView

import Database.Product
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DateFormat

@Composable
fun ItemDetailView(item: Product) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Item Name
        Text(
            text = item.name,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(vertical = 8.dp),
            )

        // Item Image
        Image(
            painter = painterResource(id = item.image),
            contentDescription = "Product image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Item Description
                Text(
                    text = item.description,
                    fontSize = 24.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                )

                // Best Before
                Box(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .background(
                            color = Color.Red,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Text(
                        text = "Best Before: ${DateFormat.getDateInstance().format(item.bestbefore)}",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }

                // Amount
                Text(
                    text = "Amount: ${item.amount}",
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                )

                // Tags
                if (item.tags.isNotEmpty()) {
                    Text(
                        text = "Tags: ${item.tags.joinToString(", ")}",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}