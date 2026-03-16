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
import com.example.isitai.data.repository.PackRepository
import com.example.isitai.data.repository.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val packRepository: PackRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    var isDarkMode by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            userPreferencesRepository.darkModeFlow.collect { isDarkMode = it }
        }
    }

    fun toggleDarkMode() {
        isDarkMode = !isDarkMode
        viewModelScope.launch(Dispatchers.IO) {
            userPreferencesRepository.saveDarkMode(isDarkMode)
        }
    }

    fun resetHighScore() {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferencesRepository.resetHighScore()
        }
    }

    fun clearDownloadedPacks() {
        viewModelScope.launch(Dispatchers.IO) {
            val installedIds = packRepository.getInstalledPackIds()
            for (packId in installedIds) {
                packRepository.deletePack(packId)
            }
            userPreferencesRepository.saveSelectedPacks(setOf("core"))
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as IsItAIApplication
                SettingsViewModel(app.packRepository, app.userPreferencesRepository)
            }
        }
    }
}
