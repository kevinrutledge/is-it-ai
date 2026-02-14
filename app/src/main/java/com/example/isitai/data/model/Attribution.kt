package com.example.isitai.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Attribution(
    val photographer: String,
    val year: Int,
    val context: String
)
