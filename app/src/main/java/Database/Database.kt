package Database

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Upsert
import java.time.LocalDate
import androidx.room.Update

@Database(entities = [Product::class, Recipe::class, ShoppingItem::class, FavoriteShoppingItem::class], version = 1, exportSchema = false)
@TypeConverters(ListDateConverter::class, ListStringConverter::class)
abstract class Recipeapp : RoomDatabase() {
    abstract fun RecipeappDao(): ProductRecipeDao
    abstract fun shoppingItemDao(): ShoppingItemDao
    abstract fun favoriteShoppingItemDao(): FavoriteShoppingItemDao

    companion object {
        @Volatile
        private var INSTANCE: Recipeapp? = null

        fun getInstance(context: Context): Recipeapp {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Recipeapp::class.java,
                    "Recipeapp"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
@Entity
data class Product(
    @PrimaryKey val barcode: String,
    val name: String,
    @TypeConverters(ListDateConverter::class) val bestbefore: List<LocalDate?>,
    val description: String,
    val amount: Int,
    @TypeConverters(ListStringConverter::class) val tags: List<String>,
    val image: String?,
    val carbonFootprint: String?
)

@Entity
data class Recipe(
    @PrimaryKey (autoGenerate = true)
    val id: Long = 0,
    @NonNull val name: String,
    val description: String,
)
@Entity
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: String,
    var addedCount: Int = 0,
    var checked: Boolean = false
)

@Entity
data class FavoriteShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)
@Dao
interface ProductRecipeDao {
    @Query("SELECT * FROM Product")
    suspend  fun GetProducts(): List<Product>

    @Query("SELECT * FROM Recipe")
    suspend  fun GetRecipes(): List<Recipe>

    @Query("SELECT * FROM Product WHERE barcode = :barcode")
    suspend fun GetProductInfo(barcode: String): Product?

    @Query("DELETE FROM Product WHERE barcode = :barcode")
    suspend fun deleteProductById(barcode: String)

    @Query("DELETE FROM Recipe WHERE id = :id")
    suspend fun deleteRecipeById(id: Long)

    @Query("SELECT * FROM Recipe WHERE id = :id")
    suspend fun GetRecipeInfo(id: Long): Recipe

    @Insert
    suspend  fun InsertRecipe(vararg recipe: Recipe)

    @Insert
    suspend  fun InsertProduct(vararg product: Product)

    @Upsert
    suspend  fun UpsertProduct(vararg product: Product)

    @Upsert
    suspend  fun UpsertRecipe(vararg recipe: Recipe)

    @Update
    suspend fun UpdateRecipe(vararg recipe: Recipe)

    @Query("UPDATE Product SET tags = :newTags WHERE barcode = :barcode")
    suspend fun UpdateProductTags(barcode: String, newTags: List<String>)

    @Query("UPDATE Product SET carbonFootprint = :carbonFootprint WHERE barcode = :barcode")
    suspend fun UpdateProductCarbonFootprint(barcode: String, carbonFootprint: String)
}

@Dao
interface ShoppingItemDao {
    @Query("SELECT * FROM ShoppingItem")
    suspend fun getAllShoppingItems(): List<ShoppingItem>

    @Insert
    suspend fun insertShoppingItem(shoppingItem: ShoppingItem)

    @Query("UPDATE ShoppingItem SET addedCount = addedCount + 1 WHERE name = :name")
    suspend fun incrementAddedCount(name: String)

    @Query("SELECT addedCount FROM ShoppingItem WHERE name = :itemName")
    suspend fun getAddedCountByName(itemName: String): Int

    @Delete
    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

    @Delete
    suspend fun deleteShoppingItems(items: List<ShoppingItem>)

    @Update
    suspend fun updateShoppingItem(item: ShoppingItem)

    @Query("SELECT * FROM ShoppingItem WHERE name = :name")
    suspend fun getShoppingItemByName(name: String): ShoppingItem?
}

@Dao
interface FavoriteShoppingItemDao {
    @Query("SELECT * FROM FavoriteShoppingItem")
    suspend fun getFavoriteItems(): List<FavoriteShoppingItem>
    @Insert
    suspend fun insertFavoriteItem(favoriteItem: FavoriteShoppingItem)
}

class DateConverter {
    @TypeConverter
    fun fromDateString(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun toDateString(date: LocalDate): String {
        return date.toString()
    }
}

class ListStringConverter {
    @TypeConverter
    fun fromListString(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toListString(value: String): List<String> {
        return value.split(",")
    }
}

class ListDateConverter {
    @TypeConverter
    fun fromListDate(list: List<LocalDate?>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toListDate(value: String): List<LocalDate?> {
        return value.split(",").map {
            if (it == "null") {
                return@map null
            }
            LocalDate.parse(it)
        }
    }
}
