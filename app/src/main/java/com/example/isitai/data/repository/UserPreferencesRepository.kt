package com.example.isitai.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.isitai.data.DARK_MODE_KEY
import com.example.isitai.data.HIGH_SCORE_KEY
import com.example.isitai.data.SELECTED_PACKS_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val highScoreFlow: Flow<Int> = dataStore.data
        .map { prefs -> prefs[HIGH_SCORE_KEY] ?: 0 }

    val darkModeFlow: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[DARK_MODE_KEY] ?: false }

    val selectedPacksFlow: Flow<Set<String>> = dataStore.data
        .map { prefs -> prefs[SELECTED_PACKS_KEY] ?: emptySet() }

    suspend fun saveHighScore(score: Int) {
        dataStore.edit { prefs ->
            prefs[HIGH_SCORE_KEY] = score
        }
    }

    suspend fun saveDarkMode(isDark: Boolean) {
        dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = isDark
        }
    }

    suspend fun saveSelectedPacks(packIds: Set<String>) {
        dataStore.edit { prefs ->
            prefs[SELECTED_PACKS_KEY] = packIds
        }
    }

    suspend fun resetHighScore() {
        dataStore.edit { prefs ->
            prefs[HIGH_SCORE_KEY] = 0
        }
    }
}
