package com.example.isitai.data.model

sealed interface DownloadState {
    data object NotDownloaded : DownloadState
    data class Downloading(val current: Int, val total: Int) : DownloadState
    data object Installed : DownloadState
    data class Error(val message: String) : DownloadState
}
