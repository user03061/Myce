package com.example.myce.ui.directions

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
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
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DirectionsViewModel(private val app: Application) : ViewModel() {

    private val clientId: String
    private val clientSecret: String

    private val _naverMap = MutableLiveData<NaverMap>()
    val naverMap: NaverMap? get() = _naverMap.value

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
            .baseUrl("https://maps.apigw.ntruss.com/") //Naver Geocoding Rest API를 호출하기 위한 URL 쿼리값은 GeocodingService 참조
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder().build()
            )
            .build()
            .create(GeocodingService::class.java)
    }

    fun searchPlaces(query: String, callback: (List<MyPlace>) -> Unit) {
        val call = naverGeocodeApi.getGeocode(query, null, clientId, clientSecret)

        call.enqueue(object : Callback<GeocodeResponse> {
            override fun onResponse(
                call: Call<GeocodeResponse>,
                response: Response<GeocodeResponse>
            ) {
                Log.d("Geocode", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("Geocode", "Response body: $body")

                    val places = body?.addresses?.mapNotNull { addr ->
                        Log.d("Geocode", "Raw addr: x=${addr.x}, y=${addr.y}, road=${addr.roadAddress}, jibun=${addr.jibunAddress}")

                        val lat = addr.y.toDoubleOrNull()
                        val lng = addr.x.toDoubleOrNull()

                        if (lat != null && lng != null) {
                            val latLng = LatLng(lat, lng)
                            Log.d("Geocode", "LatLng parsed: ${latLng.latitude}, ${latLng.longitude}")
                            MyPlace(addr.roadAddress ?: addr.jibunAddress ?: "주소 없음", addr.roadAddress ?: addr.jibunAddress, latLng)
                        } else {
                            Log.w("Geocode", "좌표 변환 실패: x=${addr.x}, y=${addr.y}")
                            null
                        }
                    } ?: emptyList()

                    Log.d("Geocode", "Parsed places: $places")
                    callback(places)
                } else {
                    Log.e("Geocode", "API 실패: ${response.errorBody()?.string()}")
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<GeocodeResponse>, t: Throwable) {
                Log.e("Geocode", "네트워크 오류: ${t.message}", t)
                callback(emptyList())
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

