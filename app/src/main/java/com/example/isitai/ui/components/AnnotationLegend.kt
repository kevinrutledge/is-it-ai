package com.example.isitai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.isitai.data.model.Annotation
import com.example.isitai.ui.theme.AnnotationColors

@Composable
fun AnnotationLegend(
    annotations: List<Annotation>,
    modifier: Modifier = Modifier
) {
    val uniqueTypes = annotations
        .map { it.artifactType }
        .distinct()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (type in uniqueTypes) {
            val annotation = annotations.first { it.artifactType == type }
            val color = artifactColor(type)
            val label = artifactLabel(type)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "— ${annotation.description}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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

private fun artifactLabel(type: String): String {
    return when (type) {
        "anatomical" -> "Anatomical"
        "texture" -> "Texture"
        "background" -> "Background"
        "facial" -> "Facial"
        "lighting" -> "Lighting"
        else -> type.replaceFirstChar { it.uppercase() }
    }
}
