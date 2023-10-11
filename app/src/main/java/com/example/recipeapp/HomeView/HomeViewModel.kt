package com.example.recipeapp.HomeView

import Database.Product
import Database.Recipe
import Database.Recipeapp
import Database.ShoppingItem
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.components.camera.getImageFromInternalStorage
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random


class HomeViewModel(context: Context) : ViewModel() {

    private val db: Recipeapp by lazy {
        Recipeapp.getInstance(context)
    }

    private val productsLiveData: MutableLiveData<List<Product>> = MutableLiveData()

    private val recipesLiveData: MutableLiveData<List<Recipe>> = MutableLiveData()

    private var savedImagePath = ""

    var storedImage by mutableStateOf<Bitmap?>(null)
        private set

    fun getProductsLiveData(): LiveData<List<Product>> {
        viewModelScope.launch {
            val products = db.RecipeappDao().GetProducts()
            productsLiveData.postValue(products)
        }
        return productsLiveData
    }

    fun getRecipesLiveData(): LiveData<List<Recipe>> {
        viewModelScope.launch {
            val recipes = db.RecipeappDao().GetRecipes()
            recipesLiveData.postValue(recipes)
        }
        return recipesLiveData
    }

    var date: LocalDate = LocalDate.of(2000, 10, 3)

    fun removeProduct(product: Product) {
        viewModelScope.launch() {
            db.RecipeappDao().deleteProductById(product.barcode)
            val products = db.RecipeappDao().GetProducts()
            productsLiveData.postValue(products)
        }
    }

    fun addToShoppingList(product: Product) {
        viewModelScope.launch {
            val shoppingItem = ShoppingItem(name = product.name, amount = 1,)
            db.shoppingItemDao().insertShoppingItem(shoppingItem)
        }
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