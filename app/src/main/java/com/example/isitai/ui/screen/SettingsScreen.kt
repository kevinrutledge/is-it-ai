package com.example.isitai.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.isitai.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showClearDialog by rememberSaveable { mutableStateOf(false) }
    var showFairUseSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                ListItem(
                    headlineContent = { Text("Dark Mode") },
                    trailingContent = {
                        Switch(
                            checked = viewModel.isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode() }
                        )
                    }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Reset High Score") },
                    modifier = Modifier.clickable { showResetDialog = true }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Clear Downloaded Packs") },
                    modifier = Modifier.clickable { showClearDialog = true }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Fair Use Policy") },
                    modifier = Modifier.clickable { showFairUseSheet = true }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("App Version") },
                    supportingContent = { Text("1.0") }
                )
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            title = { Text("Reset High Score?") },
            text = { Text("Your best streak will be reset to 0.") },
            onDismissRequest = { showResetDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetHighScore()
                    showResetDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }

    if (showClearDialog) {
        AlertDialog(
            title = { Text("Clear Downloaded Packs?") },
            text = { Text("All downloaded content packs will be removed.") },
            onDismissRequest = { showClearDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearDownloadedPacks()
                    showClearDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }

    if (showFairUseSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val policyText = context.assets.open("fair_use_policy.txt")
            .bufferedReader().use { it.readText() }

        ModalBottomSheet(
            onDismissRequest = { showFairUseSheet = false },
            sheetState = sheetState
        ) {
            Text(
                text = policyText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            )
        }
    }
}
