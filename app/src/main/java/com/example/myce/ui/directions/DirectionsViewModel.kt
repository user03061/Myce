package com.example.myce.ui.directions

import android.app.Application
import android.text.Html
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myce.model.MyPlace
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
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

    fun searchPlaces(query: String) { //연관 검색어 mysql로 db값 연동
        viewModelScope.launch(Dispatchers.IO) {

            }
        }
}

