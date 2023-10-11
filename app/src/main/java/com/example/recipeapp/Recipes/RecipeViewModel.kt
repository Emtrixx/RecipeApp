package com.example.recipeapp.Recipes

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.recipeapp.Database.Product
import com.example.recipeapp.Database.Recipe
import com.example.recipeapp.Database.Recipeapp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeViewModel (application: Application) : AndroidViewModel(application) {
        private val db: Recipeapp by lazy {
            Recipeapp.getInstance(application)
        }

        private val productsLiveData: MutableLiveData<List<Product>> = MutableLiveData()
        private val recipesLiveData: MutableLiveData<List<Recipe>> = MutableLiveData()
        private val recipeLiveData: MutableLiveData<Recipe> = MutableLiveData()

        fun getProductsAsLiveData(): LiveData<List<Product>> {
            return productsLiveData
        }

        fun getRecipesAsLiveData(): LiveData<List<Recipe>> {
            return recipesLiveData
        }

        fun getRecipeAsLiveData(): LiveData<Recipe> {
            return recipeLiveData
        }

        fun fetchRecipe(name: String) {
            viewModelScope.launch(Dispatchers.IO) {
                val Recipe = db.RecipeappDao().GetRecipeInfo(name)
                recipeLiveData.postValue(Recipe)
            }
        }

        fun fetchProducts() {
            viewModelScope.launch(Dispatchers.IO) {
                val products = db.RecipeappDao().GetProducts()
                productsLiveData.postValue(products)
            }
        }

        fun fetchRecipes() {
            viewModelScope.launch(Dispatchers.IO) {
                val recipes = db.RecipeappDao().GetRecipes()
                recipesLiveData.postValue(recipes)
            }
        }

    fun fetchSearchedRecipes(search: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val recipes = db.RecipeappDao().GetSearchedRecipes(search)
            recipesLiveData.postValue(recipes)
        }
    }

        fun addRecipe(
                      name: String,
                      description: String, ) {
            val r = Recipe( name, description)
            viewModelScope.launch { db.RecipeappDao().InsertRecipe(r) }
        }

    val chatMessages = mutableListOf<Message>()

    fun addMessage(message: Message) {
        chatMessages.add(0,message)
    }

    suspend fun sendMessage(apiKey: String): ChatResponse? {
        val request = ChatRequest(messages = chatMessages)
        return try {
            val response =
                RetrofitInstance.openAiService.getChatCompletion("Bearer $apiKey", request)
            response
        } catch (e: Exception) {
            null
        }
    }

    }
