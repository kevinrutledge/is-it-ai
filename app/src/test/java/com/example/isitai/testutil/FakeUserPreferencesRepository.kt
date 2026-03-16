package com.example.isitai.testutil

import com.example.isitai.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserPreferencesRepository : UserPreferencesRepository {
    private val _highScore = MutableStateFlow(0)
    private val _darkMode = MutableStateFlow(false)
    private val _selectedPacks = MutableStateFlow(setOf("core"))

    override val highScoreFlow: Flow<Int> = _highScore
    override val darkModeFlow: Flow<Boolean> = _darkMode
    override val selectedPacksFlow: Flow<Set<String>> = _selectedPacks

    var savedHighScore: Int? = null
        private set
    var savedDarkMode: Boolean? = null
        private set

    override suspend fun saveHighScore(score: Int) {
        savedHighScore = score
        _highScore.value = score
    }

    override suspend fun saveDarkMode(isDark: Boolean) {
        savedDarkMode = isDark
        _darkMode.value = isDark
    }

    override suspend fun saveSelectedPacks(packIds: Set<String>) {
        _selectedPacks.value = packIds
    }

    override suspend fun resetHighScore() {
        _highScore.value = 0
        savedHighScore = 0
    }
}
