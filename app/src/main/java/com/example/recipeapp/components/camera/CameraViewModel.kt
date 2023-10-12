package com.example.recipeapp.components.camera

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import com.example.recipeapp.product.createImageFile
import com.google.android.datatransport.BuildConfig
import java.io.File
import java.util.Objects

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    val context = getApplication<Application>().applicationContext

    var file by mutableStateOf<File>(context.createImageFile())
        private set

    var uri by mutableStateOf(
        FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            BuildConfig.APPLICATION_ID + ".provider", file
        )
    )
        private set

    fun createNewTempFile() {
        file = context.createImageFile()
        uri = FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            BuildConfig.APPLICATION_ID + ".provider", file
        )
    }
}