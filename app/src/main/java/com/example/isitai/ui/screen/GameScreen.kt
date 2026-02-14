package com.example.isitai.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.isitai.data.model.GameState
import com.example.isitai.ui.components.PillButton
import com.example.isitai.ui.components.StreakCounter
import com.example.isitai.ui.components.StreakSize
import com.example.isitai.ui.theme.PlaceholderDark
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
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Streak counter top-left
        StreakCounter(streak = viewModel.streak, size = StreakSize.Small)

        Spacer(modifier = Modifier.height(8.dp))

        when (gameState) {
            is GameState.Playing -> {
                // Image
                val imageUrl = gameState.item.resolveImageUrl(viewModel.contentSource)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PlaceholderDark)
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Image to evaluate",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Real / AI buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PillButton(
                        text = "Real",
                        onClick = { viewModel.submitAnswer(isAI = false) },
                        modifier = Modifier.weight(1f)
                    )
                    PillButton(
                        text = "AI",
                        onClick = { viewModel.submitAnswer(isAI = true) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
            is GameState.IncorrectFeedback -> {
                // Image stays visible
                val imageUrl = gameState.item.resolveImageUrl(viewModel.contentSource)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PlaceholderDark)
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Image to evaluate",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Wrong! It was ${gameState.item.type}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                PillButton(
                    text = "Continue",
                    onClick = { viewModel.continueToGameOver() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading...")
                }
            }
        }
    }
}
