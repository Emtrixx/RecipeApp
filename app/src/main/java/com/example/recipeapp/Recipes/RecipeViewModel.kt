package com.example.recipeapp.Recipes

import Database.Product
import Database.Recipe
import Database.Recipeapp
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val apiKey = "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"

class RecipeViewModel(context: Context) : ViewModel() {
    private val db: Recipeapp by lazy {
        Recipeapp.getInstance(context)
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

    fun fetchRecipe(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val Recipe = db.RecipeappDao().GetRecipeInfo(id)
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

    fun removeRecipe(recipe: Recipe) {
        viewModelScope.launch() {
            db.RecipeappDao().deleteRecipeById(recipe.id)
            val recipes = db.RecipeappDao().GetRecipes()
            recipesLiveData.postValue(recipes)
        }
        fetchRecipes()
    }

    fun addRecipe(
        name: String,
        description: String,
    ) {
        val recipe = Recipe(name = name, description = description)
        viewModelScope.launch { db.RecipeappDao().InsertRecipe(recipe) }
        fetchRecipes()
    }

    fun editRecipe(id: Long, name: String, description: String) {
        viewModelScope.launch {
            val editedRecipe = Recipe(id, name, description)
            db.RecipeappDao().UpdateRecipe(editedRecipe)
        }
        fetchRecipe(id)
    }

    val chatMessages = mutableListOf<Message>()

    fun addMessage(message: Message) {
        chatMessages.add(0, message)
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
