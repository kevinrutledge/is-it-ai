package com.example.isitai.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Annotation(
    val x: Float,
    val y: Float,
    val radius: Float,
    val artifactType: String,
    val label: String,
    val description: String
)
