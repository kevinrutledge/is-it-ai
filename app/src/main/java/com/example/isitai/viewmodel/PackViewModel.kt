package com.example.isitai.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.isitai.IsItAIApplication
import com.example.isitai.data.model.DownloadState
import com.example.isitai.data.model.PackMetadata
import com.example.isitai.data.repository.ContentRepository
import com.example.isitai.data.repository.PackRepository
import com.example.isitai.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PackViewModel(
    private val contentRepository: ContentRepository,
    private val packRepository: PackRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    var coreItemCount by mutableStateOf(0)
        private set

    var availablePacks by mutableStateOf<List<PackMetadata>>(emptyList())
        private set
    var downloadStates by mutableStateOf<Map<String, DownloadState>>(emptyMap())
        private set
    var selectedPackIds by mutableStateOf<Set<String>>(setOf("core"))
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadPacks()
        loadSelectedPacks()
        loadCoreItemCount()
    }

    private fun loadCoreItemCount() {
        viewModelScope.launch {
            try {
                val coreItems = contentRepository.getContent()
                coreItemCount = coreItems.size
            } catch (_: Exception) {
                // Leave at 0 if loading fails
            }
        }
    }

    fun loadPacks() {
        updateStatesFromDisk()

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val packs = packRepository.getAvailablePacks()
                availablePacks = packs
                updateStatesFromDisk()
                loadSelectedPacks()
                cleanOrphanedSelections()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load packs"
            } finally {
                isLoading = false
            }
        }
    }

    private fun updateStatesFromDisk() {
        val updated = mutableMapOf<String, DownloadState>()
        for (pack in availablePacks) {
            updated[pack.id] = if (packRepository.isInstalled(pack.id)) {
                DownloadState.Installed
            } else {
                DownloadState.NotDownloaded
            }
        }
        downloadStates = updated
    }

    fun downloadPack(packId: String) {
        viewModelScope.launch {
            packRepository.downloadPack(packId).collect { state ->
                downloadStates = downloadStates + (packId to state)
                if (state is DownloadState.Installed) {
                    togglePackSelection(packId, selected = true)
                }
            }
        }
    }

    fun deletePack(packId: String) {
        viewModelScope.launch {
            packRepository.deletePack(packId)
            downloadStates = downloadStates + (packId to DownloadState.NotDownloaded)
            togglePackSelection(packId, selected = false)
        }
    }

    fun togglePackSelection(packId: String) {
        val newSelection = if (packId in selectedPackIds) {
            selectedPackIds - packId
        } else {
            selectedPackIds + packId
        }
        selectedPackIds = newSelection
        persistSelectedPacks(newSelection)
    }

    private fun togglePackSelection(packId: String, selected: Boolean) {
        val newSelection = if (selected) {
            selectedPackIds + packId
        } else {
            selectedPackIds - packId
        }
        selectedPackIds = newSelection
        persistSelectedPacks(newSelection)
    }

    private fun loadSelectedPacks() {
        viewModelScope.launch {
            val saved = userPreferencesRepository.selectedPacksFlow.first()
            selectedPackIds = saved + "core"
        }
    }

    private fun persistSelectedPacks(packIds: Set<String>) {
        viewModelScope.launch {
            userPreferencesRepository.saveSelectedPacks(packIds)
        }
    }

    private fun cleanOrphanedSelections() {
        val installedIds = packRepository.getInstalledPackIds().toSet()
        val cleaned = selectedPackIds.filter { it == "core" || it in installedIds }.toSet()
        if (cleaned != selectedPackIds) {
            selectedPackIds = cleaned
            persistSelectedPacks(cleaned)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as IsItAIApplication
                PackViewModel(app.contentRepository, app.packRepository, app.userPreferencesRepository)
            }
        }
    }
}
