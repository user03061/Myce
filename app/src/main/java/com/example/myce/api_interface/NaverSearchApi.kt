package com.example.myce.api_interface

import com.example.myce.model.NaverSearchResponse
import okhttp3.Response

import retrofit2.http.GET
import retrofit2.http.Query

interface NaverSearchApi {
    @GET("v1/search/local.json")
    suspend fun searchPlaces(
        @Query("query") query: String,
        @Query("display") display: Int = 10
    ): Response<NaverSearchResponse>
}
