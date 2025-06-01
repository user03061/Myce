package com.example.myce.model

data class NaverSearchResponse(
    val items: List<NaverPlaceItem>
)

data class NaverPlaceItem(
    val title: String,
    val address: String,
    val mapx: String,
    val mapy: String
)


//data class NaverSearchResponse(
//    @SerializedName("title") val title: String,
//    @SerializedName("mapx") val mapx: String,
//    @SerializedName("mapy") val mapy: String
//)