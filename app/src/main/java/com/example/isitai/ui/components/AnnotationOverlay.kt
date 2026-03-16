package com.example.isitai.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.isitai.data.model.Annotation
import com.example.isitai.ui.theme.AnnotationColors

@Composable
fun AnnotationOverlay(
    annotations: List<Annotation>,
    imageSize: IntSize,
    scrimAlpha: Float,
    annotationAlpha: Float,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
        }
    ) {
        if (scrimAlpha <= 0f && annotationAlpha <= 0f) return@Canvas

        drawRect(color = Color.Black.copy(alpha = scrimAlpha))

        if (annotations.isEmpty() || annotationAlpha <= 0f) return@Canvas
        if (imageSize.width == 0 || imageSize.height == 0) return@Canvas

        val scaleFactor = minOf(
            size.width / imageSize.width.toFloat(),
            size.height / imageSize.height.toFloat()
        )
        val offsetX = (size.width - imageSize.width * scaleFactor) / 2f
        val offsetY = (size.height - imageSize.height * scaleFactor) / 2f

        for (annotation in annotations) {
            val cx = offsetX + annotation.x * imageSize.width * scaleFactor
            val cy = offsetY + annotation.y * imageSize.height * scaleFactor
            val r = annotation.radius * imageSize.width * scaleFactor

            drawCircle(
                color = Color.Black.copy(alpha = annotationAlpha),
                radius = r,
                center = Offset(cx, cy),
                style = Fill,
                blendMode = BlendMode.Clear
            )
        }

        for (annotation in annotations) {
            val cx = offsetX + annotation.x * imageSize.width * scaleFactor
            val cy = offsetY + annotation.y * imageSize.height * scaleFactor
            val r = annotation.radius * imageSize.width * scaleFactor
            val color = AnnotationColors.forType(annotation.artifactType)

            drawCircle(
                color = color.copy(alpha = annotationAlpha),
                radius = r,
                center = Offset(cx, cy),
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}

