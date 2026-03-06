package com.example.isitai.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "game_prefs")
val HIGH_SCORE_KEY = intPreferencesKey("high_score")
val SELECTED_PACKS_KEY = stringSetPreferencesKey("selected_packs")
