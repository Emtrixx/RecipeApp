package com.example.recipeapp.Navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.recipeapp.HomeView.HomeView


sealed class BottomNavItem(var title:String, var icon: ImageVector, var screen_route:String){

    object Home : BottomNavItem("Home",Icons.Default.Home,"home")
    object ShoppingList: BottomNavItem("Shopping List", Icons.Default.List,"home")
    object AddItem: BottomNavItem("Empty",Icons.Default.Add,"home")
    object Recipes: BottomNavItem("Empty",Icons.Default.Info,"home")
    object Settings: BottomNavItem("Empty",Icons.Default.Settings,"home")
}

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomNavItem.Home.screen_route)
    {
        composable(route = BottomNavItem.Home.screen_route) {
            HomeView()
        }
        composable(route = BottomNavItem.Home.screen_route) {
            HomeView()
        }
        composable(route = BottomNavItem.Home.screen_route) {
            HomeView()
        }
        composable(route = BottomNavItem.Home.screen_route) {
            HomeView()
        }
        composable(route = BottomNavItem.Home.screen_route) {
            HomeView()
        }
    }
}

@Composable
fun ShitPissBar(navController: NavHostController) {
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
    BottomNavigationItem(
        label = {
            Text(text = screen.title)
        },
        icon = {
            Icon(imageVector = screen.icon,
                contentDescription = "Navigation icon")
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.screen_route
        } == true,
        onClick = {
            navController.navigate(screen.screen_route)
        }
    )
}
