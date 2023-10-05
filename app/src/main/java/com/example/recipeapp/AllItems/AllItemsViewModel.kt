package com.example.recipeapp.AllItems

import Database.Product
import Database.Recipeapp
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.R
import com.example.recipeapp.components.camera.getImageFromInternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.random.Random

class AllItemsViewModel(context : Context) : ViewModel() {
    private var savedImagePath = ""

    var storedImage by mutableStateOf<Bitmap?>(null)

    private val db: Recipeapp by lazy {
        Recipeapp.getInstance(context)
    }

    private val productsLiveData: MutableLiveData<List<Product>> = MutableLiveData()

    fun getProductsLiveData(): LiveData<List<Product>> {
        viewModelScope.launch {
            val products = db.RecipeappDao().GetProducts()
            productsLiveData.postValue(products)
        }
        return productsLiveData
    }

    fun getProductImage (product: Product, context : Context) {
        if (product.image != null) {
            savedImagePath = product.image
            storedImage = getImageFromInternalStorage(context, savedImagePath)
        }
    }
}