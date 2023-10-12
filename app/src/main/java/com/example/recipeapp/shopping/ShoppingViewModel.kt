package com.example.recipeapp.shopping

import Database.FavoriteShoppingItem
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

    private val shoppingLiveData: MutableLiveData<List<ShoppingItem>> = MutableLiveData()
    private val favoriteLiveData: MutableLiveData<List<FavoriteShoppingItem>> = MutableLiveData()
    private val addedCountMap: MutableMap<String, Int> = mutableMapOf()

    // Expose the shopping list data as LiveData
    fun getShoppingListLiveData(): LiveData<List<ShoppingItem>> {
        return shoppingLiveData
    }

    // Function to fetch the shopping list data
    fun fetchShoppingList() {
        viewModelScope.launch(Dispatchers.IO) {
            val shoppingItems = db.shoppingItemDao().getAllShoppingItems()
            // Check the count for each item in the shopping list and add to favorites if needed
            for (item in shoppingItems) {
                val count = addedCountMap.getOrDefault(item.name, 0)
                if (count >= 3) {
                    addFavoriteItem(item.name)
                }
            }
            shoppingLiveData.postValue(shoppingItems)
        }
    }

    // Function to add a shopping item
    fun addShoppingItem(name: String, amount: String) {
        val shoppingItem = ShoppingItem(name = name, amount = amount)
        viewModelScope.launch(Dispatchers.IO) {
            db.shoppingItemDao().insertShoppingItem(shoppingItem)
            // After adding, increment the count in the map
            val count = addedCountMap.getOrDefault(name, 0)
            addedCountMap[name] = count + 1
            // Check if the item should be added to favorites
            if (count + 1 >= 3) {
                addFavoriteItem(name)
            }
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

    // Increases the item count
    fun incrementAddedCount(itemName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.shoppingItemDao().incrementAddedCount(itemName)
            //fetchFavoriteItems()

            // Fetch the updated count
            val updatedCount = db.shoppingItemDao().getAddedCountByName(itemName)

            // Check if the updated count is >= 2, then add to favorites
            if (updatedCount >= 2) {
                addFavoriteItem(itemName)
            }

            // Fetch the updated shopping list
            fetchShoppingList()
        }
    }

    // Get the list of favorite items
    fun getFavoriteItems(): LiveData<List<FavoriteShoppingItem>> {
        return favoriteLiveData
    }

    // Function to fetch favorite items
    fun fetchFavoriteItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val favoriteItemsFromDb = db.favoriteShoppingItemDao().getFavoriteItems()
            favoriteLiveData.postValue(favoriteItemsFromDb)
        }
    }

    private val addedToFavoritesSet = mutableSetOf<String>()
    // Function to add an item to favorites
    fun addFavoriteItem(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!addedToFavoritesSet.contains(name)) {
                addedToFavoritesSet.add(name)

                val favoriteItem = FavoriteShoppingItem(name = name)
                db.favoriteShoppingItemDao().insertFavoriteItem(favoriteItem)
                fetchFavoriteItems()
            }
        }
    }

    // Add from favorites to shopping list
    fun addFavoriteToShoppingList(item: FavoriteShoppingItem) {
        addShoppingItem(item.name, amount = 1)
    }
}
