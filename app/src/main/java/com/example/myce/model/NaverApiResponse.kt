package com.example.myce.model

data class NaverApiResponse(
    val items: List<NaverPlace>
)

data class NaverPlace(
    val title: String,
    val mapx: String, // 경도
    val mapy: String  // 위도
)
