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
import java.time.format.DateTimeParseException


class AddProductViewModel(barcodeArg: String?, context: Context) : ViewModel() {

    private val db: Recipeapp by lazy {
        Recipeapp.getInstance(context)
    }
    private var savedImagePath = ""

    var loading by mutableStateOf(true)
        private set
    var name by mutableStateOf("")
        private set
    var nameErrors by mutableStateOf<List<String>>(listOf())
        private set
    var capturedImageUri by mutableStateOf(Uri.EMPTY)
        private set
    var storedImage by mutableStateOf<Bitmap?>(null)
        private set
    var barcode by mutableStateOf("")
        private set

    var bestBeforeList: List<String?> by mutableStateOf(listOf(null))
        private set
    var bestBeforeErrors by mutableStateOf<List<String>>(listOf())
        private set
    private var oldBestBeforeList: List<LocalDate?> = listOf()
    private var bestBeforeSetOnce = false

    var description by mutableStateOf("")
        private set
    var descriptionErrors by mutableStateOf<List<String>>(listOf())
        private set

    var amount by mutableIntStateOf(1)
        private set
    private var oldAmount = 0
    var amountErrors by mutableStateOf<List<String>>(listOf())
        private set

    init {
        if (barcodeArg != null) {
            barcode = barcodeArg
            getProduct(barcodeArg, context)
        } else {
            loading = false
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
                description = product.description

//              Save old values for updating (TODO: Should not change in DB while editing)
                oldBestBeforeList = product.bestbefore
                oldAmount = product.amount
            }
            loading = false
        }
    }

    fun upsertProduct() {
        val product = Product(
            barcode = barcode,
            name = name,
            description = description,
            tags = listOf(),
            image = savedImagePath,
//          Taking old values into consideration
            bestbefore = oldBestBeforeList + bestBeforeList.map {
                if (it == null) {
                    return@map null
                }
                LocalDate.parse(it, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            },
            amount = oldAmount + amount,
        )

        viewModelScope.launch { db.RecipeappDao().UpsertProduct(product) }
    }

    fun updateName(name: String) {
        this.name = name
        nameErrors = listOf()
        if (name.isBlank()) {
            nameErrors = nameErrors + "Name cannot be empty."
        } else if (!name.matches(Regex("^[a-zA-Z0-9 ]+$"))) {
            nameErrors = nameErrors + "Name can only contain alphanumeric characters and spaces."
        }
    }

    fun updateDescription(description: String) {
        this.description = description
        descriptionErrors = listOf()
        if (description.length > 500) {
            descriptionErrors = descriptionErrors + "Description cannot exceed 500 characters."
        }
    }

    fun updateAmount(amount: Int) {
        amountErrors = listOf()
        if (amount > 99) {
            amountErrors = amountErrors + "Amount cannot exceed 99."
        }
        this.amount = amount

        if (amount > bestBeforeList.size) {
            bestBeforeList = bestBeforeList + List<String?>(amount - bestBeforeList.size) { null }
        } else {
            bestBeforeList = bestBeforeList.take(amount)
        }
    }

    fun updateBestBefore(bestBefore: String, index: Int) {
        bestBeforeErrors = listOf()
        if (!isValidDate(bestBefore) && bestBefore.isNotBlank()) {
            bestBeforeErrors = bestBeforeErrors + "Invalid date format. Use dd/MM/yyyy."
        }

        if (bestBeforeSetOnce || bestBefore.isBlank()) {
            this.bestBeforeList = bestBeforeList.mapIndexed { i, s ->
                if (i == index) {
                    if (bestBefore.isBlank()) {
                        return@mapIndexed null
                    }
                    return@mapIndexed bestBefore
                }
                return@mapIndexed s
            }
        } else {
            this.bestBeforeList = List(bestBeforeList.size) { i ->
                return@List bestBefore
            }
            bestBeforeSetOnce = true
        }
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    fun updatePicture(uri: Uri) {
        this.capturedImageUri = uri
    }

    fun updateSavedImagePath(path: String) {
        this.savedImagePath = path
    }
}