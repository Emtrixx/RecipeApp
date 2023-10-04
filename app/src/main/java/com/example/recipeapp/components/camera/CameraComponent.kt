package com.example.recipeapp.components.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.recipeapp.R
import com.example.recipeapp.product.createImageFile
import com.google.android.datatransport.BuildConfig
import java.util.Objects

@Composable
fun CameraComponent(photoCallback: (boolean: Boolean, uri: Uri) -> Unit) {
    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
//        TODO: Potential Bug - on other devices Manifest provider needs to match this one
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture(), {
            photoCallback(it, uri)
        })

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Button(onClick = {
        val permissionCheckResult =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }) {
        Text(text = stringResource(R.string.camera_button_label))
    }
}

fun saveImageToInternalStorage(context: Context, uri: Uri, fileName: String): String {
    val path = "$fileName.jpg"
    val inputStream = context.contentResolver.openInputStream(uri)
    val outputStream = context.openFileOutput(path, Context.MODE_PRIVATE)
    inputStream?.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
    return path
}

fun getImageFromInternalStorage(context: Context, path: String): Bitmap {
    val inputStream = context.openFileInput(path)
    return BitmapFactory.decodeStream(inputStream)
}