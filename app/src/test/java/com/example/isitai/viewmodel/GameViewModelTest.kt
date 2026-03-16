package com.example.isitai.viewmodel

import com.example.isitai.data.model.ContentItem
import com.example.isitai.data.model.GameState
import com.example.isitai.testutil.FakeContentRepository
import com.example.isitai.testutil.FakeUserPreferencesRepository
import com.example.isitai.testutil.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var contentRepository: FakeContentRepository
    private lateinit var prefsRepository: FakeUserPreferencesRepository
    private lateinit var viewModel: GameViewModel

    private val testItems = listOf(
        ContentItem(id = "1", filename = "img1.webp", type = "ai"),
        ContentItem(id = "2", filename = "img2.webp", type = "real"),
        ContentItem(id = "3", filename = "img3.webp", type = "ai")
    )

    @Before
    fun setup() {
        contentRepository = FakeContentRepository()
        contentRepository.items = testItems
        prefsRepository = FakeUserPreferencesRepository()
        viewModel = GameViewModel(contentRepository, prefsRepository)
    }

    @Test
    fun startGame_setsIdleSynchronously() {
        val standardDispatcher = StandardTestDispatcher()
        Dispatchers.resetMain()
        Dispatchers.setMain(standardDispatcher)
        val vm = GameViewModel(contentRepository, prefsRepository)
        vm.startGame()
        assertEquals(GameState.Idle, vm.gameState)
    }

    @Test
    fun startGame_resetsStreakToZero() {
        viewModel.startGame()
        assertEquals(0, viewModel.streak)
    }

    @Test
    fun startGame_transitionsToPlaying() = runTest {
        viewModel.startGame()
        advanceUntilIdle()
        assertTrue(viewModel.gameState is GameState.Playing)
    }

    @Test
    fun submitAnswer_correctAnswer_incrementsStreak() = runTest {
        viewModel.startGame()
        advanceUntilIdle()
        val playing = viewModel.gameState as GameState.Playing
        val correctAnswer = playing.item.isAI

        viewModel.submitAnswer(correctAnswer)

        assertEquals(1, viewModel.streak)
    }

    @Test
    fun submitAnswer_correctAnswer_advancesToNextItem() = runTest {
        viewModel.startGame()
        advanceUntilIdle()
        val firstItem = (viewModel.gameState as GameState.Playing).item

        viewModel.submitAnswer(firstItem.isAI)

        val secondItem = (viewModel.gameState as GameState.Playing).item
        assertNotEquals(firstItem.id, secondItem.id)
    }

    @Test
    fun submitAnswer_wrongAnswer_setsIncorrectFeedbackImmediately() = runTest {
        viewModel.startGame()
        advanceUntilIdle()
        val playing = viewModel.gameState as GameState.Playing

        viewModel.submitAnswer(!playing.item.isAI)

        assertTrue(viewModel.gameState is GameState.IncorrectFeedback)
    }

    @Test
    fun submitAnswer_wrongAnswer_savesHighScore() = runTest {
        viewModel.startGame()
        advanceUntilIdle()

        val firstItem = (viewModel.gameState as GameState.Playing).item
        viewModel.submitAnswer(firstItem.isAI)

        val secondItem = (viewModel.gameState as GameState.Playing).item
        viewModel.submitAnswer(!secondItem.isAI)
        advanceUntilIdle()

        assertEquals(1, prefsRepository.savedHighScore)
    }

    @Test
    fun continueToGameOver_setsGameOverImmediately() = runTest {
        viewModel.startGame()
        advanceUntilIdle()
        val playing = viewModel.gameState as GameState.Playing
        viewModel.submitAnswer(!playing.item.isAI)

        viewModel.continueToGameOver()

        assertTrue(viewModel.gameState is GameState.GameOver)
    }

    @Test
    fun continueToGameOver_showsNewRecord() = runTest {
        viewModel.startGame()
        advanceUntilIdle()
        val playing = viewModel.gameState as GameState.Playing
        viewModel.submitAnswer(playing.item.isAI)

        val next = viewModel.gameState as GameState.Playing
        viewModel.submitAnswer(!next.item.isAI)

        viewModel.continueToGameOver()

        val gameOver = viewModel.gameState as GameState.GameOver
        assertTrue(gameOver.isNewRecord)
    }

    @Test
    fun selectNextItem_neverRepeats() = runTest {
        viewModel.startGame()
        advanceUntilIdle()

        val seenIds = mutableSetOf<String>()
        repeat(testItems.size) {
            val playing = viewModel.gameState as? GameState.Playing ?: return@repeat
            val alreadySeen = !seenIds.add(playing.item.id)
            assertTrue("Item ${playing.item.id} was repeated", !alreadySeen)
            viewModel.submitAnswer(playing.item.isAI)
        }
        assertEquals(testItems.size, seenIds.size)
    }

    @Test
    fun allItemsCorrect_setsAllComplete() = runTest {
        viewModel.startGame()
        advanceUntilIdle()

        repeat(testItems.size) {
            val playing = viewModel.gameState as GameState.Playing
            viewModel.submitAnswer(playing.item.isAI)
        }

        assertTrue(viewModel.gameState is GameState.AllComplete)
    }

    @Test
    fun allItemsCorrect_savesHighScore() = runTest {
        viewModel.startGame()
        advanceUntilIdle()

        repeat(testItems.size) {
            val playing = viewModel.gameState as GameState.Playing
            viewModel.submitAnswer(playing.item.isAI)
        }
        advanceUntilIdle()

        assertEquals(testItems.size, prefsRepository.savedHighScore)
    }

    @Test
    fun reviewPreviousCorrect_switchesToCorrectFeedback() = runTest {
        viewModel.startGame()
        advanceUntilIdle()

        val firstItem = (viewModel.gameState as GameState.Playing).item
        viewModel.submitAnswer(firstItem.isAI)

        viewModel.reviewPreviousCorrect()

        val feedback = viewModel.gameState as GameState.CorrectFeedback
        assertEquals(firstItem.id, feedback.item.id)
    }
}
