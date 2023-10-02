package com.example.recipeapp.RecipeView

import Database.Product
import Database.Recipe
import Database.RecipeappViewModel
import android.R
import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException


class RecipeViewModel(application : Application) : AndroidViewModel(application) {

    //ChatGPT Fuctionality
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    val responseBody = mutableStateOf("")

    suspend fun sendMessage(message: String): String {
        val apiKey = ""
        //val apiUrl = "https://api.openai.com/v1/engines/davinci-codex/completions"
        val apiUrl = "https://api.openai.com/v1/chat/completions"

        val jsonMediaType = "application/json".toMediaTypeOrNull()

        var requestBody: RequestBody = FormBody.Builder()
            .add("model", "gpt-3.5-turbo")
            .add("prompt", message)
            .add("max_tokens", "100")
            .build()

        Log.d("TESTBODY", "$requestBody")
            /*
            {
                "model": "gpt-3.5-turbo",
                "messages": [{"role": "user", "content": "hello"}]
                "max_tokens": 50
            } */

        val request = Request.Builder()
            .url(apiUrl)
            .post(requestBody)
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody ?: "Empty response"
                } else {
                    "Error: ${response.code} - ${response.message}"
                }
            } catch (e: IOException) {
                "Network error: ${e.message}"
            }
        }
    }

    private val recipeappViewModel = RecipeappViewModel(application)

    // LiveData to hold the list of products
    private val productsLiveData: LiveData<List<Product>> = recipeappViewModel.getProductsAsLiveData()

    // LiveData to hold the list of recipes
    private val recipesLiveData: LiveData<List<Recipe>> = recipeappViewModel.getRecipesAsLiveData()

    fun getProductsLiveData(): LiveData<List<Product>> {
        return productsLiveData
    }

    fun getRecipesLiveData(): LiveData<List<Recipe>> {
        return recipesLiveData
    }

    fun fetchProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            recipeappViewModel.fetchProducts()
        }
    }

    fun fetchRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            recipeappViewModel.fetchRecipes()
        }
    }

}