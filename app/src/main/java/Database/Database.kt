package Database

import android.app.Application
import android.content.Context
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import androidx.room.ColumnInfo

@Database(entities = [Product::class, Recipe::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class, ListStringConverter::class)
abstract class Recipeapp : RoomDatabase() {
    abstract fun RecipeappDao(): ProductRecipeDao
    companion object {
        @Volatile
        private var INSTANCE: Recipeapp? = null

        fun getInstance(context: Context): Recipeapp {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Recipeapp::class.java,
                    "Recipeapp"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}



@Entity
data class Product(
    @PrimaryKey val barcode: String,
    @NonNull val name: String,
    @TypeConverters(DateConverter::class) val bestbefore: Date,
    val description: String,
    @NonNull val amount: Double,
    @TypeConverters(ListStringConverter::class) val tags: List<String>,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val image: ByteArray
)

@Entity
data class Recipe(
    @TypeConverters(ListStringConverter::class) @NonNull val ingredients: List<String>,
    @NonNull @PrimaryKey val name: String,
    val description: String,
    @TypeConverters(ListStringConverter::class) val tags: List<String>
)


@Dao
interface ProductRecipeDao {
    @Query("SELECT * FROM Product")
    suspend  fun GetProducts(): List<Product>

    @Query("SELECT * FROM Recipe")
    suspend  fun GetRecips(): List<Recipe>

    @Query("SELECT * FROM Product WHERE barcode = :barcode")
    suspend fun GetProductInfo(barcode: String): Product

    @Query("SELECT * FROM Recipe WHERE name = :name")
    suspend fun GetrecipeInfo(name: String): Recipe

    @Insert
    suspend  fun InsertRecipe(vararg recipe: Recipe)

    @Insert
    suspend  fun InsertProduct(vararg product: Product)

}

class RecipeappViewModel(application: Application) : AndroidViewModel(application) {
    private val db: Recipeapp by lazy {
        Room.databaseBuilder(
            application.applicationContext,
            Recipeapp::class.java, "Recipeapp"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    private val productsLiveData: MutableLiveData<List<Product>> = MutableLiveData()
    private val recipesLiveData: MutableLiveData<List<Recipe>> = MutableLiveData()

    fun getProductsAsLiveData(): LiveData<List<Product>> {
        return productsLiveData
    }

    fun getRecipesAsLiveData(): LiveData<List<Recipe>> {
        return recipesLiveData
    }

    fun fetchProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            val products = db.RecipeappDao().GetProducts()
            productsLiveData.postValue(products)
        }
    }

    fun fetchRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            val recipes = db.RecipeappDao().GetRecips()
            recipesLiveData.postValue(recipes)
        }
    }

    fun addProduct( barcode: String,
                    name: String,
                    bestbefore: Date,
                    description: String,
                    amount: Double,
                    tags: List<String>,
                    image: ByteArray) {
        val p = Product(barcode, name, bestbefore, description, amount, tags, image)
        viewModelScope.launch { db.RecipeappDao().InsertProduct(p) }
    }

    fun addRecipe(ingridients: List<String>,
                  name: String,
                  description: String,
                  tags: List<String>) {
        val r = Recipe(ingridients, name, description, tags)
        viewModelScope.launch { db.RecipeappDao().InsertRecipe(r) }
    }

}

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
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

/*val database = Recipeapp.getInstance(applicationContext)
            val viewModel: RecipeappViewModel by viewModels()
            val actorList by viewModel.getProductsAsLiveData().observeAsState(initial = emptyList())
            LaunchedEffect(Unit) {
                viewModel.fetchProducts()
            }*/