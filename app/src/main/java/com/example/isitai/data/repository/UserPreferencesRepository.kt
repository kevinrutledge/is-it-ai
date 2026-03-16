package com.example.isitai.data.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val highScoreFlow: Flow<Int>
    val darkModeFlow: Flow<Boolean>
    val selectedPacksFlow: Flow<Set<String>>
    suspend fun saveHighScore(score: Int)
    suspend fun saveDarkMode(isDark: Boolean)
    suspend fun saveSelectedPacks(packIds: Set<String>)
    suspend fun resetHighScore()
}
