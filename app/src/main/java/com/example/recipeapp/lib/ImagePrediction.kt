package com.example.recipeapp.lib

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.util.concurrent.TimeUnit


interface PredictionApiService {
    @Multipart
    @POST("/predict")
    fun uploadImage(@Part image: MultipartBody.Part): Call<String>
}

object PredictionRetrofitClient {
    private const val BASE_URL = "https://api.fridgemate.jesseguenzl.com"

    //Used for logging the HTTPS requests and responses
    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val predictionApiService: PredictionApiService =
        instance.create(PredictionApiService::class.java)

}

fun uploadImageToServer(file: File, callback: (String) -> Unit) {
    val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
    val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestFile)

    PredictionRetrofitClient.predictionApiService.uploadImage(body).enqueue(object :
        Callback<String> {
        override fun onResponse(call: Call<String>, response: Response<String>) {
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    println(responseBody)
                    callback(responseBody)
                } else {
                    Log.d("PredictionApiService", "Response body is null")
                }
            } else {
                Log.d("PredictionApiService", "Response is not successful")
            }
        }

        override fun onFailure(call: Call<String>, t: Throwable) {
            Log.d("PredictionApiService", "Failed to upload image")
        }
    })
}