package com.example.recipeapp.shopping

import Database.Recipeapp
import Database.ShoppingItem
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingViewModel(application: Application) : AndroidViewModel(application) {
    private val db: Recipeapp by lazy {
        Recipeapp.getInstance(application)
    }

    //private val favoritesLiveData: MutableLiveData<List<ShoppingItem>> = MutableLiveData()
    private val shoppingLiveData: MutableLiveData<List<ShoppingItem>> = MutableLiveData()

    // Expose the shopping list data as LiveData
    fun getShoppingListLiveData(): LiveData<List<ShoppingItem>> {
        return shoppingLiveData
    }

    // Expose favorites data as LiveData
    //fun getFavoritesLiveData(): LiveData<List<ShoppingItem>> {
      //  return favoritesLiveData
    //}

    // Function to fetch the shopping list data
    fun fetchShoppingList() {
        viewModelScope.launch(Dispatchers.IO) {
            val shoppingItems = db.shoppingItemDao().getAllShoppingItems()
            shoppingLiveData.postValue(shoppingItems)
        }
    }

    // Function to add a shopping item
    fun addShoppingItem(name: String, amount: Int) {
        val shoppingItem = ShoppingItem(name = name, amount = amount)
        viewModelScope.launch(Dispatchers.IO) {
            db.shoppingItemDao().insertShoppingItem(shoppingItem)
            // After adding, fetch the updated shopping list
            fetchShoppingList()
        }
    }

    //Delete items
    fun deleteShoppingItem(item: ShoppingItem) {
        viewModelScope.launch(Dispatchers.IO) {
            db.shoppingItemDao().deleteShoppingItem(item)
            // After deleting, fetch the updated shopping list
            fetchShoppingList()
        }
    }

    fun incrementAddedCount(itemName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.shoppingItemDao().incrementAddedCount(itemName)
            fetchShoppingList()
        }
    }
}
