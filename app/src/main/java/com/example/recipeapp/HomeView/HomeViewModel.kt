package com.example.recipeapp.HomeView

import Database.Product
import Database.Recipeapp
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

class HomeViewModel(context: Context) : ViewModel() {

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
                    image = "R.drawable.egg",
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
}