package com.example.recipeapp.HomeView

import Database.Product
import Database.Recipeapp
import Database.ShoppingItem
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.components.camera.getImageFromInternalStorage
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import kotlin.random.Random


class HomeViewModel(context: Context) : ViewModel() {

    private val db: Recipeapp by lazy {
        Recipeapp.getInstance(context)
    }

    private val productsLiveData: MutableLiveData<List<Product>> = MutableLiveData()

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

    var date: LocalDate = LocalDate.of(2000, 10, 3)
    fun addProduct() {
        viewModelScope.launch{
            db.RecipeappDao().InsertProduct(
                Product(
                    barcode = Random.Default.nextInt(1, 1000000).toString(),
                    name = "egg ${Random.Default.nextInt(1, 100000)}",
                    description = "pirkka eggs, the best ones",
                    amount = 2,
                    tags = listOf("egg"),
                    image = null,
                    bestbefore = listOf(date),
                )
            )
        }
    }

    fun removeProduct(barcode: String) {
        viewModelScope.launch {
            db.RecipeappDao().deleteProductById(barcode)
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
        if (product.image != null) {
            savedImagePath = product.image
            storedImage = getImageFromInternalStorage(context, savedImagePath)
        }
    }
}