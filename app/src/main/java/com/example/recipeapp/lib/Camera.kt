package com.example.recipeapp.lib

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface

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