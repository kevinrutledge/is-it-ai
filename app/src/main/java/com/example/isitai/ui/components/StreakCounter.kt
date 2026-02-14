package com.example.isitai.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class StreakSize { Small, Large }

@Composable
fun StreakCounter(
    streak: Int,
    size: StreakSize,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "\uD83D\uDD25",
            style = when (size) {
                StreakSize.Small -> MaterialTheme.typography.titleMedium
                StreakSize.Large -> MaterialTheme.typography.displayLarge
            }
        )
        Text(
            text = "$streak",
            style = when (size) {
                StreakSize.Small -> MaterialTheme.typography.titleMedium
                StreakSize.Large -> MaterialTheme.typography.displayLarge
            },
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
