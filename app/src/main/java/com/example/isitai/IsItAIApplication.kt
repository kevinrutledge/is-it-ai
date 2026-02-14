package com.example.isitai

import android.app.Application
import com.example.isitai.data.remote.ContentApiService
import com.example.isitai.data.repository.ContentRepository
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File

class IsItAIApplication : Application() {
    lateinit var contentRepository: ContentRepository

    override fun onCreate() {
        super.onCreate()

        val cacheDir = File(cacheDir, "http_cache")
        val okHttpClient = OkHttpClient.Builder()
            .cache(Cache(cacheDir, 50L * 1024L * 1024L))
            .build()

        val json = Json { ignoreUnknownKeys = true }

        val contentService: ContentApiService by lazy {
            Retrofit.Builder()
                .baseUrl("https://kevinrutledge.github.io/is-it-ai-content/")
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(ContentApiService::class.java)
        }

        contentRepository = ContentRepository(contentService, applicationContext)
    }
}
