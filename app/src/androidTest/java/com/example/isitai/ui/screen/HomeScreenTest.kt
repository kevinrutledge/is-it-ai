package com.example.isitai.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.isitai.ui.theme.IsItAITheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysAppTitle() {
        composeTestRule.setContent {
            IsItAITheme { HomeScreen(highScore = 0, onPlayClick = {}) }
        }
        composeTestRule.onNodeWithText("Is It AI?").assertIsDisplayed()
    }

    @Test
    fun displaysHighScore() {
        composeTestRule.setContent {
            IsItAITheme { HomeScreen(highScore = 7, onPlayClick = {}) }
        }
        composeTestRule.onNodeWithText("7").assertIsDisplayed()
    }

    @Test
    fun displaysPlayButton() {
        composeTestRule.setContent {
            IsItAITheme { HomeScreen(highScore = 0, onPlayClick = {}) }
        }
        composeTestRule.onNodeWithText("Play").assertIsDisplayed()
    }

    @Test
    fun playButtonCallsCallback() {
        var clicked = false
        composeTestRule.setContent {
            IsItAITheme { HomeScreen(highScore = 0, onPlayClick = { clicked = true }) }
        }
        composeTestRule.onNodeWithText("Play").performClick()
        assertTrue(clicked)
    }
}
