package com.example.myce.ui.directions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.overlay.Marker

class DirectionsViewModel(application: Application) : AndroidViewModel(application) {

    val currentLocation: MutableLiveData<LatLng> = MutableLiveData()

    private var naverMap: NaverMap? = null

    init {
        // Naver Map SDK 초기화
        NaverMapSdk.getInstance(application).client =
            NaverMapSdk.NaverCloudPlatformClient("q6qnl6tmsi")
    }

    fun onMapReady(mapView: MapView) {
        // 네이버 맵이 준비되면 호출되는 콜백
        mapView.getMapAsync { map ->
            this.naverMap = map

            // 예시로 임의의 위치를 설정 (서울 위치)
            //val currentLatLng = LatLng(37.5665, 126.9780)  // 서울 좌표
           // _currentLocation.postValue(currentLatLng)

            // 마커 추가
            //val marker = Marker()
            //marker.position = currentLatLng
            //marker.map = map

            // 카메라 위치 설정
            //map.moveCamera(CameraPosition(currentLatLng, 10.0))
        }
    }
}
