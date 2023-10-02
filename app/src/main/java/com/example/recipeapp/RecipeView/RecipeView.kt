package com.example.recipeapp.RecipeView

import Database.Product
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipeapp.HomeView.HomeViewModel
import com.example.recipeapp.Navigation.BottomNavItem
import kotlinx.coroutines.launch

@Composable
fun RecipeView() {
    var text by remember { mutableStateOf(TextFieldValue()) }
    val coroutineScope = rememberCoroutineScope()

    var response by remember { mutableStateOf("") }

    val recipesViewModel: RecipeViewModel = viewModel()

    val productList: List<Product>? by recipesViewModel.getProductsLiveData().observeAsState()
}