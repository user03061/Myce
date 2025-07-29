package com.example.myce.api_interface

data class NaverSearchResponse(
    val items: List<NaverSearchItem>
)

data class NaverSearchItem(
    val title: String,
    val link: String,
    val category: String,
    val description: String,
    val telephone: String,
    val address: String,
    val roadAddress: String,
    val mapx: String,
    val mapy: String
)
