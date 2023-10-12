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
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.abs


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
        viewModelScope.launch(Dispatchers.IO) {
            val recipes = db.RecipeappDao().GetRecipes()
            recipesLiveData.postValue(recipes)
        }
        return recipesLiveData
    }

    fun removeProduct(product: Product) {
        viewModelScope.launch() {
            db.RecipeappDao().deleteProductById(product.barcode)
            val products = db.RecipeappDao().GetProducts()
            productsLiveData.postValue(products)
        }
    }

    private fun sortProductsByClosestExpiryDate(products: List<Product>, currentDate: LocalDate): List<Product> {
        return products.filter { product ->
            product.bestbefore.isNotEmpty()
        }.sortedBy { product ->
            product.bestbefore.minBy { bestBeforeDate ->
                abs(currentDate.until(bestBeforeDate).days)
            }
        }

    }

    fun getProductsExpiringSoon(): LiveData<List<Product>> {
        val currentDate = LocalDate.now()

        viewModelScope.launch {
            val products = db.RecipeappDao().GetProducts()

            val sortedProducts = sortProductsByClosestExpiryDate(products, currentDate)

            productsLiveData.postValue(sortedProducts)
        }
        return productsLiveData
    }

    fun addToShoppingList(product: Product) {
        viewModelScope.launch {
            val shoppingItem = ShoppingItem(name = product.name, amount = "1")
            db.shoppingItemDao().insertShoppingItem(shoppingItem)
        }
    }

    fun getProductImage(product: Product, context: Context) {
        if (product.image != "") {
            savedImagePath = product.image.toString()
            storedImage = getImageFromInternalStorage(context, savedImagePath)
        } else {
            storedImage = null
        }
    }

    fun generateBarcodeImage(barcodeValue: String): Bitmap? {
        try {
            val multiFormatWriter = MultiFormatWriter()
            val bitMatrix: BitMatrix =
                multiFormatWriter.encode(barcodeValue, BarcodeFormat.CODE_128, 800, 400)
            val barcodeBitmap =
                Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.ARGB_8888)
            for (x in 0 until bitMatrix.width) {
                for (y in 0 until bitMatrix.height) {
                    barcodeBitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                    )
                }
            }
            return barcodeBitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}