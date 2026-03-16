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
import com.example.isitai.data.model.ContentItem
import com.example.isitai.data.model.GameState
import com.example.isitai.data.repository.ContentRepository
import com.example.isitai.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GameViewModel(
    private val contentRepository: ContentRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    var gameState by mutableStateOf<GameState>(GameState.Idle)
        private set
    var streak by mutableStateOf(0)
        private set
    var highScore by mutableStateOf(0)
        private set
    var previousCorrectItem by mutableStateOf<ContentItem?>(null)
        private set

    private var _contentItems: List<ContentItem> = emptyList()
    private val _usedIds: MutableSet<String> = mutableSetOf()
    private var _savedPlayingItem: ContentItem? = null

    init {
        viewModelScope.launch {
            try {
                val selectedPacks = userPreferencesRepository.selectedPacksFlow.first()
                _contentItems = contentRepository.getContent(selectedPacks)
            } catch (_: Exception) {
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.highScoreFlow.collect { highScore = it }
        }
    }

    fun resolveImageUrl(item: ContentItem): String {
        return contentRepository.resolveImageUrl(item)
    }

    fun startGame() {
        gameState = GameState.Idle
        streak = 0
        _usedIds.clear()
        previousCorrectItem = null
        _savedPlayingItem = null

        viewModelScope.launch {
            try {
                val selectedPacks = userPreferencesRepository.selectedPacksFlow.first()
                _contentItems = contentRepository.getContent(selectedPacks)
            } catch (_: Exception) {
                return@launch
            }
            val item = selectNextItem() ?: return@launch
            gameState = GameState.Playing(item)
        }
    }

    fun submitAnswer(isAI: Boolean) {
        val current = gameState as? GameState.Playing ?: return
        val correct = current.item.isAI == isAI
        if (correct) {
            previousCorrectItem = current.item
            streak++
            val next = selectNextItem()
            if (next != null) {
                gameState = GameState.Playing(next)
            } else {
                val isNewRecord = streak > highScore
                if (isNewRecord) {
                    highScore = streak
                    viewModelScope.launch {
                        userPreferencesRepository.saveHighScore(streak)
                    }
                }
                gameState = GameState.AllComplete(streak = streak, isNewRecord = isNewRecord)
            }
        } else {
            if (streak > highScore) {
                highScore = streak
                viewModelScope.launch {
                    userPreferencesRepository.saveHighScore(streak)
                }
            }
            gameState = GameState.IncorrectFeedback(current.item)
        }
    }

    fun reviewPreviousCorrect() {
        val prev = previousCorrectItem ?: return
        val current = gameState as? GameState.Playing ?: return
        _savedPlayingItem = current.item
        gameState = GameState.CorrectFeedback(prev)
    }

    fun returnToPlaying() {
        val saved = _savedPlayingItem ?: return
        gameState = GameState.Playing(saved)
        _savedPlayingItem = null
    }

    fun continueToGameOver() {
        gameState = GameState.GameOver(streak = streak, isNewRecord = streak >= highScore && streak > 0)
    }

    private fun selectNextItem(): ContentItem? {
        val candidates = _contentItems.filter { it.id !in _usedIds }
        val item = candidates.randomOrNull() ?: return null
        _usedIds.add(item.id)
        return item
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as IsItAIApplication
                GameViewModel(app.contentRepository, app.userPreferencesRepository)
            }
        }
    }
}
