package com.example.myce.ui.directions

import android.app.Application
import android.text.Html
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myce.api_interface.NaverApiClient
import com.google.android.gms.location.places.Place
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.overlay.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DirectionsViewModel(application: Application) : AndroidViewModel(application) {

    val currentLocation: MutableLiveData<LatLng> = MutableLiveData()
    private val _naverMap = MutableLiveData<NaverMap>()
    val naverMap: NaverMap? get() = _naverMap.value


    private val _searchResults = MutableLiveData<List<Place>>() //검색어 자동완성 관련
    val searchResults: LiveData<List<Place>> get() = _searchResults

    data class Place(val title: String, val latLng: LatLng)

    fun onMapReady(mapView: MapView) {
        mapView.getMapAsync {
            _naverMap.value = it
        }
    }

    fun tm128ToLatLng(x: Double, y: Double): LatLng {
        val lat = y - 1680000.0 / 1.6 / 10000.0
        val lng = x - 1010000.0 / 1.6 / 10000.0
        return LatLng(lat, lng)
    }

    fun stripHtml(html: String): String {
        return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString()
    }


    fun searchPlaces(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = NaverApiClient.retrofit.searchPlaces(query)
                if (response.isSuccessful) {
                    val result = response.body()?.items?.map {
                        // mapx: 경도, mapy: 위도 (단위는 TM128 → 실제 좌표 변환 필요)
                        val lat = tm128ToLatLng(it.mapx.toDouble(), it.mapy.toDouble()).latitude
                        val lng = tm128ToLatLng(it.mapx.toDouble(), it.mapy.toDouble()).longitude
                        Place(title = stripHtml(it.title), latLng = LatLng(lat, lng))
                    } ?: emptyList()
                    _searchResults.postValue(result)
                } else {
                    Log.e("NAVER_API", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("NAVER_API", "Exception: $e")
            }
        }
    }

    init {
        // Naver Map SDK 초기화
        NaverMapSdk.getInstance(application).client =
            NaverMapSdk.NaverCloudPlatformClient("q6qnl6tmsi")
    }

}