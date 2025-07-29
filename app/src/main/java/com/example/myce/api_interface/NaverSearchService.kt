package com.example.myce.api_interface

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverSearchService {
    @GET("/v1/search/local.json")
    suspend fun searchPlaces(
        @Query("query") query: String,
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String
    ): NaverSearchResponse
}
