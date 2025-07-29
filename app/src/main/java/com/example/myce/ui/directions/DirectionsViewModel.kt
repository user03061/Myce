package com.example.myce.ui.directions

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myce.api_interface.GeocodingService
import com.example.myce.model.GeocodeResponse
import com.example.myce.model.MyPlace
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DirectionsViewModel(private val app: Application) : ViewModel() {

    val currentLocation: MutableLiveData<LatLng> = MutableLiveData()
    private val _naverMap = MutableLiveData<NaverMap>()
    val naverMap: NaverMap? get() = _naverMap.value

    private val _searchResults = MutableLiveData<List<MyPlace>>()
    val searchResults: LiveData<List<MyPlace>> get() = _searchResults

    private val clientId: String
    private val clientSecret: String

    init {
        val keys = getNaverKeys()
        clientId = keys.first
        clientSecret = keys.second
    }

    private fun getNaverKeys(): Pair<String, String> {
        val metaData = app.packageManager
            .getApplicationInfo(app.packageName, PackageManager.GET_META_DATA)
            .metaData

        val clientId = metaData.getString("NAVER_CLIENT_ID") ?: ""
        val clientSecret = metaData.getString("NAVER_CLIENT_SECRET") ?: ""
        return Pair(clientId, clientSecret)
    }

    private val naverGeocodeApi: GeocodingService by lazy {
        Retrofit.Builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                            .build()
            )
            .build()
            .create(GeocodingService::class.java)
    }

    fun searchPlaces(query: String) {
        val call = naverGeocodeApi.getGeocode(query, clientId, clientSecret)

        call.enqueue(object : retrofit2.Callback<GeocodeResponse> {
            override fun onResponse(call: Call<GeocodeResponse>, response: retrofit2.Response<GeocodeResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val places = body?.addresses?.map { addr ->
                        MyPlace(
                            title = addr.roadAddress ?: addr.jibunAddress ?: "주소 없음",
                            latLng = LatLng(addr.y.toDouble(), addr.x.toDouble())
                        )
                    } ?: emptyList()

                    _searchResults.postValue(places)
                } else {

                    Log.e("ViewModel", "응답 실패: ${response.code()}")
                    _searchResults.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<GeocodeResponse>, t: Throwable) {
                Log.e("ViewModel", "지오코딩 호출 실패", t)
                _searchResults.postValue(emptyList())
            }
        })
    }


    fun onMapReady(mapView: MapView) {
        mapView.getMapAsync { map ->
            _naverMap.value = map
            Log.d("DirectionsViewModel", "NaverMap 초기화 완료")
        }
    }

}



