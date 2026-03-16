package com.example.isitai.ui.screen

import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.isitai.ui.theme.IsItAITheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GameOverScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysGameOverTitle() {
        composeTestRule.setContent {
            IsItAITheme {
                GameOverScreen(streak = 5, isNewRecord = false, previousBest = 3, onPlayAgain = {}, onHome = {})
            }
        }
        composeTestRule.onNodeWithText("Game Over").assertIsDisplayed()
    }

    @Test
    fun displaysStreak() {
        composeTestRule.setContent {
            IsItAITheme {
                GameOverScreen(streak = 5, isNewRecord = false, previousBest = 3, onPlayAgain = {}, onHome = {})
            }
        }
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
    }

    @Test
    fun showsNewRecordBadge_whenNewRecord() {
        composeTestRule.setContent {
            IsItAITheme {
                GameOverScreen(streak = 5, isNewRecord = true, previousBest = 3, onPlayAgain = {}, onHome = {})
            }
        }
        composeTestRule.onNodeWithText("New Record!").assertIsDisplayed()
    }

    @Test
    fun hidesNewRecordBadge_whenNotNewRecord() {
        composeTestRule.setContent {
            IsItAITheme {
                GameOverScreen(streak = 5, isNewRecord = false, previousBest = 3, onPlayAgain = {}, onHome = {})
            }
        }
        composeTestRule.onNodeWithText("New Record!").assertDoesNotExist()
    }

    @Test
    fun playAgainCallsCallback() {
        var clicked = false
        composeTestRule.setContent {
            IsItAITheme {
                GameOverScreen(streak = 5, isNewRecord = false, previousBest = 3, onPlayAgain = { clicked = true }, onHome = {})
            }
        }
        composeTestRule.onNodeWithText("Play Again").performClick()
        assertTrue(clicked)
    }
}
