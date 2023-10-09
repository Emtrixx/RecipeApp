package com.example.recipeapp.Navigation

import Database.Product
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.recipeapp.AllItems.AllItems
import com.example.recipeapp.HomeView.HomeListView
import com.example.recipeapp.ItemView.ItemDetailView
import com.example.recipeapp.RecipeView.RecipeViewTest
import com.example.recipeapp.RecipeView.TestRecipeViewModel
import com.example.recipeapp.product.AddProductForm
import com.example.recipeapp.product.AddProductViewModel
import com.example.recipeapp.product.BarcodeViewModel
import com.example.recipeapp.product.BarcodeScannerView
import com.example.recipeapp.shopping.ShoppingList

sealed class BottomNavItem(var title: String, var icon: ImageVector, var screen: String) {

    object Home : BottomNavItem("Home", Icons.Default.Home, "home")

    object ShoppingList : BottomNavItem("Shopping List", Icons.Default.List, "shoppingList")
    object AddItem : BottomNavItem("Add Item", Icons.Default.Add, "scanner")
    object Recipes : BottomNavItem("Empty", Icons.Default.Info, "recipe")
    object Settings : BottomNavItem("Empty", Icons.Default.Settings, "home")

}

@Composable
fun NavGraph(navController: NavHostController, productList: List<Product>?) {
    NavHost(navController = navController, startDestination = BottomNavItem.Home.screen)
    {
        composable(route = BottomNavItem.Home.screen) {
            HomeListView(productList, navController = navController)
        }
        composable(route = BottomNavItem.ShoppingList.screen) {
            ShoppingList()
        }
        composable(route = BottomNavItem.AddItem.screen) {
            val barcodeViewModel = BarcodeViewModel()
            BarcodeScannerView(barcodeViewModel, navController)
        }
        composable(route = BottomNavItem.Recipes.screen) {
            RecipeViewTest(viewModel = TestRecipeViewModel())
        }
        composable(
            "add?barcode={barcode}",
            arguments = listOf(navArgument("barcode") { nullable = true })
        ) {
            val barcode = it.arguments?.getString("barcode")
            val productViewModel = AddProductViewModel(barcode, LocalContext.current)
            AddProductForm(productViewModel, navController)
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
        composable(
            route = "allItems",
        ) {
            AllItems()
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val screens = listOf(
        BottomNavItem.Home,
        BottomNavItem.ShoppingList,
        BottomNavItem.AddItem,
        BottomNavItem.Recipes,
        BottomNavItem.Settings
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomNavigation() {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavItem,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    if (screen.screen != "scanner") {
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = screen.icon,
                    contentDescription = "Navigation icon",
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            selected = currentDestination?.hierarchy?.any {
                Log.d("DBG", screen.screen ?: "empty")
                it.route == screen.screen
            } == true,
            onClick = {
                navController.navigate(screen.screen)
            },
            modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant),
            selectedContentColor = Color.White,
            unselectedContentColor = Color.Gray,
        )
    } else {
        FloatingActionButton(
            shape = CircleShape,
            onClick = {
                navController.navigate(screen.screen)
            },
            contentColor = Color.White,
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
                .padding(2.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add icon")
        }
    }
}

@Composable
fun RecipeAppTopAppBar(navController: NavController) {

    val title = when (navController.currentBackStackEntryAsState().value?.destination?.route) {
        "home" -> "Home"
        "itemDetail/{itemId}" -> "Product"
        // Add more cases for different views
        else -> "RecipeApp"
    }

    TopAppBar(
        title = { Text(text = title) },
    )
}
