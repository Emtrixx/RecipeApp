package com.example.recipeapp.shopping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingForm(
    onItemAdded: (String) -> Unit
){
    var newItem by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp, 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Add to shopping list",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.padding(8.dp))

        Text(
            text = "Item name",
            style = MaterialTheme.typography.bodyLarge
        )

        //Text Field for adding items to ShoppingList
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = newItem,
            onValueChange = { newItem = it },
            placeholder = { Text(text = "e.g. Milk") }
        )

        //Button for adding items to list
        Button(
            onClick = {
                if (newItem.isNotBlank()) {
                    onItemAdded(newItem)
                    newItem = ""
                }
            },
            modifier = Modifier
                .padding(top = 8.dp)
        ) {
            Text("Add")
        }
    }
}

