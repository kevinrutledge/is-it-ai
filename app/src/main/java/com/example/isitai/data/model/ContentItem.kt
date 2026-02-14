package com.example.isitai.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ContentItem(
    val id: String,
    val filename: String,
    val type: String,
    val difficulty: String,
    val explanation: String? = null,
    val attribution: Attribution? = null,
    val whyItLookedAI: String? = null,
    val story: String? = null,
    val annotations: List<Annotation> = emptyList()
) {
    val isAI: Boolean get() = type == "ai"
    val isReal: Boolean get() = type == "real"
}
