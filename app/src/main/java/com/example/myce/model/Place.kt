package com.example.myce.model

import com.naver.maps.geometry.LatLng

data class Place(
    val title: String,
    val address: String,
    val latLng: LatLng
)



