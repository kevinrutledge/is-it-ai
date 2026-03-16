package com.example.isitai.data.remote

import com.example.isitai.data.model.ContentItem
import com.example.isitai.data.model.PackMetadata
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ContentApiService {
    @Headers("Cache-Control: no-cache")
    @GET("content.json")
    suspend fun getContentManifest(): List<ContentItem>

    @Headers("Cache-Control: no-cache")
    @GET("packs.json")
    suspend fun getPackManifest(): List<PackMetadata>

    @Headers("Cache-Control: no-cache")
    @GET("packs/{packId}/manifest.json")
    suspend fun getPackContent(@Path("packId") packId: String): List<ContentItem>
}
