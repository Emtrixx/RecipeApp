package com.example.recipeapp.components.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.recipeapp.R
import com.example.recipeapp.product.createImageFile
import com.google.android.datatransport.BuildConfig
import java.io.File
import java.util.Objects

@Composable
fun CameraComponent(
    modifier: Modifier = Modifier,
    photoCallback: (boolean: Boolean, uri: Uri, file: File) -> Unit
) {
    val context = LocalContext.current
    val file = rememberSaveable {
        context.createImageFile()
    }
    val uri = rememberSaveable {
        FileProvider.getUriForFile(
            Objects.requireNonNull(context),
//        TODO: Potential Bug - on other devices Manifest provider needs to match this one
            BuildConfig.APPLICATION_ID + ".provider", file
        )
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            photoCallback(it, uri, file)
        }

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

    Button(modifier = modifier, onClick = {
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

fun saveImageToInternalStorage(context: Context, uri: Uri, fileName: String): String? {
    val path = "$fileName.jpg"
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val orientation = getExifOrientation(context, uri)

        val rotatedBitmap = rotateBitmap(bitmap, orientation)

        saveBitmap(context, rotatedBitmap, path)

        path
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getImageFromInternalStorage(context: Context, path: String): Bitmap {
    val inputStream = context.openFileInput(path)
    return BitmapFactory.decodeStream(inputStream)
}

fun getExifOrientation(context: Context, uri: Uri): Int {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val exifInterface = ExifInterface(inputStream!!)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        Log.d("DBG", "Exif orientation: $orientation")
        inputStream.close()
        orientation
    } catch (e: Exception) {
        Log.d("DBG", "Error getting Exif orientation")
        e.printStackTrace()
        -1
    }
}

fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun saveBitmap(context: Context, bitmap: Bitmap, filename: String) {
    val fos = context.openFileOutput(filename, Context.MODE_PRIVATE)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, fos)
    fos.close()
}




