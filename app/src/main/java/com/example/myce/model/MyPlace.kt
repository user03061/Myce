package com.example.myce.model

import com.naver.maps.geometry.LatLng

data class MyPlace(
    val title: String,
    val address: String? = null,
    val latLng: LatLng
)


