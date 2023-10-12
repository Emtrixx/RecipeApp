import Database.Product
import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.recipeapp.Recipes.Message
import Database.Recipe
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.window.DialogProperties
import com.example.recipeapp.Recipes.RecipeViewModel
import kotlinx.coroutines.launch
import com.example.recipeapp.components.DotsPulsing

const val apiKey = "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"

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
    var buttontext by remember { mutableStateOf("") }
    var allproducts = " "
    if (selectedIngredients.isEmpty()) {
        buttontext = "Surprise me"
    } else {
        buttontext = "Generate"
    }

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
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    CircleShape
                )) {
                Text(
                    text = "Choose Ingredients",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


            // Use LazyVerticalGrid to display ingredients in a grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
            ) {
                items(ingredientItems) { ingredientItem ->

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed = interactionSource.collectIsPressedAsState().value
                    allproducts += ingredientItem.name + ", "

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
                            .padding(6.dp)
                            .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                        interactionSource = interactionSource,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (ingredientItem.isSelected)  MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant,
                            contentColor = if (ingredientItem.isSelected) MaterialTheme.colorScheme.onSurfaceVariant else  MaterialTheme.colorScheme.surfaceVariant
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
            Button(
                onClick = {
                    if (selectedIngredients.isNotEmpty()) {
                        val ingredientsString = selectedIngredients.joinToString(",")
                        navController.navigate("recipes?ingredients=${ingredientsString}?all=${false}")
                    } else {
                        navController.navigate("recipes?ingredients=${allproducts}?all=${true}")
                        Log.d("DBG","recipes?ingredients=${allproducts}?all=${true}")
                    }
                },
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(top = 16.dp),
                colors = ButtonColors(MaterialTheme.colorScheme.primaryContainer,MaterialTheme.colorScheme.onPrimaryContainer,MaterialTheme.colorScheme.onPrimaryContainer,MaterialTheme.colorScheme.primaryContainer),
            ) {
                Text(text = buttontext, modifier = Modifier.padding(8.dp), color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        }
    }


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RecipesScreen(
    viewModel: RecipeViewModel,
    selectedIngredients: String?,
    navController: NavController,
    allproducts: Boolean
) {

    var response by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedIngredients) {
        if (!selectedIngredients.isNullOrBlank()) {
            val userMessage =
                if(allproducts)
                    Message(role = "user", content = "Generate a new recipe with some of these ingredients: $selectedIngredients print only the title, the ingredients and the instructions. if there are some ingredients not eatable don't use them")
            else
                Message(role = "user", content = "Generate a different recipe with these ingredients: $selectedIngredients print only the title, the ingredients and the instructions")
            viewModel.addMessage(userMessage)
            viewModel.sendMessage(apiKey)?.let {
                val assistantMessage = it.choices[0].message.content
                response = assistantMessage
            }
        }
    }

    val newrecipe = response.split("\n")
    val title = newrecipe[0]
    val body = newrecipe.drop(2).joinToString("\n")
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
                .verticalScroll(rememberScrollState())
                .height(10.dp)) {
            if (response.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = CenterHorizontally
                ) {
                    DotsPulsing()
                }

            } else {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.shapes.extraLarge
                    )) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        4.dp,
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.shapes.extraLarge
                    )
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.shapes.extraLarge
                    )) {
                    Column (modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())){
                        Text(
                            text = body,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                }
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
                    viewModel.sendMessage(apiKey)?.let {
                        val assistantMessage = it.choices[0].message.content
                        response = assistantMessage
                    }
                }
            },
                enabled = if (response.isNullOrEmpty()) false else true,
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                )
                ) {
                Text(text = "Another one", modifier = Modifier.padding(8.dp))
            }
            Button(onClick =
            { showsavescreen=true },
                enabled = if (response.isNullOrEmpty()) false else true,
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(text = "Save recipe", modifier = Modifier.padding(8.dp))
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
            .padding(vertical = 6.dp)
            .clickable {
                navController.navigate("recipe_description/${recipe.id}")
            },
    ) {

        Text(
            text = recipe.name,
            modifier = Modifier.padding(vertical = 18.dp, horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }

}

@Composable
fun Recipedescription(viewModel: RecipeViewModel, id: Long, navController: NavController) {

    LaunchedEffect(Unit) {
        viewModel.fetchRecipe(id)
    }
    val recipe by viewModel.getRecipeAsLiveData().observeAsState(Recipe(name ="",description =""))
    var showeditsreen by remember { mutableStateOf(false) }
    var deleterecipe by remember { mutableStateOf(false) }

    val onDeleteItem: (Recipe) -> Unit = { item ->
        viewModel.removeRecipe(recipe = item)
    }

        Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
            ) {
            Button(onClick = { showeditsreen = true }, colors = ButtonColors(MaterialTheme.colorScheme.primaryContainer,MaterialTheme.colorScheme.onPrimaryContainer,MaterialTheme.colorScheme.onPrimaryContainer,MaterialTheme.colorScheme.primaryContainer)) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Edit recipe",
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
            Button(onClick = { deleterecipe = true }, colors = ButtonColors(MaterialTheme.colorScheme.primaryContainer,Color.Red,MaterialTheme.colorScheme.onPrimaryContainer,MaterialTheme.colorScheme.primaryContainer)) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.shapes.extraLarge
                )) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .border(
                    4.dp,
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.shapes.extraLarge
                )
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.shapes.extraLarge
                )) {
                Column (modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())){
                    Text(
                        text = recipe.description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            }

    }
    if (showeditsreen) {
        Editrecipe(recipe = recipe, viewModel = viewModel) {
            navController.navigate("recipe_description/${recipe.id}")
        }
    }
    if (deleterecipe) {
        androidx.compose.material.AlertDialog(
            onDismissRequest = {
                deleterecipe = false
            },
            title = {
                Text(
                    text = "Are you sure you want to delete this item?",
                    color = Color.Black
                )
            },
            text = {
                Text(
                    "This recipe will be removed permanently",
                    color = Color.Black
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteItem(recipe)
                        navController.navigate("recipes")
                    },
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = Color.White,
                        disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                ) {
                    Text("Confirm", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        deleterecipe = false
                    },
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black,
                        disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    border = BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text("Cancel", color = Color.Black)
                }
            }
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
            route = "recipes?ingredients={ingredients}?all={all}",
            arguments = listOf(
                navArgument("ingredients") { type = NavType.StringType },
                navArgument("all") {type = NavType.BoolType}
                )
        ) { backStackEntry ->
            val selectedIngredients =
                backStackEntry.arguments?.getString("ingredients")
            val allproducts = backStackEntry.arguments?.getBoolean("all") ?: true
            RecipesScreen(
                viewModel = viewModel,
                selectedIngredients,
                navController=navController,
                allproducts
            )
        }
        composable(
            route = "recipe_description/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val recipeid = backStackEntry.arguments?.getLong("id")
            if (recipeid != null) {
                Recipedescription(viewModel= viewModel,id = recipeid, navController = navController)
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


    var searchText by remember { mutableStateOf("") }
    val recipesList by viewModel.getRecipesAsLiveData().observeAsState(emptyList())
    var save by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchRecipes()
    }

    val filteredRecipeList = recipesList.filter { recipe ->
        recipe.name.contains(searchText, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            // TextField
            OutlinedTextField(
                value = searchText,
                label = { Text(text = "Search Recipes") },
                singleLine = true,
                shape = CircleShape,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 8.dp),
                onValueChange = { newQuery -> searchText = newQuery },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done

                ),
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedBorderColor = Color.Transparent,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(filteredRecipeList) { recipe ->
                RecipeItem(recipe, navController)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { navController.navigate("ingredients") }, modifier = Modifier.weight(1f), colors = ButtonColors(MaterialTheme.colorScheme.primaryContainer,Color.White,MaterialTheme.colorScheme.onPrimaryContainer,MaterialTheme.colorScheme.primaryContainer)) {
                Text(text = "Generate a new recipe",modifier = Modifier.padding(vertical = 8.dp))
            }
            Button(onClick = {
                save = true
            },modifier = Modifier.weight(0.3f), colors = ButtonColors(MaterialTheme.colorScheme.primaryContainer,Color.White,MaterialTheme.colorScheme.onPrimaryContainer,MaterialTheme.colorScheme.primaryContainer)) {
                Icon( Icons.Filled.Add,
                    contentDescription = "New recipe",
                    modifier = Modifier.padding(vertical = 6.dp))
            }
        }

    }
    if (save) {
        Saverecipe(title = "", body = "", viewModel = viewModel) {
            navController.navigate("recipes")
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

    Column(Modifier.padding(36.dp)) {
        AlertDialog(onDismissRequest = {onDismiss()},
            text = {
                Column {
                    Text(text = title, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
                        OutlinedTextField(
                            value = value,
                            onValueChange = { newText ->
                                value = newText
                            },
                            Modifier
                                .padding(vertical = 8.dp),
                            maxLines = 15,
                            shape = MaterialTheme.shapes.extraLarge,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = if (value.isEmpty()) Color.Red else MaterialTheme.colorScheme.primaryContainer,
                                unfocusedBorderColor = if (value.isEmpty()) Color.Red else Color.Transparent,
                                focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )

                    if (value.isEmpty()) {
                        Text("This field cannot be empty", color = Color.Red, modifier = Modifier.padding(horizontal = 8.dp))
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
                            onDismiss()
                        }
                        if(currentPage < 1) {
                            currentPage++
                        }
                    },
                    enabled = if (value.isEmpty()) false else true
                ){
                    Text(buttontext)
                }
            },
            properties = DialogProperties(dismissOnBackPress = true)
        )
    }

}

@Composable
fun Editrecipe(recipe: Recipe, viewModel: RecipeViewModel, onDismiss: () -> Unit) {
    var value by remember { mutableStateOf(recipe.name) }
    var tmp by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("Title") }
    var currentPage by remember { mutableStateOf(0) }
    var buttontext by remember { mutableStateOf("Next") }

    Column(Modifier.padding(36.dp)) {
        AlertDialog(onDismissRequest = {onDismiss()},
            text = {
                Column {
                    Text(text = title, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
                    OutlinedTextField(
                        value = value,
                        onValueChange = { newText ->
                            value = newText
                        },
                        Modifier
                            .padding(vertical = 8.dp),
                        maxLines = 15,
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = if (value.isEmpty()) Color.Red else MaterialTheme.colorScheme.primaryContainer,
                            unfocusedBorderColor = if (value.isEmpty()) Color.Red else Color.Transparent,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )

                    if (value.isEmpty()) {
                        Text("This field cannot be empty", color = Color.Red, modifier = Modifier.padding(horizontal = 8.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (currentPage == 0) {
                            tmp = value
                            value = recipe.description
                            title = "Description"
                            buttontext = "Save"
                        } else if(currentPage == 1) {
                            viewModel.editRecipe(recipe.id,tmp,value)
                            onDismiss()
                        }
                        if(currentPage < 1) {
                            currentPage++
                        }
                    },
                    enabled = if (value.isEmpty()) false else true
                ){
                    Text(buttontext)
                }
            },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        )
    }
}



