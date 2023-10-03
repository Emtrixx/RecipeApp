package com.example.recipeapp.AllItems

import Database.Product
import Database.RecipeappViewModel
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.random.Random

class AllItemsViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeappViewModel = RecipeappViewModel(application)

    // LiveData to hold the list of products
    private val productsLiveData: LiveData<List<Product>> = recipeappViewModel.getProductsAsLiveData()

    fun getProductsAsLiveData(): LiveData<List<Product>> {
        return productsLiveData
    }

    fun fetchProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            recipeappViewModel.fetchProducts()
        }
    }


}