package com.example.recipeapp.product

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


// DUMMY DATA CLASS TODO: Get from DB
data class Product(
    var name: String,
    var picture: String,
    var barcode: String,
    var bestBefore: String,
    var description: String?,
    var amount: String,
)

class AddProductViewModel(barcode: String?) : ViewModel() {

    //    val product = MutableStateOf<Product?>()
//    var productUiState by mutableStateOf<Product>()
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
    var amount by mutableStateOf("")
        private set

    init {
        if (barcode != null) {
            getProduct(barcode)
        }
    }
    fun getProduct(barcode: String) {
        // TODO: Get from DB
    }
    fun updateName(name: String) {
        this.name = name
    }

}