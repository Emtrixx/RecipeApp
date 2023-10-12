package com.example.recipeapp.AllItems

import Database.Product
import Database.Recipe
import Database.Recipeapp
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.lib.getImageFromInternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllItemsViewModel(context : Context) : ViewModel() {
    private var savedImagePath = ""

    var storedImage by mutableStateOf<Bitmap?>(null)
        private set

    private val db: Recipeapp by lazy {
        Recipeapp.getInstance(context)
    }

    private val productsLiveData: MutableLiveData<List<Product>> = MutableLiveData()

    private val recipesLiveData: MutableLiveData<List<Recipe>> = MutableLiveData()

    fun getProductsLiveData(): LiveData<List<Product>> {
        viewModelScope.launch {
            val products = db.RecipeappDao().GetProducts()
            productsLiveData.postValue(products)
        }
        return productsLiveData
    }

    fun getRecipesLiveData(): LiveData<List<Recipe>> {
        viewModelScope.launch(Dispatchers.IO){
            val recipes = db.RecipeappDao().GetRecipes()
            recipesLiveData.postValue(recipes)
        }
        return recipesLiveData
    }

    fun getProductImage (product: Product, context : Context) {
        if (product.image != "") {
            savedImagePath = product.image.toString()
            storedImage = getImageFromInternalStorage(context, savedImagePath)
        } else {
            storedImage = null
        }
    }
}