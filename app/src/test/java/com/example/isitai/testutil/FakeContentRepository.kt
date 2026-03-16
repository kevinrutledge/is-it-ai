package com.example.isitai.testutil

import com.example.isitai.data.model.ContentItem
import com.example.isitai.data.repository.ContentRepository

class FakeContentRepository : ContentRepository {
    var items: List<ContentItem> = emptyList()

    override suspend fun getContent(): List<ContentItem> = items

    override suspend fun getContent(selectedPackIds: Set<String>): List<ContentItem> = items

    override fun resolveImageUrl(item: ContentItem): String = "test://${item.filename}"
}
