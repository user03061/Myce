package com.example.myce.api_interface

import com.example.myce.model.NaverApiResponse
import com.example.myce.model.NaverSearchResponse
import okhttp3.Response

import retrofit2.http.GET
import retrofit2.http.Query

interface NaverSearchApi {
        @GET("local.json")
        suspend fun searchPlaces(
            @Query("query") query: String,
            @Query("display") display: Int = 10, // 한 번에 불러올 검색 결과 개수
            @Query("start") start: Int = 1, // 페이지 시작 번호
            @Query("sort") sort: String = "random" // 정렬 기준
        ): Response<NaverApiResponse>
}

