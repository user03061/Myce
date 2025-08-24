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
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DirectionsViewModel(private val app: Application) : ViewModel() {

    val currentLocation: MutableLiveData<LatLng> = MutableLiveData()
    private val _naverMap = MutableLiveData<NaverMap>()
    val naverMap: NaverMap? get() = _naverMap.value

    private val _searchResults = MutableLiveData<List<MyPlace>>() //이거 나중에 연관 검색어(?) 기능 만들어서 리스트에 뜨게하려고 함
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
            .getApplicationInfo(app.packageName, PackageManager.GET_META_DATA) //메니페스트 메타 데이터 네임 참조
            .metaData

        val clientId = metaData.getString("com.naver.maps.map.NCP_KEY_ID") ?: ""
        val clientSecret = metaData.getString("NAVER_CLIENT_SECRET") ?: ""

        Log.d("DirectionsViewModel", "clientId: $clientId, clientSecret: $clientSecret") //키 정상 연결 되있음

        return Pair(clientId, clientSecret)
    }

    private val naverGeocodeApi: GeocodingService by lazy {
        Retrofit.Builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com/") //Naver Geocoding Rest API를 호출하기 위한 URL 쿼리값은 GeocodingService 참조
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingService::class.java)
    }

    fun searchPlaces(query: String) {
        val call = naverGeocodeApi.getGeocode(query, clientId, clientSecret) // interface -> GeocodingService 변수 값

        call.enqueue(object : retrofit2.Callback<GeocodeResponse> {
            override fun onResponse(
                call: Call<GeocodeResponse>,
                response: retrofit2.Response<GeocodeResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val places = body?.addresses?.map { addr ->
                        MyPlace(
                            title = addr.roadAddress ?: addr.jibunAddress ?: "주소 없음",
                            latLng = LatLng(addr.y.toDouble(), addr.x.toDouble()) //latLng 형태로 변환해서 반환해야 함
                        )
                    } ?: emptyList()
                    _searchResults.postValue(places)
                } else {
                    val errorBody = response.errorBody()?.string() //permission denied 에러가 뜸 geocoding 권한이 없다는데..
                    Log.e("ViewModel", "응답 실패: ${response.code()} - body: $errorBody")
                    _searchResults.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<GeocodeResponse>, t: Throwable) {
                Log.e("ViewModel", "지오코딩 호출 실패", t) //도달 안함
                _searchResults.postValue(emptyList())
            }
        })
    }



    fun onMapReady(mapView: MapView) {
        mapView.getMapAsync { map ->
            _naverMap.value = map
            Log.d("DirectionsViewModel", "NaverMap 초기화 완료")
            val metaData = app.packageManager
                .getApplicationInfo(app.packageName, PackageManager.GET_META_DATA)
                .metaData

        }
    }


}



