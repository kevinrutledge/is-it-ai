package com.example.isitai.data.repository

import android.content.Context
import com.example.isitai.data.model.ContentItem
import com.example.isitai.data.remote.ContentApiService
import kotlinx.serialization.json.Json
import java.io.File

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

    suspend fun getContent(selectedPackIds: Set<String>): List<ContentItem> {
        val coreItems = getContent()
        val downloadedItems = selectedPackIds
            .filter { it != "core" }
            .flatMap { packId -> loadPackFromDisk(packId) }
        return coreItems + downloadedItems
    }

    fun resolveImageUrl(item: ContentItem): String {
        return if (item.packageId == "core") {
            when (contentSource) {
                ContentSource.REMOTE -> BASE_URL + item.filename
                ContentSource.BUNDLED -> "file:///android_asset/${item.filename}"
            }
        } else {
            val packFile = File(context.filesDir, "packs/${item.packageId}/${item.filename}")
            "file://${packFile.absolutePath}"
        }
    }

    private fun loadFromAssets(): List<ContentItem> {
        val json = context.assets.open("content.json")
            .bufferedReader().use { it.readText() }
        return Json.decodeFromString(json)
    }

    private fun loadPackFromDisk(packId: String): List<ContentItem> {
        val manifestFile = File(context.filesDir, "packs/$packId/manifest.json")
        if (!manifestFile.exists()) return emptyList()
        val text = manifestFile.readText()
        return Json.decodeFromString(text)
    }

    enum class ContentSource { REMOTE, BUNDLED }

    companion object {
        private const val BASE_URL = "https://kevinrutledge.github.io/is-it-ai-content/"
    }
}
