package com.example.isitai.data.remote

import com.example.isitai.data.model.ContentItem
import com.example.isitai.data.model.PackMetadata
import retrofit2.http.GET
import retrofit2.http.Path

interface ContentApiService {
    @GET("content.json")
    suspend fun getContentManifest(): List<ContentItem>

    @GET("packs.json")
    suspend fun getPackManifest(): List<PackMetadata>

    @GET("packs/{packId}/manifest.json")
    suspend fun getPackContent(@Path("packId") packId: String): List<ContentItem>
}
