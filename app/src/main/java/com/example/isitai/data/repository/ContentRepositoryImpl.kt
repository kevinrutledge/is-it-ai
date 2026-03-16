package com.example.isitai.data.repository

import android.content.Context
import com.example.isitai.data.model.ContentItem
import com.example.isitai.data.remote.ContentApiService
import kotlinx.serialization.json.Json
import java.io.File

class ContentRepositoryImpl(
    private val apiService: ContentApiService,
    private val context: Context
) : ContentRepository {
    override suspend fun getContent(): List<ContentItem> {
        return try {
            apiService.getContentManifest()
        } catch (_: Exception) {
            loadFromAssets()
        }
    }

    override suspend fun getContent(selectedPackIds: Set<String>): List<ContentItem> {
        val coreItems = getContent()
        val downloadedItems = selectedPackIds
            .filter { it != "core" }
            .flatMap { packId -> loadPackFromDisk(packId) }
        return coreItems + downloadedItems
    }

    override fun resolveImageUrl(item: ContentItem): String {
        return if (item.packageId == "core") {
            "file:///android_asset/${item.filename}"
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
        return try {
            Json.decodeFromString(manifestFile.readText())
        } catch (_: Exception) {
            emptyList()
        }
    }
}
