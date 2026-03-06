package com.example.isitai.data.repository

import android.content.Context
import com.example.isitai.data.local.FileDownloader
import com.example.isitai.data.model.ContentItem
import com.example.isitai.data.model.DownloadState
import com.example.isitai.data.model.PackMetadata
import com.example.isitai.data.remote.ContentApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class PackRepository(
    private val apiService: ContentApiService,
    private val fileDownloader: FileDownloader,
    private val context: Context
) {
    private val packsDir: File get() = File(context.filesDir, "packs")
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val BASE_URL = "https://kevinrutledge.github.io/is-it-ai-content/"
    }

    suspend fun getAvailablePacks(): List<PackMetadata> {
        return apiService.getPackManifest()
    }

    fun downloadPack(packId: String): Flow<DownloadState> = flow {
        val tempDir = File(packsDir, "${packId}_temp")
        val finalDir = File(packsDir, packId)
        try {
            tempDir.mkdirs()
            File(tempDir, "images").mkdirs()

            val items = apiService.getPackContent(packId)
            val total = items.size + 1

            emit(DownloadState.Downloading(current = 0, total = total))

            val manifestJson = json.encodeToString(items)
            File(tempDir, "manifest.json").writeText(manifestJson)
            emit(DownloadState.Downloading(current = 1, total = total))

            items.forEachIndexed { index, item ->
                val imageUrl = "${BASE_URL}packs/$packId/${item.filename}"
                val destination = File(tempDir, item.filename)
                destination.parentFile?.mkdirs()
                fileDownloader.downloadFile(imageUrl, destination)
                emit(DownloadState.Downloading(current = index + 2, total = total))
            }

            finalDir.deleteRecursively()
            tempDir.renameTo(finalDir)
            emit(DownloadState.Installed)
        } catch (e: Exception) {
            tempDir.deleteRecursively()
            emit(DownloadState.Error(e.message ?: "Download failed"))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun deletePack(packId: String) {
        val packDir = File(packsDir, packId)
        packDir.deleteRecursively()
    }

    fun isInstalled(packId: String): Boolean {
        return File(packsDir, "$packId/manifest.json").exists()
    }

    fun getInstalledPackIds(): List<String> {
        val dir = packsDir
        if (!dir.exists()) return emptyList()
        return dir.listFiles()
            ?.filter { it.isDirectory && File(it, "manifest.json").exists() }
            ?.map { it.name }
            ?: emptyList()
    }

    fun getPackContent(packId: String): List<ContentItem> {
        val manifestFile = File(packsDir, "$packId/manifest.json")
        if (!manifestFile.exists()) return emptyList()
        val text = manifestFile.readText()
        return json.decodeFromString(text)
    }
}
