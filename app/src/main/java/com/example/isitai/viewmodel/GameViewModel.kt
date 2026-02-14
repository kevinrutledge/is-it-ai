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
import com.example.isitai.data.HIGH_SCORE_KEY
import com.example.isitai.data.dataStore
import com.example.isitai.data.model.ContentItem
import com.example.isitai.data.model.ContentUiState
import com.example.isitai.data.model.GameState
import com.example.isitai.data.repository.ContentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class GameViewModel(
    application: Application,
    private val contentRepository: ContentRepository
) : AndroidViewModel(application) {

    var gameState by mutableStateOf<GameState>(GameState.Idle)
        private set
    var contentState by mutableStateOf<ContentUiState>(ContentUiState.Loading)
        private set
    var streak by mutableStateOf(0)
        private set
    var highScore by mutableStateOf(0)
        private set
    val contentSource: ContentRepository.ContentSource
        get() = contentRepository.contentSource

    private var _contentItems: List<ContentItem> = emptyList()
    private val _usedIds: MutableSet<String> = mutableSetOf()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            contentState = ContentUiState.Loading
            try {
                val items = contentRepository.getContent()
                _contentItems = items
                contentState = ContentUiState.Ready(items)
            } catch (e: Exception) {
                contentState = ContentUiState.Error(e.message ?: "Failed to load content")
            }
        }
        viewModelScope.launch {
            getApplication<Application>().dataStore.data
                .map { prefs -> prefs[HIGH_SCORE_KEY] ?: 0 }
                .collect { highScore = it }
        }
    }

    fun startGame() {
        if (contentState !is ContentUiState.Ready) return
        streak = 0
        _usedIds.clear()
        val item = selectNextItem() ?: return
        gameState = GameState.Playing(item)
    }

    fun submitAnswer(isAI: Boolean) {
        val current = gameState as? GameState.Playing ?: return
        val correct = current.item.isAI == isAI
        if (correct) {
            streak++
            val next = selectNextItem()
            if (next != null) {
                gameState = GameState.Playing(next)
            } else {
                gameState = GameState.GameOver(streak = streak, isNewRecord = streak > highScore)
            }
        } else {
            if (streak > highScore) {
                highScore = streak
                viewModelScope.launch(Dispatchers.IO) {
                    getApplication<Application>().dataStore.edit { prefs ->
                        prefs[HIGH_SCORE_KEY] = streak
                    }
                }
            }
            gameState = GameState.IncorrectFeedback(current.item)
        }
    }

    fun continueToGameOver() {
        gameState = GameState.GameOver(streak = streak, isNewRecord = false)
    }

    private fun selectNextItem(): ContentItem? {
        val difficulty = when {
            streak <= 2 -> "easy"
            streak <= 6 -> "medium"
            else -> "hard"
        }
        var candidates = _contentItems.filter {
            it.difficulty == difficulty && it.id !in _usedIds
        }
        if (candidates.isEmpty()) {
            _usedIds.removeAll { id -> _contentItems.any { it.id == id && it.difficulty == difficulty } }
            candidates = _contentItems.filter { it.difficulty == difficulty }
        }
        if (candidates.isEmpty()) {
            candidates = _contentItems.filter { it.id !in _usedIds }
        }
        val item = candidates.randomOrNull() ?: return null
        _usedIds.add(item.id)
        return item
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as IsItAIApplication
                GameViewModel(app, app.contentRepository)
            }
        }
    }
}
