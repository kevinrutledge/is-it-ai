package com.example.isitai.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

class FileDownloader(private val client: OkHttpClient) {
    suspend fun downloadFile(url: String, destination: File) {
        withContext(Dispatchers.IO) {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IOException("Download failed: ${response.code}")
            }
            response.body?.byteStream()?.use { input ->
                destination.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: throw IOException("Empty response body")
        }
    }
}
