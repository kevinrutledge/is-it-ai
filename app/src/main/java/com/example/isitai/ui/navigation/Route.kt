package com.example.isitai.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    @Serializable
    data object Home : Route()

    @Serializable
    data object Game : Route()

    @Serializable
    data class GameOver(
        val streak: Int,
        val isNewRecord: Boolean
    ) : Route()
}
