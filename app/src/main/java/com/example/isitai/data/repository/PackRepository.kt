package com.example.isitai.data.repository

import com.example.isitai.data.model.ContentItem
import com.example.isitai.data.model.DownloadState
import com.example.isitai.data.model.PackMetadata
import kotlinx.coroutines.flow.Flow

interface PackRepository {
    suspend fun getAvailablePacks(): List<PackMetadata>
    fun downloadPack(packId: String): Flow<DownloadState>
    suspend fun deletePack(packId: String)
    fun isInstalled(packId: String): Boolean
    fun getInstalledPackIds(): List<String>
    fun getPackContent(packId: String): List<ContentItem>
}
