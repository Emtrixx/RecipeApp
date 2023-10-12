package com.example.recipeapp

import Database.Recipeapp
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.recipeapp.Recipes.ChatRequest
import com.example.recipeapp.Recipes.Message
import com.example.recipeapp.Recipes.RetrofitInstance
import com.example.recipeapp.Recipes.apiKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject

class AddingProductFieldsService : Service() {

    // Define a CoroutineScope for the service
    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)
    private val maxTokens = 400;

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d("AddingTagsService", "Service started")
        val context = this
        val barcode = intent?.getStringExtra("barcode")
        serviceScope.launch {
            val db = Recipeapp.getInstance(context)

            if (barcode == null) {
                Log.d("AddingTagsService", "Barcode is null")
                return@launch
            }

            val product = db.RecipeappDao().GetProductInfo(barcode)

            if (product == null) {
                Log.d("AddingTagsService", "Product is null")
                return@launch
            }

            val messages = listOf(
                Message(
                    role = "user", content = "Answer ONLY in the following JSON format:\n" +
                            "{\n" +
                            "  tags: [String]\n" +
                            "}\n" +
                            "\n" +
                            "The list of strings in the tags property should contain a few tags for categorizing this product: ${product.name}"
                )
            )

            val request = ChatRequest(messages = messages, max_tokens = maxTokens)
            val tags = try {
                val response =
                    RetrofitInstance.openAiService.getChatCompletion("Bearer $apiKey", request)
                val jsonString = response.choices[0].message.content

                Log.d("AddingTagsService", "Response String: ${jsonString}")

                val jsonObject = JSONObject(jsonString)
                val jsonArray = jsonObject.getJSONArray("tags")

                val tagsList: List<String> = List(jsonArray.length()) { index ->
                    jsonArray.getString(index)
                }

                tagsList

            } catch (e: Exception) {
                Log.d("AddingTagsService", "Failed to get tags for product: ${product.name}")
                Log.d("AddingTagsService", "Error: ${e.message}")
                null
            } ?: return@launch

            db.RecipeappDao().UpdateProductTags(barcode, tags)

            // Getting carbon footprint
            val messagesCarbon = listOf(
                Message(
                    role = "user",
                    content = "Give a brief summary of the carbon footprint of this product: ${product.name}. Also give specific numbers as to how many kg it produces if possible. Answer only with the text and no response"
                )
            )

            val requestCarbon = ChatRequest(messages = messagesCarbon, max_tokens = maxTokens)
            val carbonText = try {
                val response =
                    RetrofitInstance.openAiService.getChatCompletion(
                        "Bearer $apiKey",
                        requestCarbon
                    )
                val jsonString = response.choices[0].message.content

                jsonString

            } catch (e: Exception) {
                Log.d("AddingTagsService", "Failed to get carbon footprint for: ${product.name}")
                Log.d("AddingTagsService", "Error: ${e.message}")
                null
            } ?: return@launch

            db.RecipeappDao().UpdateProductCarbonFootprint(barcode, carbonText)


            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}