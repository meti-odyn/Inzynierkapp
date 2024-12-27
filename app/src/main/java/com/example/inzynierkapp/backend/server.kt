package com.example.inzynierkapp.backend

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

fun sendRequestToServer(
    prompt: String,
    onResult: (String, String) -> Unit,
    onError: (String) -> Unit
) {
    val client = OkHttpClient.Builder()
        .connectTimeout(600, TimeUnit.SECONDS)
        .readTimeout(600, TimeUnit.SECONDS)
        .writeTimeout(600, TimeUnit.SECONDS)
        .build()

    //val url = "http://150.254.3.82:5000/generate_summary_questions"
    val url = "https://d421-150-254-3-82.ngrok-free.app/generate_summary_questions"
    val json = JSONObject()
    json.put("prompt", prompt)

    val mediaType = "application/json; charset=utf-8".toMediaType()
    val requestBody = json.toString().toRequestBody(mediaType)

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onError("Błąd połączenia: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            if (responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
                val summary = jsonResponse.optString("summary", "Brak streszczenia")
                val questions = jsonResponse.optString("questions", "Brak pytań")
                onResult(summary, questions)
            } else {
                onError("Odpowiedź serwera była pusta.")
            }
        }
    })
}
