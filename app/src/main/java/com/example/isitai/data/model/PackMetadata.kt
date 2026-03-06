package com.example.isitai.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PackMetadata(
    val id: String,
    val name: String,
    val description: String,
    val itemCount: Int,
    val difficulty: String
)
