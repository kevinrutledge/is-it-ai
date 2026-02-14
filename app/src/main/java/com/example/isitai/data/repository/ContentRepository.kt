package com.example.isitai.data.repository

import android.content.Context
import com.example.isitai.data.model.ContentItem
import com.example.isitai.data.remote.ContentApiService
import kotlinx.serialization.json.Json

class ContentRepository(
    private val apiService: ContentApiService,
    private val context: Context
) {
    var contentSource: ContentSource = ContentSource.BUNDLED
        private set

    suspend fun getContent(): List<ContentItem> {
        return try {
            val items = apiService.getContentManifest()
            contentSource = ContentSource.REMOTE
            items
        } catch (e: Exception) {
            contentSource = ContentSource.BUNDLED
            loadFromAssets()
        }
    }

    private fun loadFromAssets(): List<ContentItem> {
        val json = context.assets.open("content.json")
            .bufferedReader().use { it.readText() }
        return Json.decodeFromString(json)
    }

    enum class ContentSource { REMOTE, BUNDLED }
}
