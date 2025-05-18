package com.example.myce.ui.directions

import android.app.Application
import android.text.Html
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myce.api_interface.NaverApiClient
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

    private val _searchResults = MutableLiveData<List<Place>>()
    val searchResults: LiveData<List<Place>> get() = _searchResults

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
                // API 호출
                val response = NaverApiClient.apiService.searchPlaces(query)

                // 응답이 성공적인지 확인
                if (response.isSuccessful) {
                    // 응답 본문 가져오기 (NaverApiResponse)
                    val result = response.body()?.items?.map {
                        val lat = tm128ToLatLng(it.mapx.toDouble(), it.mapy.toDouble()).latitude
                        val lng = tm128ToLatLng(it.mapx.toDouble(), it.mapy.toDouble()).longitude
                        Place(title = stripHtml(it.title), latLng = LatLng(lat, lng))
                    } ?: emptyList()

                    // 결과를 LiveData에 설정
                    _searchResults.postValue(result)
                } else {
                    // 실패한 경우 오류 처리
                    Log.e("NAVER_API", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                // 예외 처리 (네트워크 오류 등)
                Log.e("NAVER_API", "Exception: $e")
            }
        }
    }


    init {
        NaverMapSdk.getInstance(application).client =
            NaverMapSdk.NaverCloudPlatformClient("YOUR_NAVER_CLIENT_ID")
    }
}

data class Place(
    val title: String,
    val latLng: LatLng
)
