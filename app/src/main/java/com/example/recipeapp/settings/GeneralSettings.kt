package com.example.recipeapp.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recipeapp.settings.components.SettingItem
import com.example.recipeapp.settings.components.SettingsLayout

@Composable
fun GeneralSettings(navController: NavHostController) {
    SettingsLayout(
        "General", "General Settings for the app",
        {
            SettingItem(name = "About", icon = {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "About Information Setting"
                )
            }, onClick = {
                navController.navigate("about")
            })
        },
    )
}

@Composable
fun About() {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Text(modifier = Modifier.padding(15.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                text ="Fridge Mate is application that helps you manage your fridge, generate shopping lists, and explore new recipes using AI-powered features.")
        }
        Spacer(modifier = Modifier.padding(10.dp))

        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                style = MaterialTheme.typography.titleLarge,
                text ="Save Your Products")
            Text("- Add your groceries by scanning their barcode")
            Text("- Add description and the expiry date of the product")
            Text("- Tags and carbon footprint are calculated automatically")
            Text("- Take and save the picture of the product")
        }

        Spacer(modifier = Modifier.padding(5.dp))

        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                style = MaterialTheme.typography.titleLarge,
                text ="Recipe Generator")
            Text("- Generate recipes of your saved products with ChatGPT")
        }

        Spacer(modifier = Modifier.padding(5.dp))

        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = "Shopping List")
            Text("- Create and manage your shopping lists")
            Text("- Add products to your list manually or by scanning their barcodes")
        }

        Spacer(modifier = Modifier.padding(5.dp))

        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = "Usage")
            Text("You will need a ChatGPT api key to use the application")
        }
    }
}