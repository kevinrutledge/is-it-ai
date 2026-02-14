package com.example.isitai.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun GameOverScreen(
    streak: Int,
    isNewRecord: Boolean,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Game Over")
        Text("Streak: $streak")
        if (isNewRecord) {
            Text("New Record!")
        }
        Button(onClick = onPlayAgain) {
            Text("Play Again")
        }
        TextButton(onClick = onHome) {
            Text("Home")
        }
    }
}
