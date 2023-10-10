package com.example.recipeapp.product

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.recipeapp.R
import com.google.android.datatransport.BuildConfig
import java.io.File
import java.util.Date
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScannerView(viewModel: BarcodeViewModel, navController: NavController) {
    val scanResult by viewModel.scanResult.observeAsState("")

    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
//        TODO: Potential Bug - on other devices Manifest provider needs to match this one
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    var imageBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            capturedImageUri = uri
            imageBitmap = BitmapFactory.decodeFile(file.absolutePath)
            viewModel.getScannedValue(imageBitmap!!)
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

    val openCameraCallback = {
        val permissionCheckResult =
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(uri)
        } else {
            // Request a permission
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    if (scanResult != "") {
        LaunchedEffect(Unit) {
            navController.navigate("add?barcode=${scanResult}")
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Add products", fontSize = 24.sp, modifier = Modifier.padding(8.dp))
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            onClick = (openCameraCallback),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            ),
        ) {
            Column(
                modifier = Modifier

            ) {
                // Card Content
                Column(
                    modifier = Modifier
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.withbarcode),
                        contentDescription = "Product image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(120.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Add product with barcode", fontSize = 18.sp)
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            onClick = {
                viewModel.createBarCode()
                navController.navigate("add?barcode=${scanResult}")
                Log.d("BARCODEADD", "$scanResult")
            },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            ),
        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Card Content
                Column(
                    modifier = Modifier
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.withoutbarcode),
                        contentDescription = "Product image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(120.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Add product without barcode", fontSize = 18.sp)
                    }
                }
            }

            if (scanResult != "") {
                Button(onClick = {
                    navController.navigate("add?barcode=${scanResult}")
                }) {
                    Text(text = "Continue")
                }
                Image(
                    modifier = Modifier
                        .padding(16.dp, 8.dp),
                    painter = rememberAsyncImagePainter(capturedImageUri),
                    contentDescription = null
                )
            }
        }
    }
}


@SuppressLint("SimpleDateFormat")
fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}