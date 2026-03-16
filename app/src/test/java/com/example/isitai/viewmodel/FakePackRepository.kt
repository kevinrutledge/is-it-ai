package com.example.isitai.viewmodel

import com.example.isitai.data.model.ContentItem
import com.example.isitai.data.model.DownloadState
import com.example.isitai.data.model.PackMetadata
import com.example.isitai.data.repository.PackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakePackRepository : PackRepository {
    override suspend fun getAvailablePacks(): List<PackMetadata> = emptyList()
    override fun downloadPack(packId: String): Flow<DownloadState> = flowOf()
    override suspend fun deletePack(packId: String) {}
    override fun isInstalled(packId: String): Boolean = false
    override fun getInstalledPackIds(): List<String> = emptyList()
    override fun getPackContent(packId: String): List<ContentItem> = emptyList()
}
