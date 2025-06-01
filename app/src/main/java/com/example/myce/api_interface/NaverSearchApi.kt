package com.example.myce.api_interface

import com.example.myce.model.NaverApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NaverSearchApi {
    @GET("local.json")
    suspend fun searchPlaces(
        @Query("query") query: String,
        @Query("display") display: Int = 10,
        @Query("start") start: Int = 1,
        @Query("sort") sort: String = "random"
    ): Response<NaverApiResponse>
}
