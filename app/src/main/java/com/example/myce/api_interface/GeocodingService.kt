package com.example.myce.api_interface

import com.example.myce.model.GeocodeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GeocodingService {
    @GET("map-geocode/v2/geocode")
    fun getGeocode(
        @Query("query") query: String,
        @Query("coordinate") coordinate: String? = null,
        @Header("X-NCP-APIGW-API-KEY-ID") clientId: String,
        @Header("X-NCP-APIGW-API-KEY") clientSecret: String
    ): Call<GeocodeResponse>
}

