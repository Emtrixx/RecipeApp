package com.example.recipeapp.Navigation

import Database.Product
import Database.Recipe
import RecipeNavigation
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.recipeapp.AllItems.AllItemsListView
import com.example.recipeapp.HomeView.HomeListView
import com.example.recipeapp.ItemView.ItemView
import com.example.recipeapp.product.AddProductForm
import com.example.recipeapp.product.AddProductViewModel
import com.example.recipeapp.product.BarcodeScannerView
import com.example.recipeapp.product.BarcodeViewModel
import com.example.recipeapp.settings.AppearanceSettings
import com.example.recipeapp.settings.DevSettings
import com.example.recipeapp.settings.GeneralSettings
import com.example.recipeapp.settings.NotificationSettingsView
import com.example.recipeapp.settings.SettingsPage
import com.example.recipeapp.settings.SettingsViewModel
import com.example.recipeapp.shopping.ShoppingList

sealed class BottomNavItem(var title: String, var icon: ImageVector, var screen: String) {

    object Home : BottomNavItem("Home", Icons.Default.Home, "home")

    object ShoppingList : BottomNavItem("Shopping List", Icons.Default.ShoppingCart, "shoppingList")
    object AddItem : BottomNavItem("Add Item", Icons.Default.Add, "scanner")
    object Recipes : BottomNavItem("Empty", Icons.Default.Info, "recipe")
    object Settings : BottomNavItem("Empty", Icons.Default.Settings, "settings")

}

@Composable
fun NavGraph(
    navController: NavHostController,
    productList: List<Product>?,
    recipeList: List<Recipe>?
) {
    NavHost(navController = navController, startDestination = BottomNavItem.Home.screen)
    {
        composable(route = BottomNavItem.Home.screen) {
            HomeListView(productList, recipeList, navController = navController)
        }
        composable(route = BottomNavItem.ShoppingList.screen) {
            ShoppingList()
        }
        composable(route = BottomNavItem.AddItem.screen) {
            val barcodeViewModel = BarcodeViewModel()
            BarcodeScannerView(barcodeViewModel, navController)
        }
        composable(route = BottomNavItem.Recipes.screen) {
           RecipeNavigation()
        }
        composable(
            "add?barcode={barcode}?edit={edit}",
            arguments = listOf(
                navArgument("barcode") { nullable = true },
                navArgument("edit") { defaultValue = false })
        ) {
            val barcode = it.arguments?.getString("barcode")
            val edit = it.arguments?.getBoolean("edit") ?: false
            val productViewModel = AddProductViewModel(barcode, LocalContext.current, edit)
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
                ItemView(product = selectedItem, navController)
            } else {
                Text("Item not found")
            }
        }
        composable(
            route = "allItems",
        ) {
            AllItemsListView(navController = navController, productList = productList)
        }
        navigation(
            route = BottomNavItem.Settings.screen,
            startDestination = "${BottomNavItem.Settings.screen}/list"
        ) {
            composable("${BottomNavItem.Settings.screen}/list") { SettingsPage(navController) }
            composable(
                route = "${BottomNavItem.Settings.screen}/{settingName}",
                arguments = listOf(navArgument("settingName") { type = NavType.StringType })
            ) { backStackEntry ->
                val settingsViewModel: SettingsViewModel = viewModel()
                // Extract the itemId from the route and find the corresponding item
                val settingName = backStackEntry.arguments?.getString("settingName")
//                Text(settingName ?: "Setting not found")
                settingName?.let {
                    when (it) {
                        "General" -> {
                            GeneralSettings()
                        }

                        "Notifications" -> {
                            NotificationSettingsView(settingsViewModel)
                        }

                        "Appearance" -> {
                            AppearanceSettings()
                        }

                        "Dev" -> {
                            DevSettings()
                        }
                    }
                }
            }
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

    BottomNavigation(elevation = 24.dp) {
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
        modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer),
        selectedContentColor = Color.White,
        unselectedContentColor = Color.Gray,
    )
}
