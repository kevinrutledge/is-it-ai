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
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
        }
    ) {
        if (imageSize.width == 0 || imageSize.height == 0) return@Canvas

        val scaleFactor = minOf(
            size.width / imageSize.width.toFloat(),
            size.height / imageSize.height.toFloat()
        )
        val offsetX = (size.width - imageSize.width * scaleFactor) / 2f
        val offsetY = (size.height - imageSize.height * scaleFactor) / 2f

        // Draw scrim over entire area
        drawRect(color = Color.Black.copy(alpha = 0.32f))

        // Cut out spotlight holes for each annotation
        for (annotation in annotations) {
            val cx = offsetX + annotation.x * imageSize.width * scaleFactor
            val cy = offsetY + annotation.y * imageSize.height * scaleFactor
            val r = annotation.radius * imageSize.width * scaleFactor

            drawCircle(
                color = Color.Black,
                radius = r,
                center = Offset(cx, cy),
                style = Fill,
                blendMode = BlendMode.Clear
            )
        }

        // Draw colored stroke circles on top
        for (annotation in annotations) {
            val cx = offsetX + annotation.x * imageSize.width * scaleFactor
            val cy = offsetY + annotation.y * imageSize.height * scaleFactor
            val r = annotation.radius * imageSize.width * scaleFactor
            val color = artifactColor(annotation.artifactType)

            drawCircle(
                color = color,
                radius = r,
                center = Offset(cx, cy),
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}

private fun artifactColor(type: String): Color {
    return when (type) {
        "anatomical" -> AnnotationColors.Anatomical
        "texture" -> AnnotationColors.Texture
        "background" -> AnnotationColors.Background
        "facial" -> AnnotationColors.Facial
        "lighting" -> AnnotationColors.Lighting
        else -> Color.Gray
    }
}
