package com.example.recipeapp

import Database.Recipeapp
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.recipeapp.ChatGPT.ChatRequest
import com.example.recipeapp.ChatGPT.Message
import com.example.recipeapp.ChatGPT.RetrofitInstance
import com.example.recipeapp.RecipeView.apiKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject

class AddingTagsService : Service() {

    // Define a CoroutineScope for the service
    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

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

            val request = ChatRequest(messages = messages)
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
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}