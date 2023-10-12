package com.example.recipeapp.product.components

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
fun rememberUpdatedPainter(uri: Uri, fileChangeKey: Long): Painter {
    val context = LocalContext.current
    // to trigger recomposition when file changes
    key(fileChangeKey) {
        return rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(uri)
                .crossfade(true).memoryCachePolicy(
                    CachePolicy.DISABLED
                ).diskCachePolicy(
                    CachePolicy.DISABLED
                )
                .build()
        )
    }
}