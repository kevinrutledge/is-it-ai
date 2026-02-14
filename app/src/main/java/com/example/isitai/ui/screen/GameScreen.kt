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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.isitai.data.model.ContentItem
import com.example.isitai.data.model.GameState
import com.example.isitai.ui.components.AIFeedbackContent
import com.example.isitai.ui.components.AnnotationOverlay
import com.example.isitai.ui.components.PillButton
import com.example.isitai.ui.components.RealFeedbackContent
import com.example.isitai.ui.components.StreakCounter
import com.example.isitai.ui.components.StreakSize
import com.example.isitai.ui.theme.PlaceholderDark
import com.example.isitai.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    val currentItem: ContentItem? = when (gameState) {
        is GameState.Playing -> gameState.item
        is GameState.IncorrectFeedback -> gameState.item
        else -> null
    }
    val isIncorrect = gameState is GameState.IncorrectFeedback

    Box(modifier = modifier.fillMaxSize()) {
        // Game content layer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            StreakCounter(streak = viewModel.streak, size = StreakSize.Small)
            Spacer(modifier = Modifier.height(8.dp))

            if (currentItem != null) {
                val imageUrl = currentItem.resolveImageUrl(viewModel.contentSource)
                var imageSize by remember { mutableStateOf(IntSize.Zero) }

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
                        modifier = Modifier.fillMaxSize(),
                        onSuccess = { result ->
                            imageSize = IntSize(
                                result.painter.intrinsicSize.width.toInt(),
                                result.painter.intrinsicSize.height.toInt()
                            )
                        }
                    )

                    // Annotation overlay for AI images during feedback
                    if (isIncorrect && currentItem.isAI && currentItem.annotations.isNotEmpty()) {
                        AnnotationOverlay(
                            annotations = currentItem.annotations,
                            imageSize = imageSize,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Show buttons only during Playing state
                if (!isIncorrect) {
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
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Scrim overlay for Real images only (AI scrim is built into AnnotationOverlay)
        if (isIncorrect && currentItem != null && currentItem.isReal) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.32f))
            )
        }

        // Bottom sheet during feedback
        if (isIncorrect && currentItem != null) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            ModalBottomSheet(
                onDismissRequest = { viewModel.continueToGameOver() },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                if (currentItem.isAI) {
                    AIFeedbackContent(
                        item = currentItem,
                        onContinue = { viewModel.continueToGameOver() },
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                } else {
                    RealFeedbackContent(
                        item = currentItem,
                        onContinue = { viewModel.continueToGameOver() },
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }
    }
}
