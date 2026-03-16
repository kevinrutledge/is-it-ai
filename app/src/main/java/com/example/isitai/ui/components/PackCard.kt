package com.example.isitai.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.isitai.data.model.DownloadState
import com.example.isitai.data.model.PackMetadata

@Composable
fun PackCard(
    pack: PackMetadata,
    downloadState: DownloadState,
    isSelected: Boolean,
    isCore: Boolean,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    onToggleSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Optional thumbnail
            if (pack.thumbnailUrl.isNotEmpty()) {
                AsyncImage(
                    model = pack.thumbnailUrl,
                    contentDescription = "${pack.name} thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = pack.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = pack.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val sizeText = if (isCore) "Included" else "%.1f MB".format(pack.sizeMb)
                Text(
                    text = "${pack.itemCount} images · $sizeText",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isCore || downloadState is DownloadState.Installed) {
                Switch(
                    checked = isSelected,
                    onCheckedChange = { if (!isCore) onToggleSelection() },
                    enabled = !isCore
                )
            }
        }

        when (downloadState) {
            is DownloadState.NotDownloaded -> {
                PillButton(
                    text = "Download",
                    onClick = onDownload,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }
            is DownloadState.Downloading -> {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Downloading ${downloadState.current} of ${downloadState.total}...",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LinearProgressIndicator(
                        progress = {
                            if (downloadState.total > 0) {
                                downloadState.current.toFloat() / downloadState.total
                            } else 0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                    )
                }
            }
            is DownloadState.Installed -> {
                if (!isCore) {
                    TextButton(
                        onClick = onDelete,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "Delete",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            is DownloadState.Error -> {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Download failed. Check your connection and try again.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    PillButton(
                        text = "Retry",
                        onClick = onDownload,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
