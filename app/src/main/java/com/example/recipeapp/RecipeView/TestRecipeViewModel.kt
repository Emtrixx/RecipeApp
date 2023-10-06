package com.example.recipeapp.RecipeView
import androidx.lifecycle.ViewModel
import com.example.recipeapp.ChatGPT.ChatRequest
import com.example.recipeapp.ChatGPT.ChatResponse
import com.example.recipeapp.ChatGPT.Message
import com.example.recipeapp.ChatGPT.RetrofitInstance


class TestRecipeViewModel() : ViewModel() {

    //ChatGPT Fuctionality

    //val apiKey = "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"
    val baseUrl = "https://api.openai.com/v1/chat/completions"

    val chatMessages = mutableListOf<Message>()

    fun addMessage(message: Message) {
        chatMessages.add(message)
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

    /*
    fun fetchChatResponse(apiKey: String) {
        val userMessage = Message(role = "user", content = "Create a recipe with eggs and bacon")
        addMessage(userMessage)

        viewModelScope.launch {
            val response = sendMessage(apiKey)
            response?.let {
                val assistantMessage =
                    Message(role = "assistant", content = it.choices[0].message.content)
                addMessage(assistantMessage)
                _chatResponse.postValue(it.choices[0].message.content)
            }
        }
    }*/
}
