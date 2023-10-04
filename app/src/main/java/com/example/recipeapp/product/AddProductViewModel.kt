package com.example.recipeapp.product

import Database.Product
import Database.Recipeapp
import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AddProductViewModel(barcodeArg: String?, application: Application) : ViewModel() {

    private val db: Recipeapp by lazy {
        Recipeapp.getInstance(application)
    }

    var name by mutableStateOf("")
        private set
    var nameError by mutableStateOf<List<String>>(listOf())
        private set
    var picture by mutableStateOf("")
        private set
    var barcode by mutableStateOf("")
        private set
    var bestBefore by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var amount by mutableStateOf("1")
        private set

    init {
        if (barcodeArg != null) {
            barcode = barcodeArg
            getProduct(barcodeArg)
        }
    }

    fun getProduct(barcode: String) {
        viewModelScope.launch {
            val product = db.RecipeappDao().GetProductInfo(barcode)
            if (product != null) {
                name = product.name
//            picture = product.image
                bestBefore = product.bestbefore.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                description = product.description
                amount = product.amount.toString()
            }
        }
    }

    fun upsertProduct() {
        val product = Product(
            barcode = barcode,
            name = name,
            bestbefore = LocalDate.parse(bestBefore, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            description = description,
            amount = amount.toInt(),
            tags = listOf(),
            image = byteArrayOf()
        )
        viewModelScope.launch { db.RecipeappDao().UpsertProduct(product) }
    }

    fun updateName(name: String) {
        this.name = name
    }

    fun updateDescription(description: String) {
        this.description = description
    }

    fun updateAmount(amount: String) {
        if (amount.length <= 2) {
            this.amount = amount.filter { it.isDigit() }
        }
    }

    fun updateBestBefore(bestBefore: String) {
        this.bestBefore = bestBefore
    }

}