package com.example.recipeapp.RecipeView


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.recipeapp.ChatGPT.Message
import com.example.recipeapp.R
import kotlinx.coroutines.launch

// Replace with your api key
val apiKey = "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"

@Composable
fun RecipeViewTest(viewModel: TestRecipeViewModel) {
    var message by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    var response by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                val userMessage = Message(role = "user", content = "Generate a recipe with eggs")
                viewModel.addMessage(userMessage)
                coroutineScope.launch {
                    viewModel.sendMessage(apiKey)?.let {
                        val assistantMessage = it.choices[0].message.content
                        response = assistantMessage
                    }
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Generate a recipe")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = response,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(16.dp),
            color = Color.White
        )
    }
}
