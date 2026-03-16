package com.example.isitai.data.repository

import com.example.isitai.data.model.ContentItem

interface ContentRepository {
    suspend fun getContent(): List<ContentItem>
    suspend fun getContent(selectedPackIds: Set<String>): List<ContentItem>
    fun resolveImageUrl(item: ContentItem): String
}
