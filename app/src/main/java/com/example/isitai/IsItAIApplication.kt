package com.example.isitai

import android.app.Application
import com.example.isitai.data.dataStore
import com.example.isitai.data.local.FileDownloader
import com.example.isitai.data.remote.ContentApiService
import com.example.isitai.data.repository.ContentRepository
import com.example.isitai.data.repository.ContentRepositoryImpl
import com.example.isitai.data.repository.PackRepository
import com.example.isitai.data.repository.PackRepositoryImpl
import com.example.isitai.data.repository.UserPreferencesRepository
import com.example.isitai.data.repository.UserPreferencesRepositoryImpl
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File

class IsItAIApplication : Application() {
    lateinit var contentRepository: ContentRepository
    lateinit var packRepository: PackRepository
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()

        val baseUrl = "https://kevinrutledge.github.io/is-it-ai-content/"

        val cacheDir = File(cacheDir, "http_cache")
        val okHttpClient = OkHttpClient.Builder()
            .cache(Cache(cacheDir, 50L * 1024L * 1024L))
            .build()

        val json = Json { ignoreUnknownKeys = true }

        val contentService: ContentApiService by lazy {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(ContentApiService::class.java)
        }

        val fileDownloader = FileDownloader(okHttpClient)

        contentRepository = ContentRepositoryImpl(contentService, applicationContext)
        packRepository = PackRepositoryImpl(contentService, fileDownloader, applicationContext, baseUrl)
        userPreferencesRepository = UserPreferencesRepositoryImpl(dataStore)
    }
}
