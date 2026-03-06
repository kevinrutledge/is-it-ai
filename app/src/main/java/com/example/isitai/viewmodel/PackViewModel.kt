package com.example.isitai.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.isitai.IsItAIApplication
import com.example.isitai.data.SELECTED_PACKS_KEY
import com.example.isitai.data.dataStore
import com.example.isitai.data.model.DownloadState
import com.example.isitai.data.model.PackMetadata
import com.example.isitai.data.repository.PackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PackViewModel(
    application: Application,
    private val packRepository: PackRepository
) : AndroidViewModel(application) {

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
    }

    fun loadPacks() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            errorMessage = null
            try {
                val packs = packRepository.getAvailablePacks()
                availablePacks = packs
                val states = mutableMapOf<String, DownloadState>()
                for (pack in packs) {
                    states[pack.id] = if (packRepository.isInstalled(pack.id)) {
                        DownloadState.Installed
                    } else {
                        DownloadState.NotDownloaded
                    }
                }
                downloadStates = states
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load packs"
                loadInstalledPacksFromDisk()
            } finally {
                isLoading = false
            }
        }
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
        viewModelScope.launch(Dispatchers.IO) {
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
            val saved = getApplication<Application>().dataStore.data
                .map { prefs -> prefs[SELECTED_PACKS_KEY] ?: emptySet() }
                .first()
            selectedPackIds = saved + "core"
        }
    }

    private fun persistSelectedPacks(packIds: Set<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[SELECTED_PACKS_KEY] = packIds
            }
        }
    }

    private fun loadInstalledPacksFromDisk() {
        val installedIds = packRepository.getInstalledPackIds()
        val states = mutableMapOf<String, DownloadState>()
        for (packId in installedIds) {
            states[packId] = DownloadState.Installed
        }
        downloadStates = states
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as IsItAIApplication
                PackViewModel(app, app.packRepository)
            }
        }
    }
}
