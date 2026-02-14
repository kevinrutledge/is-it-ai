package com.example.isitai.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.isitai.data.model.GameState
import com.example.isitai.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onContinueToGameOver: (streak: Int, isNewRecord: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val gameState = viewModel.gameState

    LaunchedEffect(gameState) {
        if (gameState is GameState.GameOver) {
            onContinueToGameOver(gameState.streak, gameState.isNewRecord)
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Streak: ${viewModel.streak}")

        when (gameState) {
            is GameState.Playing -> {
                Text("Item: ${gameState.item.id} (${gameState.item.type})")
                Text("Difficulty: ${gameState.item.difficulty}")
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    Button(onClick = { viewModel.submitAnswer(isAI = false) }) {
                        Text("Real")
                    }
                    Button(onClick = { viewModel.submitAnswer(isAI = true) }) {
                        Text("AI")
                    }
                }
            }
            is GameState.IncorrectFeedback -> {
                Text("Wrong! It was ${gameState.item.type}")
                Button(onClick = { viewModel.continueToGameOver() }) {
                    Text("Continue")
                }
            }
            else -> {
                Text("Loading...")
            }
        }
    }
}
