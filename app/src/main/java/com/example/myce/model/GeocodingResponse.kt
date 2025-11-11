package com.example.myce.model

data class GeocodeResponse(
    val addresses: List<GeocodeAddress>
)

data class GeocodeAddress(
    val roadAddress: String?,
    val jibunAddress: String?,
    val x: String,  // longitude
    val y: String   // latitude
)
