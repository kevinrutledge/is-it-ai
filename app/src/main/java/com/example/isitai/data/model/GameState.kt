package com.example.isitai.data.model

sealed interface GameState {
    data object Idle : GameState
    data class Playing(val item: ContentItem) : GameState
    data class IncorrectFeedback(val item: ContentItem) : GameState
    data class GameOver(val streak: Int, val isNewRecord: Boolean) : GameState
}
