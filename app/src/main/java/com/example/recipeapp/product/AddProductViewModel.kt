package com.example.recipeapp.product

import Database.Product
import Database.Recipeapp
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.components.camera.getImageFromInternalStorage
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AddProductViewModel(barcodeArg: String?, context: Context) : ViewModel() {

    private val db: Recipeapp by lazy {
        Recipeapp.getInstance(context)
    }
    private var savedImagePath = ""

    var name by mutableStateOf("")
        private set
    var nameError by mutableStateOf<List<String>>(listOf())
        private set
    var capturedImageUri by mutableStateOf(Uri.EMPTY)
        private set
    var storedImage by mutableStateOf<Bitmap?>(null)
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
            getProduct(barcodeArg, context)
        }
    }

    private fun getProduct(barcode: String, context: Context) {
        viewModelScope.launch {
            val product = db.RecipeappDao().GetProductInfo(barcode)

            if (product != null) {
                if (product.image != null) {
                    savedImagePath = product.image
                    storedImage = getImageFromInternalStorage( context ,savedImagePath)
                }
                name = product.name
                bestBefore = product.bestbefore.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                description = product.description
                amount = product.amount.toString()
            }
        }
    }

    fun upsertProduct() {
        var product = Product(
            barcode = barcode,
            name = name,
            bestbefore = LocalDate.parse(bestBefore, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            description = description,
            amount = amount.toInt(),
            tags = listOf(),
            image = savedImagePath
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

    fun updatePicture(uri: Uri) {
        this.capturedImageUri = uri
    }

    fun updateSavedImagePath(path: String) {
        this.savedImagePath = path
    }
}