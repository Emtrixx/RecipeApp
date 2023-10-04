package com.example.recipeapp.product

import Database.Product
import Database.Recipeapp
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
    var bestBeforeList: List<String?> by mutableStateOf(listOf(null))
        private set
    var description by mutableStateOf("")
        private set
    var amount by mutableIntStateOf(1)
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
                    storedImage = getImageFromInternalStorage(context, savedImagePath)
                }
                name = product.name
                bestBeforeList = product.bestbefore.map {
                    if (it == null) {
                        return@map null
                    }
                    it.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                }
                description = product.description
                amount = product.amount
            }
        }
    }

    fun upsertProduct() {
        val product = Product(
            barcode = barcode,
            name = name,
            bestbefore = bestBeforeList.map {
                if (it == null) {
                    return@map null
                }
                LocalDate.parse(it, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            },
            description = description,
            amount = amount,
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

    fun updateAmount(amount: Int) {
//        if (amount.length <= 2) {
//            this.amount = amount.filter { it.isDigit() }
//        }
        this.amount = amount

        if (amount > bestBeforeList.size) {
            bestBeforeList = bestBeforeList + List<String?>(amount - bestBeforeList.size) { null }
        } else {
            bestBeforeList = bestBeforeList.take(amount)
        }
    }

    fun updateBestBefore(bestBefore: String, index: Int) {
        this.bestBeforeList = bestBeforeList.mapIndexed { i, s ->
            if (i == index) {
                return@mapIndexed bestBefore
            }
            return@mapIndexed s
        }
    }

    fun updatePicture(uri: Uri) {
        this.capturedImageUri = uri
    }

    fun updateSavedImagePath(path: String) {
        this.savedImagePath = path
    }
}