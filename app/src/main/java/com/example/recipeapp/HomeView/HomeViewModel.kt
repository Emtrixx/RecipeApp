package com.example.recipeapp.HomeView

import Database.Product
import Database.Recipeapp
import Database.RecipeappViewModel
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.util.Date
import kotlin.random.Random

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeappViewModel = RecipeappViewModel(application)

    // LiveData to hold the list of products
    private var productsLiveData: LiveData<List<Product>> = recipeappViewModel.getProductsAsLiveData()

    fun getProductsLiveData(): LiveData<List<Product>> {
        return productsLiveData
    }

    fun addProduct() {
        recipeappViewModel.addProduct(
            name = "egg ${Random.Default.nextInt(1, 100)}",
            bestbefore = Date(),
            amount = 2.0,
            barcode = Random.Default.nextInt(1, 1000000).toString(),
            description = "pirkka eggs, the best ones",
            image = R.drawable.egg,
            tags =  listOf("egg")
        )
    }

    fun fetchProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            recipeappViewModel.fetchProducts()
        }
    }

    fun removeProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeappViewModel.removeProduct(product.barcode)
            productsLiveData = recipeappViewModel.getProductsAsLiveData()
        }
    }
}