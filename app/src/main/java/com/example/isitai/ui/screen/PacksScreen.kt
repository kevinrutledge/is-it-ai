package com.example.isitai.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.isitai.data.model.DownloadState
import com.example.isitai.data.model.PackMetadata
import com.example.isitai.ui.components.PackCard
import com.example.isitai.viewmodel.PackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacksScreen(
    viewModel: PackViewModel,
    modifier: Modifier = Modifier
) {
    var packPendingDelete by rememberSaveable { mutableStateOf<String?>(null) }
    var packPendingDeleteName by rememberSaveable { mutableStateOf("") }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadPacks()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Content Packs",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        when {
            viewModel.isLoading && viewModel.availablePacks.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            viewModel.errorMessage != null && viewModel.availablePacks.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = viewModel.errorMessage ?: "Unknown error",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            else -> {
                val corePack = PackMetadata(
                    id = "core",
                    name = "Core Pack",
                    description = "Starter images bundled with the app",
                    itemCount = viewModel.coreItemCount,
                    sizeMb = 0.0
                )
                val allPacks = listOf(corePack) + viewModel.availablePacks

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(items = allPacks, key = { it.id }) { pack ->
                        val isCore = pack.id == "core"
                        val downloadState = if (isCore) {
                            DownloadState.Installed
                        } else {
                            viewModel.downloadStates[pack.id] ?: DownloadState.NotDownloaded
                        }
                        val isSelected = isCore || pack.id in viewModel.selectedPackIds

                        PackCard(
                            pack = pack,
                            downloadState = downloadState,
                            isSelected = isSelected,
                            isCore = isCore,
                            onDownload = { viewModel.downloadPack(pack.id) },
                            onDelete = {
                                packPendingDelete = pack.id
                                packPendingDeleteName = pack.name
                            },
                            onToggleSelection = { viewModel.togglePackSelection(pack.id) }
                        )
                    }
                }
            }
        }
    }

    if (packPendingDelete != null) {
        AlertDialog(
            title = { Text("Delete '$packPendingDeleteName'?") },
            text = { Text("You'll need to re-download it to use it again.") },
            onDismissRequest = { packPendingDelete = null },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePack(packPendingDelete!!)
                    packPendingDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { packPendingDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
