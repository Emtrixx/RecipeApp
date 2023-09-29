package com.example.recipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recipeapp.product.AddProductForm
import com.example.recipeapp.product.BarcodeScannerView
import com.example.recipeapp.product.BarcodeViewModel
import com.example.recipeapp.product.AddProductViewModel
import com.example.recipeapp.ui.theme.RecipeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val barcodeViewModel = BarcodeViewModel()


        setContent {
            val navController = rememberNavController()

            RecipeAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController,
                        startDestination = "product"
                    ) {
                        navigation(startDestination = "list", route = "product") {
//                        navigation(startDestination = "add?barcode={barcode}", route = "product") {
                            composable("list") { ProductList(navController) }
                            composable("scanner") { BarcodeScannerView(barcodeViewModel, navController) }
                            composable(
                                "add?barcode={barcode}",
                                arguments = listOf(navArgument("barcode") { nullable = true })
                            ) {
                                val barcode = it.arguments?.getString("barcode")
                                val productViewModel = AddProductViewModel(barcode)
                                AddProductForm(productViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductList(navController: NavController) {
    Column {
        Text("Product List")
        Button(onClick = {
            navController.navigate("scanner")
        }) {
            Text("Scan Barcode")
        }
    }
}