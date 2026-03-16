package com.example.isitai.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.isitai.data.model.ContentItem
import com.example.isitai.data.model.GameState
import com.example.isitai.ui.components.AIFeedbackContent
import com.example.isitai.ui.components.AnnotationOverlay
import com.example.isitai.ui.components.PillButton
import com.example.isitai.ui.components.RealFeedbackContent
import com.example.isitai.ui.components.StreakCounter
import com.example.isitai.ui.components.StreakSize
import com.example.isitai.viewmodel.GameViewModel

private val SHEET_PEEK_HEIGHT = 56.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onContinueToGameOver: (streak: Int, isNewRecord: Boolean) -> Unit,
    onAllComplete: (streak: Int, isNewRecord: Boolean) -> Unit,
    onNavigateHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameState = viewModel.gameState

    LaunchedEffect(gameState) {
        if (gameState is GameState.GameOver) {
            onContinueToGameOver(gameState.streak, gameState.isNewRecord)
        }
        if (gameState is GameState.AllComplete) {
            onAllComplete(gameState.streak, gameState.isNewRecord)
        }
    }

    val currentItem: ContentItem? = when (gameState) {
        is GameState.Playing -> gameState.item
        is GameState.IncorrectFeedback -> gameState.item
        is GameState.CorrectFeedback -> gameState.item
        else -> null
    }
    val isIncorrect = gameState is GameState.IncorrectFeedback
    val isReviewingCorrect = gameState is GameState.CorrectFeedback
    var showZoomOverlay by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentItem?.id) {
        showZoomOverlay = false
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            isIncorrect && currentItem != null -> {
                FeedbackMode(
                    item = currentItem,
                    imageUrl = viewModel.resolveImageUrl(currentItem),
                    streak = viewModel.streak,
                    onContinue = { viewModel.continueToGameOver() },
                    onZoomImage = { showZoomOverlay = true }
                )
            }
            isReviewingCorrect && currentItem != null -> {
                FeedbackMode(
                    item = currentItem,
                    imageUrl = viewModel.resolveImageUrl(currentItem),
                    streak = viewModel.streak,
                    onContinue = { viewModel.returnToPlaying() },
                    onZoomImage = { showZoomOverlay = true },
                    continueLabel = "Back to Game"
                )
            }
            currentItem != null -> {
                PlayingMode(
                    imageUrl = viewModel.resolveImageUrl(currentItem),
                    streak = viewModel.streak,
                    hasPreviousCorrect = viewModel.previousCorrectItem != null,
                    onAnswerReal = { viewModel.submitAnswer(isAI = false) },
                    onAnswerAI = { viewModel.submitAnswer(isAI = true) },
                    onZoomImage = { showZoomOverlay = true },
                    onNavigateHome = onNavigateHome,
                    onReviewPrevious = { viewModel.reviewPreviousCorrect() },
                    onOpenSettings = { showSettingsDialog = true }
                )
            }
        }

        if (currentItem != null && showZoomOverlay) {
            ZoomOverlay(
                imageUrl = viewModel.resolveImageUrl(currentItem),
                onClose = { showZoomOverlay = false }
            )
        }
    }

    if (showSettingsDialog) {
        Dialog(onDismissRequest = { showSettingsDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .toggleable(
                                value = isDarkMode,
                                onValueChange = { onToggleDarkMode() },
                                role = Role.Switch
                            )
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dark Mode",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayingMode(
    imageUrl: String,
    streak: Int,
    hasPreviousCorrect: Boolean,
    onAnswerReal: () -> Unit,
    onAnswerAI: () -> Unit,
    onZoomImage: () -> Unit,
    onNavigateHome: () -> Unit,
    onReviewPrevious: () -> Unit,
    onOpenSettings: () -> Unit
) {
    BackHandler { onNavigateHome() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StreakCounter(
                streak = streak,
                size = StreakSize.Small,
                modifier = Modifier.padding(start = 12.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (hasPreviousCorrect) {
                    IconButton(onClick = onReviewPrevious) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Review previous",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        GameImage(
            imageUrl = imageUrl,
            onTap = onZoomImage,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PillButton(
                text = "Real",
                onClick = onAnswerReal,
                modifier = Modifier.weight(1f)
            )
            PillButton(
                text = "AI",
                onClick = onAnswerAI,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedbackMode(
    item: ContentItem,
    imageUrl: String,
    streak: Int,
    onContinue: () -> Unit,
    onZoomImage: () -> Unit,
    continueLabel: String = "Continue"
) {
    BackHandler { }

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    val density = LocalDensity.current
    val peekHeightPx = with(density) { SHEET_PEEK_HEIGHT.toPx() }
    var expandedOffsetPx by remember { mutableFloatStateOf(Float.NaN) }

    LaunchedEffect(Unit) {
        sheetState.expand()
        expandedOffsetPx = sheetState.requireOffset()
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val layoutHeightPx = with(density) { maxHeight.toPx() }
        val peekOffsetPx = layoutHeightPx - peekHeightPx

        // 0 = sheet expanded, 1 = sheet at peek
        val sheetProgress by remember(peekOffsetPx) {
            derivedStateOf {
                val expanded = expandedOffsetPx
                if (expanded.isNaN()) return@derivedStateOf 0f
                val offset = try {
                    sheetState.requireOffset()
                } catch (_: IllegalStateException) {
                    expanded
                }
                if (peekOffsetPx <= expanded) 0f
                else ((offset - expanded) / (peekOffsetPx - expanded))
                    .coerceIn(0f, 1f)
            }
        }

        val easedProgress by remember {
            derivedStateOf {
                FastOutSlowInEasing.transform(sheetProgress)
            }
        }

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = SHEET_PEEK_HEIGHT,
            sheetDragHandle = { BottomSheetDefaults.DragHandle() },
            sheetContainerColor = MaterialTheme.colorScheme.surface,
            sheetContent = {
                if (item.isAI) {
                    AIFeedbackContent(
                        item = item,
                        onContinue = onContinue,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        continueLabel = continueLabel
                    )
                } else {
                    RealFeedbackContent(
                        item = item,
                        onContinue = onContinue,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        continueLabel = continueLabel
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                StreakCounter(streak = streak, size = StreakSize.Small)
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    var imageSize by remember { mutableStateOf(IntSize.Zero) }

                    GameImage(
                        imageUrl = imageUrl,
                        onTap = onZoomImage,
                        onImageLoaded = { imageSize = it },
                        modifier = Modifier.fillMaxSize()
                    )

                    if (item.isAI && item.annotations.isNotEmpty()) {
                        AnnotationOverlay(
                            annotations = item.annotations,
                            imageSize = imageSize,
                            scrimAlpha = 0.32f,
                            annotationAlpha = 1f,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AnnotationOverlay(
                            annotations = emptyList(),
                            imageSize = imageSize,
                            scrimAlpha = (1f - easedProgress) * 0.32f,
                            annotationAlpha = 0f,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameImage(
    imageUrl: String,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    onImageLoaded: ((IntSize) -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .pointerInput(Unit) {
                detectTapGestures { onTap() }
            }
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Image to evaluate",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
            onSuccess = { result ->
                onImageLoaded?.invoke(
                    IntSize(
                        result.painter.intrinsicSize.width.toInt(),
                        result.painter.intrinsicSize.height.toInt()
                    )
                )
            }
        )
    }
}

@Composable
private fun ZoomOverlay(
    imageUrl: String,
    onClose: () -> Unit
) {
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var zoomOffsetX by remember { mutableFloatStateOf(0f) }
    var zoomOffsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    zoomScale = (zoomScale * zoom).coerceIn(1f, 5f)
                    if (zoomScale > 1f) {
                        zoomOffsetX += pan.x
                        zoomOffsetY += pan.y
                    } else {
                        zoomOffsetX = 0f
                        zoomOffsetY = 0f
                    }
                }
            }
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Zoomed image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = zoomScale,
                    scaleY = zoomScale,
                    translationX = zoomOffsetX,
                    translationY = zoomOffsetY
                )
        )

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close zoom",
                tint = Color.White
            )
        }
    }
}
