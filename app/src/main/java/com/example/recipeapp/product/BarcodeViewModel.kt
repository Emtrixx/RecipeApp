package com.example.recipeapp.product

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

class BarcodeViewModel : ViewModel() {

    val scanResult: MutableLiveData<String> = MutableLiveData<String>("")

    private val barcodeScannerInstance = BarcodeScannerInstance(scanResult)

    fun getScannedValue(bitmap: Bitmap) {
        Log.d("BarcodeViewModel", bitmap.byteCount.toString() + " bytes")
        barcodeScannerInstance.getScannedValue(bitmap)
    }
}

class BarcodeScannerInstance(private val scanResult: MutableLiveData<String>) {

    private val scanner: BarcodeScanner

    //    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
//    camera = cameraProvider.bindToLifecycle(
//        this, cameraSelector, preview, imageAnalysis, imageCapture
//    )
    init {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_ALL_FORMATS,
            )
//            .setZoomSuggestionOptions(
//                ZoomSuggestionOptions.Builder(zoo)
//                    .setMaxSupportedZoomRatio(maxSupportedZoomRatio)
//                    .build()
//            ) // Optional
            .build()

        scanner = BarcodeScanning.getClient(options)
    }

//    private fun setZoom(ZoomRatio: Float): Boolean {
//        if (camera.isClosed()) return false
//        camera.getCameraControl().setZoomRatio(zoomRatio)
//        return true
//    }

    fun getScannedValue(bitmap: Bitmap) {
        scanner.process(bitmap, 0)
            .addOnSuccessListener { barcodes ->
                Log.d("BarcodeScanner", "Success")


//                val first = barcodes.firstOrNull()
//                if (first != null) {
//                    Log.d("BarcodeScanner", "Type: " + first.valueType)
//                    scanResult.postValue(first.rawValue);
//                    return@addOnSuccessListener
//                }

                for (barcode in barcodes) {
//                    val bounds = barcode.boundingBox
//                    val corners = barcode.cornerPoints
                    val rawValue = barcode.rawValue
                    val valueType = barcode.valueType
                    // See API reference for complete list of supported types
                    when (valueType) {
                        Barcode.TYPE_PRODUCT -> {
                            Log.d("BarcodeScanner", "Product Code found")
                            scanResult.postValue(rawValue);
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.d("BarcodeScanner", "Failure")
            }
    }
}