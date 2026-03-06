package com.example.isitai.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Content Packs",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
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
                    itemCount = 9,
                    difficulty = "mixed"
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
                            onDelete = { viewModel.deletePack(pack.id) },
                            onToggleSelection = { viewModel.togglePackSelection(pack.id) }
                        )
                    }
                }
            }
        }
    }
}
