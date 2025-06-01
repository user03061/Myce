package com.example.myce.ui.directions

import android.app.Application
import android.text.Html
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myce.api_interface.NaverApiClient
import com.example.myce.model.MyPlace
import com.google.android.gms.location.places.Place
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DirectionsViewModel(application: Application) : AndroidViewModel(application) {

    val currentLocation: MutableLiveData<LatLng> = MutableLiveData()
    private val _naverMap = MutableLiveData<NaverMap>()
    val naverMap: NaverMap? get() = _naverMap.value

    private val _searchResults = MutableLiveData<List<MyPlace>>()
    val searchResults: LiveData<List<MyPlace>> get() = _searchResults


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
                val response = NaverApiClient.apiService.searchPlaces(query)

                if (response.isSuccessful) {
                    val result = response.body()?.items?.map { item ->
                        // TM128 → 위경도 변환
                        val latLng = tm128ToLatLng(item.mapx.toDouble(), item.mapy.toDouble())

                        // HTML 제거 및 객체 생성
                        MyPlace(title = stripHtml(item.title), latLng = latLng)
                    } ?: emptyList()

                    _searchResults.postValue(result)
                } else {
                    Log.e("NAVER_API", "API Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("NAVER_API", "Exception: ${e.message}", e)
            }
        }
    }



    init {
        NaverMapSdk.getInstance(application).client =
            NaverMapSdk.NaverCloudPlatformClient("YOUR_NAVER_CLIENT_ID")
    }
}

