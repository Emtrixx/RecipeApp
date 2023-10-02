package com.example.recipeapp.ItemView

import Database.Product
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun ItemDetailView(item: Product) {
    Text(text = "${item.name}")
}