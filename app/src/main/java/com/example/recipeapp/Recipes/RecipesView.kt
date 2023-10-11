import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.recipeapp.Recipes.Message
import com.example.recipeapp.Database.Recipe
import com.example.recipeapp.Recipes.RecipeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.recipeapp.components.DotsPulsing

val apiKey = "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"

@Composable
fun IngredientsSelectionScreen(
    navController: NavController,
    viewModel: RecipeViewModel,
) {
    data class IngredientItem(
        val name: String,
        var isSelected: Boolean
    )

    var selectedIngredients by remember { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(Unit) {
        viewModel.fetchProducts()
    }

    val productsList by viewModel.getProductsAsLiveData().observeAsState(emptyList())

    // Create a mutable list of IngredientItem objects and initialize it with product names
    val ingredientItems = remember { mutableStateListOf<IngredientItem>() }

    // Initialize the ingredientItems list with product names
    LaunchedEffect(productsList) {
        val initialItems = productsList.map {
            IngredientItem(
                it.name,
                isSelected = false
            )
        }
        ingredientItems.clear()
        ingredientItems.addAll(initialItems)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column (modifier = Modifier
            .fillMaxWidth()
            .weight(1f)){
            Text(
                text = "Choose Ingredients",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Use LazyVerticalGrid to display ingredients in a grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ingredientItems) { ingredientItem ->

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed = interactionSource.collectIsPressedAsState().value

                    Button(
                        onClick = {
                            // Toggle selection state
                            ingredientItem.isSelected = !ingredientItem.isSelected
                            // Update the selectedIngredients list
                            selectedIngredients = if (ingredientItem.isSelected) {
                                selectedIngredients + ingredientItem.name
                            } else {
                                selectedIngredients - ingredientItem.name
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        interactionSource = interactionSource,
                        colors = ButtonDefaults.buttonColors( containerColor = if (ingredientItem.isSelected) Color.Green else Color.White, contentColor = if (ingredientItem.isSelected) Color.White else Color.Green
                        )

                    ) {
                        Text(
                            text = ingredientItem.name,
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier
                                .padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        Column (modifier = Modifier.fillMaxWidth()){
            var popupVisible by remember { mutableStateOf(false) }
            Button(
                onClick = {
                    if (selectedIngredients.isNotEmpty()) {
                        val ingredientsString = selectedIngredients.joinToString(",")
                        navController.navigate("recipes/$ingredientsString")
                    } else {
                        popupVisible = true
                    }
                },
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(top = 16.dp)
            ) {
                Text(text = "Generate")
                if (popupVisible) {
                    PopupMessage{
                        popupVisible = false
                    }
                }

            }
        }
        }
    }

@Composable
fun PopupMessage(
    onDismiss: () -> Unit
) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(text = "Choose at least one ingredient")
            },
            confirmButton = {
                Button(
                    onClick = { onDismiss() }
                ) {
                    Text(text = "OK")
                }
            })
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RecipesScreen(
    viewModel: RecipeViewModel,
    selectedIngredients: String?,
    navController: NavController

) {

    var response by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedIngredients) {
        if (!selectedIngredients.isNullOrBlank()) {
            val userMessage =
                Message(role = "user", content = "Generate a different recipe with $selectedIngredients")
            viewModel.addMessage(userMessage)
            viewModel.sendMessage(apiKey)?.let {
                val assistantMessage = it.choices[0].message.content
                response = assistantMessage
            }
        }
    }

    val newrecipe = response.split("\n")
    val title = newrecipe[0]
    val body = newrecipe.drop(1).joinToString("\n")
    var showsavescreen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .absolutePadding(top = 12.dp, left = 12.dp, right = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())) {
            if (response.isNullOrEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = CenterHorizontally
                ) {
                    DotsPulsing()
                }

            } else {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(text = body,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Button(onClick = {
                coroutineScope.launch {
                    viewModel.sendMessage(com.example.recipeapp.RecipeView.apiKey)?.let {
                        val assistantMessage = it.choices[0].message.content
                        response = assistantMessage
                    }
                }
            },
                enabled = if (response.isNullOrEmpty()) false else true
                ) {
                Text(text = "Another one")
            }
            Button(onClick =
            { showsavescreen=true },
                enabled = if (response.isNullOrEmpty()) false else true
            ) {
                Text(text = "Save recipe")
            }
        }

    }
    if (showsavescreen){
        Saverecipe(title = title, body = body,viewModel= viewModel) {
            navController.navigate("recipes")
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe, navController: NavController) {
    // Composable for displaying a single recipe item
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate("recipe_description/${recipe.name}")
            }
    ) {

        Text(
            text = recipe.name,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }

}

@Composable
fun Recipedescription(viewModel: RecipeViewModel, name: String) {

    LaunchedEffect(Unit) {
        viewModel.fetchRecipe(name)
    }
    val recipe by viewModel.getRecipeAsLiveData().observeAsState(Recipe("",""))
    var showsavescreen by remember { mutableStateOf(false) }

        Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End
            ) {
            Button(onClick = { showsavescreen = true }) {
                Text(text = "Edit")
            }
            if (showsavescreen) {
                Saverecipe(title = recipe.name, body = recipe.description, viewModel = viewModel) {
                }
            }
        }
        Text(
            text = recipe.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = recipe.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
fun RecipeNavigation() {
    val navController = rememberNavController()
    val viewModel = RecipeViewModel(application = Application())

    NavHost(navController, startDestination = "recipes") {
        //saved recepies
        composable("recipes") {
            Myrecipes(
                navController = navController,
                viewModel = viewModel
            )
        }
        // Define the ingredients selection screen
        composable("ingredients") {
            IngredientsSelectionScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // Define the recipes screen with ingredients as a route parameter
        composable(
            route = "recipes/{ingredients}",
            arguments = listOf(navArgument("ingredients") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedIngredients =
                backStackEntry.arguments?.getString("ingredients")
            RecipesScreen(
                viewModel = viewModel,
                selectedIngredients,
                navController=navController
            )
        }
        composable(
            route = "recipe_description/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipename = backStackEntry.arguments?.getString("name")
            if (recipename != null) {
                Recipedescription(viewModel= viewModel,name = recipename)
            }

        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun Myrecipes(
    navController: NavController,
    viewModel: RecipeViewModel
) {


    var text by remember { mutableStateOf("") }
    var src by remember { mutableStateOf(false) }



    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            // TextField
            TextField(
                value = text,
                onValueChange = {
                    text = it
                },
                maxLines = 1 // Take up remaining horizontal space
            )
            // Button
            Button(
                onClick = {
                    if (!text.isNullOrEmpty())
                        src = false
                },
                modifier = Modifier.padding(start = 8.dp) // Add some spacing between TextField and Button
            ) {
                Text("S")
            }
        }
        val recipesList by viewModel.getRecipesAsLiveData().observeAsState(emptyList())

        if (src) {
            LaunchedEffect(Unit) {
                viewModel.fetchSearchedRecipes(text)
            }
        } else {
            LaunchedEffect(Unit) {
                viewModel.fetchRecipes()
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            items(recipesList) { recipe ->
                RecipeItem(recipe, navController)
            }
        }
        var save by remember {  mutableStateOf(false) }
        Row (modifier = Modifier
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
            Button(onClick = { navController.navigate("ingredients") }) {
                Text(text = "Generate a new recipe")
            }
            Button(onClick = {
                        save = true
             }) {
                Text(text = "+")
            }
            if (save) {
                Saverecipe(title = "", body = "", viewModel = viewModel) {}
            }
        }
    }

}

@Composable
fun Saverecipe(title : String, body:String, viewModel: RecipeViewModel, onDismiss: () -> Unit) {
    var value by remember { mutableStateOf(title) }
    var tmp by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("Title") }
    var currentPage by remember { mutableStateOf(0) }
    var buttontext by remember { mutableStateOf("Next") }
    var isSaveEnabled = remember { mutableStateOf(false) }

    Column(Modifier.padding(36.dp)) {
        AlertDialog(onDismissRequest = {onDismiss()},
            text = {
                Column {
                    Text(text = title, fontSize = 16.sp)

                        TextField(
                            value = value,
                            onValueChange = { newText ->
                                value = newText
                            },
                            Modifier
                                .padding(vertical = 8.dp)
                                .border(
                                    width = 2.dp,
                                    color = if (value.isEmpty()) Color.Red else Color.Transparent
                                ),
                            maxLines = 15
                        )

                    if (value.isEmpty()) {
                        Text("This field cannot be empty", color = Color.Red)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (currentPage == 0) {
                            tmp = value
                            value = body
                            title = "Description"
                            buttontext = "Save"
                        } else if(currentPage == 1) {
                            viewModel.addRecipe(tmp,value)
                            //  delay function
                            onDismiss()
                        }
                        if(currentPage < 1) {
                            currentPage++
                        }
                    },
                    enabled = if (value.isEmpty()) false else true
                ) {
                    Text(buttontext)
                }
            }
        )
    }

}



