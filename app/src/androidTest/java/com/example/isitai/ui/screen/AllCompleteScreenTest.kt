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

class AllCompleteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysAllClearTitle() {
        composeTestRule.setContent {
            IsItAITheme {
                AllCompleteScreen(streak = 12, isNewRecord = true, previousBest = 0, onPlayAgain = {}, onHome = {})
            }
        }
        composeTestRule.onNodeWithText("All Clear!").assertIsDisplayed()
    }

    @Test
    fun displaysSubtitle() {
        composeTestRule.setContent {
            IsItAITheme {
                AllCompleteScreen(streak = 12, isNewRecord = true, previousBest = 0, onPlayAgain = {}, onHome = {})
            }
        }
        composeTestRule.onNodeWithText("You identified every image").assertIsDisplayed()
    }

    @Test
    fun showsNewRecordBadge_whenNewRecord() {
        composeTestRule.setContent {
            IsItAITheme {
                AllCompleteScreen(streak = 12, isNewRecord = true, previousBest = 0, onPlayAgain = {}, onHome = {})
            }
        }
        composeTestRule.onNodeWithText("New Record!").assertIsDisplayed()
    }

    @Test
    fun displaysPreviousBest() {
        composeTestRule.setContent {
            IsItAITheme {
                AllCompleteScreen(streak = 12, isNewRecord = true, previousBest = 8, onPlayAgain = {}, onHome = {})
            }
        }
        composeTestRule.onNodeWithText("Previous best: 8").assertIsDisplayed()
    }

    @Test
    fun homeCallsCallback() {
        var clicked = false
        composeTestRule.setContent {
            IsItAITheme {
                AllCompleteScreen(streak = 12, isNewRecord = true, previousBest = 0, onPlayAgain = {}, onHome = { clicked = true })
            }
        }
        composeTestRule.onNodeWithText("Home").performClick()
        assertTrue(clicked)
    }
}
