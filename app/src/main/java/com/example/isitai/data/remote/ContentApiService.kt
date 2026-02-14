package com.example.isitai.data.remote

import com.example.isitai.data.model.ContentItem
import retrofit2.http.GET

interface ContentApiService {
    @GET("content.json")
    suspend fun getContentManifest(): List<ContentItem>
}
