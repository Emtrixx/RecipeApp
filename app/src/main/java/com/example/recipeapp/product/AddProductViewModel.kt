package com.example.recipeapp.product

import Database.Product
import Database.Recipeapp
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.AddingProductFieldsService
import com.example.recipeapp.lib.getImageFromInternalStorage
import com.example.recipeapp.lib.uploadImageToServer
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.UUID


class AddProductViewModel(barcodeArg: String?, context: Context, val edit: Boolean) :
    ViewModel() {

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
    var capturedImageUri: Uri by mutableStateOf(Uri.EMPTY)
        private set
    var imageTimeStamp by mutableLongStateOf(0L)
        private set
    var storedImage by mutableStateOf<Bitmap?>(null)
        private set
    var predictedName by mutableStateOf("")
        private set
    var showPredictionDialog: Boolean by mutableStateOf(false)
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
            getProduct(barcodeArg, context, edit)
        } else {
            if (edit) {
                throw IllegalArgumentException("Barcode cannot be null when editing.")
            }
            barcode = UUID.randomUUID().toString()
            loading = false
        }
    }

    private fun getProduct(barcode: String, context: Context, edit: Boolean) {
        viewModelScope.launch {
            val product = db.RecipeappDao().GetProductInfo(barcode)

            if (product != null) {
                if (product.image != null) {
                    savedImagePath = product.image
                    storedImage = getImageFromInternalStorage(context, savedImagePath)
                }
                name = product.name
                description = product.description

                if (edit) {
                    Log.d("AddProductViewModel", "Editing product")
                    amount = product.amount
                    bestBeforeList = product.bestbefore.map { date ->
                        if (date == null) {
                            return@map null
                        }
                        return@map date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    }
                } else {
                    // Save old values for adding to them (TODO: Should not change in DB while editing)
                    oldBestBeforeList = product.bestbefore
                    oldAmount = product.amount
                }
            }
            loading = false
        }
    }

    fun upsertProduct(context: Context) {
        val product = Product(
            barcode = barcode,
            name = name,
            description = description,
            image = savedImagePath,
//          Taking old values into consideration
            bestbefore = oldBestBeforeList + bestBeforeList.map {
                if (it == null) {
                    return@map null
                }
                LocalDate.parse(it, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            },
            amount = oldAmount + amount,
            // To be filled in background service
            tags = listOf(),
            carbonFootprint = null
        )

        viewModelScope.launch {
            db.RecipeappDao().UpsertProduct(product)

            // Add tags in the background with ChatGPT
            Log.d("AddProductViewModel", "Starting AddingTagsService")
            val intent = Intent(context, AddingProductFieldsService::class.java)
            intent.putExtra("barcode", product.barcode)
            context.startService(intent)
        }
    }

    fun updateName(name: String) {
        this.name = name
        validateName(name)
    }

    private fun validateName(name: String): Boolean {
        nameErrors = listOf()
        if (name.isBlank()) {
            nameErrors = nameErrors + "Name cannot be empty."
        } else if (!name.matches(Regex("^[a-zA-Z0-9 ]+$"))) {
            nameErrors = nameErrors + "Name can only contain alphanumeric characters and spaces."
        }
        return nameErrors.isEmpty()
    }

    fun updateDescription(description: String) {
        this.description = description
        validateDescription(description)
    }

    private fun validateDescription(description: String): Boolean {
        descriptionErrors = listOf()
        if (description.length > 500) {
            descriptionErrors = descriptionErrors + "Description cannot exceed 500 characters."
        }
        return descriptionErrors.isEmpty()
    }

    fun updateAmount(amount: Int) {
        this.amount = amount
        validateAmount(amount)

        bestBeforeList = if (amount > bestBeforeList.size) {
            bestBeforeList + List<String?>(amount - bestBeforeList.size) { null }
        } else {
            bestBeforeList.take(amount)
        }
    }

    private fun validateAmount(amount: Int): Boolean {
        amountErrors = listOf()
        if (amount > 99) {
            amountErrors = amountErrors + "Amount cannot exceed 99."
        }
        return amountErrors.isEmpty()
    }

    fun updateBestBefore(bestBefore: String, index: Int) {
        validateBestBefore(bestBefore)

        if (bestBeforeSetOnce || bestBefore.isBlank() || edit) {
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
            this.bestBeforeList = List(bestBeforeList.size) { _ ->
                return@List bestBefore
            }
            bestBeforeSetOnce = true
        }
    }

    private fun validateBestBefore(bestBefore: String): Boolean {
        bestBeforeErrors = listOf()
        if (!isValidDate(bestBefore) && bestBefore.isNotBlank()) {
            bestBeforeErrors = bestBeforeErrors + "Invalid date format. Use dd/MM/yyyy."
        }
        return bestBeforeErrors.isEmpty()
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
        this.imageTimeStamp = System.currentTimeMillis()
        this.capturedImageUri = uri
    }

    fun getImagePrediction(file: File) {
        viewModelScope.launch {
            uploadImageToServer(file) { prediction ->
                predictedName = prediction
                updateShowPredictionDialog(true)
            }
        }
    }

    fun updateShowPredictionDialog(showPredictionDialog: Boolean) {
        this.showPredictionDialog = showPredictionDialog
    }

    fun updateSavedImagePath(path: String) {
        this.savedImagePath = path
    }

    fun validateAll(): Boolean {
        var valid = true
        valid = validateName(name) && valid
        valid = validateDescription(description) && valid
        valid = validateAmount(amount) && valid
        bestBeforeList.forEach { bestBefore ->
            valid = validateBestBefore(bestBefore ?: "") && valid
        }
        return valid
    }
}