package com.example.myce.api_interface

import com.example.myce.model.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverSearchService {
    @GET("v1/search/local.json")
    fun searchLocal(
        @Query("query") query: String,
        @Query("display") display: Int = 5,
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String
    ): Call<SearchResponse>
}
