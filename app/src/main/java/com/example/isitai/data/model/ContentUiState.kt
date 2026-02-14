package com.example.isitai.data.model

sealed class ContentUiState {
    data object Loading : ContentUiState()
    data class Ready(val items: List<ContentItem>) : ContentUiState()
    data class Error(val message: String) : ContentUiState()
}
