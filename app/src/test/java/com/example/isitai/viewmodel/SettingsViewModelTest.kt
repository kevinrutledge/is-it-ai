package com.example.isitai.viewmodel

import com.example.isitai.testutil.FakeUserPreferencesRepository
import com.example.isitai.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var prefsRepository: FakeUserPreferencesRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        prefsRepository = FakeUserPreferencesRepository()
    }

    @Test
    fun toggleDarkMode_updatesState() = runTest {
        viewModel = SettingsViewModel(
            packRepository = FakePackRepository(),
            userPreferencesRepository = prefsRepository
        )
        advanceUntilIdle()

        viewModel.toggleDarkMode()
        advanceUntilIdle()

        assertTrue(viewModel.isDarkMode)
    }

    @Test
    fun toggleDarkMode_persistsToRepository() = runTest {
        viewModel = SettingsViewModel(
            packRepository = FakePackRepository(),
            userPreferencesRepository = prefsRepository
        )
        advanceUntilIdle()

        viewModel.toggleDarkMode()
        advanceUntilIdle()

        assertEquals(true, prefsRepository.savedDarkMode)
    }

    @Test
    fun resetHighScore_callsRepository() = runTest {
        viewModel = SettingsViewModel(
            packRepository = FakePackRepository(),
            userPreferencesRepository = prefsRepository
        )
        advanceUntilIdle()

        viewModel.resetHighScore()
        advanceUntilIdle()

        assertEquals(0, prefsRepository.savedHighScore)
    }
}
