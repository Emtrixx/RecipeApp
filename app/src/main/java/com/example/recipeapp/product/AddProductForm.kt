package com.example.recipeapp.product

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.recipeapp.Navigation.BottomNavItem
import com.example.recipeapp.R
import com.example.recipeapp.components.IntegerInputStepper
import com.example.recipeapp.components.MyDatePickerDialog
import com.example.recipeapp.components.camera.CameraComponent
import com.example.recipeapp.components.camera.saveImageToInternalStorage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductForm(viewModel: AddProductViewModel, navController: NavController) {

    val context = LocalContext.current

    val name = viewModel.name
    val description = viewModel.description
    val bestBeforeList: List<String?> = viewModel.bestBeforeList
    val amount = viewModel.amount
    val capturedImageUri = viewModel.capturedImageUri
    val storedImage = viewModel.storedImage

    val barcode = viewModel.barcode

    val painter = if (storedImage != null && capturedImageUri == Uri.EMPTY) {
        rememberAsyncImagePainter(storedImage)
    } else {
        rememberAsyncImagePainter(capturedImageUri)
    }

    Column {
        TopAppBar(
            title = {
                Text(text = "Add Product: $barcode")
            },
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigate(BottomNavItem.AddItem.screen)
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = name,
                label = { Text(text = stringResource(R.string.addProductNameLabel)) },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onValueChange = { viewModel.updateName(it) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
            )
            OutlinedTextField(
                value = description,
                label = { Text(text = stringResource(R.string.addProductDescriptionLabel)) },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onValueChange = { viewModel.updateDescription(it) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
            )

            IntegerInputStepper(
                value = amount,
                onValueChange = { viewModel.updateAmount(it) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp),
                minValue = 1,
                maxValue = 100
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = "Best Before Dates")
                bestBeforeList.forEachIndexed { index, date ->
                    // Assuming there's a DatePicker or similar component to pick dates
                    ProductFormDatePicker(
                        date = date,
                        onDateSelected = {
                            viewModel.updateBestBefore(it, index)
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .height(200.dp)
                    .width(200.dp)
                    .padding(16.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
//                        .padding(16.dp, 8.dp)
//                        .align(Alignment.CenterHorizontally)
                        .width(200.dp)
                        .height(200.dp),
                    painter = painter,
                    contentDescription = stringResource(R.string.product_image_desc)
                )
                CameraComponent(
                    Modifier
                        .width(200.dp)
                        .height(200.dp)
                        .alpha(0f)
                ) { success, uri ->
                    if (success) {
                        viewModel.updatePicture(uri)
                    }
                }
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Camera Icon")
            }

            Button(modifier = Modifier.padding(8.dp), onClick = {
                val path = saveImageToInternalStorage(context, capturedImageUri, barcode)
                viewModel.updateSavedImagePath(path)

                viewModel.upsertProduct()
                navController.navigate(BottomNavItem.Home.screen)
            }) {
                Text(text = "Save")
            }
        }
    }
}

@Composable
fun ProductFormDatePicker(date: String?, onDateSelected: (String) -> Unit) {

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    val dateText = date ?: run {
        "Open date picker dialog"
    }

//    val initialSelectedDateMillis = convertDateToMillis(date)

    Box(contentAlignment = Alignment.Center) {
        Button(modifier = Modifier.padding(8.dp), onClick = { showDatePicker = true }) {
            Text(text = dateText)
        }
    }

    if (showDatePicker) {
        MyDatePickerDialog(
            onDateSelected = onDateSelected,
            onDismiss = { showDatePicker = false },
//            initialSelectedDateMillis = initialSelectedDateMillis
        )
    }
}

