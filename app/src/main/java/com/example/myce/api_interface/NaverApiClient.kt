package com.example.myce.api_interface

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NaverApiClient {
    private const val BASE_URL = "https://openapi.naver.com/v1/search/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(provideOkHttpClient())
        .build()

    val apiService: NaverSearchApi = retrofit.create(NaverSearchApi::class.java)

    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-Naver-Client-Id", "YOUR_CLIENT_ID")
                    .addHeader("X-Naver-Client-Secret", "YOUR_CLIENT_SECRET")
                    .build()
                chain.proceed(request)
            }
            .build()
    }
}

