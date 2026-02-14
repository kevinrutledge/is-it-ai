package com.example.isitai.data.model

import com.example.isitai.data.repository.ContentRepository.ContentSource
import kotlinx.serialization.Serializable

private const val BASE_URL = "https://kevinrutledge.github.io/is-it-ai-content/"

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

    fun resolveImageUrl(source: ContentSource): String {
        return when (source) {
            ContentSource.REMOTE -> BASE_URL + filename
            ContentSource.BUNDLED -> "file:///android_asset/$filename"
        }
    }
}
