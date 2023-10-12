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
import com.example.recipeapp.lib.getImageFromInternalStorage
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

    //function to get all products from the database
    fun getProductsLiveData(): LiveData<List<Product>> {
        viewModelScope.launch {
            val products = db.RecipeappDao().GetProducts()
            productsLiveData.postValue(products)
        }
        return productsLiveData
    }

    //function to get all recipes from the database
    fun getRecipesLiveData(): LiveData<List<Recipe>> {
        viewModelScope.launch(Dispatchers.IO) {
            val recipes = db.RecipeappDao().GetRecipes()
            recipesLiveData.postValue(recipes)
        }
        return recipesLiveData
    }

    //function to add a product to the database
    fun removeProduct(product: Product) {
        viewModelScope.launch() {
            db.RecipeappDao().deleteProductById(product.barcode)
            val products = db.RecipeappDao().GetProducts()
            productsLiveData.postValue(products)
        }
    }

    // function to sort products by closest expiry date
    private fun sortProductsByClosestExpiryDate(products: List<Product>, currentDate: LocalDate): List<Product> {
        val filteredProducts = products.filter { product ->
            product.bestbefore.filterNotNull().isNotEmpty()
        }
        val sortedProducts = filteredProducts.sortedBy { product ->
            product.bestbefore.filterNotNull().minBy { bestBeforeDate ->
                abs(currentDate.until(bestBeforeDate).days)
            }
        }
        return sortedProducts
    }

    // function to get products that are expiring soon
    fun getProductsExpiringSoon(): LiveData<List<Product>> {
        val currentDate = LocalDate.now()

        viewModelScope.launch {
            val products = db.RecipeappDao().GetProducts()

            val sortedProducts = sortProductsByClosestExpiryDate(products, currentDate)

            productsLiveData.postValue(sortedProducts)
        }
        return productsLiveData
    }

    // function to add a recipe to the shopping list
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


    // functio to generate a barcode image using the product barcode
    // using the ZXing library
    fun generateBarcodeImage(barcodeValue: String): Bitmap? {

        // If barcode is a valid UUID return null
        if (barcodeValue.length == 36) {
            return null
        }

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