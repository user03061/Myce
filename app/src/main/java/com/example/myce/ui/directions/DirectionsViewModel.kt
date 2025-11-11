package com.example.myce.ui.directions

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myce.api_interface.GeocodingService
import com.example.myce.api_interface.NaverSearchService
import com.example.myce.coordinate.CoordConverter
import com.example.myce.model.GeocodeResponse
import com.example.myce.model.MyPlace
import com.example.myce.model.SearchResponse
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DirectionsViewModel(private val app: Application) : ViewModel() {

    private val TAG = "DirectionsViewModel"

    private val _naverMap = MutableLiveData<NaverMap>()
    val naverMap: NaverMap? get() = _naverMap.value

    //Naver Search API 키 받기
    private fun getSearchApiKeys(): Pair<String, String> {
        val metaData = app.packageManager
            .getApplicationInfo(app.packageName, PackageManager.GET_META_DATA)
            .metaData

        val clientId = metaData.getString("com.naver.search.api.CLIENT_ID") ?: ""
        val clientSecret = metaData.getString("com.naver.search.api.CLIENT_SECRET") ?: ""

        Log.d(TAG, "SearchAPI Key loaded: $clientId / $clientSecret")
        return Pair(clientId, clientSecret)
    }

    //Naver Geocoding API 키 받기
    private fun getGeoApiKeys(): Pair<String, String> {
        val metaData = app.packageManager
            .getApplicationInfo(app.packageName, PackageManager.GET_META_DATA)
            .metaData

        val clientId = metaData.getString("com.naver.maps.map.NCP_KEY_ID") ?: ""
        val clientSecret = metaData.getString("NAVER_CLIENT_SECRET") ?: ""

        Log.d(TAG, "GeocodeAPI Key loaded: $clientId / $clientSecret")
        return Pair(clientId, clientSecret)
    }

    //장소 검색 — Naver Search API → Geocoding API 순서
    fun searchPlaces(query: String, callback: (List<MyPlace>) -> Unit) {
        val (searchClientId, searchClientSecret) = getSearchApiKeys()

        val retrofitSearch = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val searchApi = retrofitSearch.create(NaverSearchService::class.java)
        val call = searchApi.searchLocal(query, display = 3, clientId = searchClientId, clientSecret = searchClientSecret)

        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val items = response.body()?.items ?: emptyList()
                    if (items.isNotEmpty()) {
                        val results = items.mapNotNull { item ->
                            val mapx = item.mapx.toDoubleOrNull() ?: 0.0
                            val mapy = item.mapy.toDoubleOrNull() ?: 0.0

                            //좌표변환 관련 중요 부분
                            val finalLatLng: LatLng

                            // mapx 값이 비정상적으로 크면 (천만 이상), WGS84 좌표로 간주
                            if (mapx > 10000000) {
                                Log.d(TAG, "WGS84 좌표로 처리: mapx=${mapx}, mapy=${mapy}")
                                // 1000만으로 나누고, mapy가 위도(lat), mapx가 경도(lon)다
                                finalLatLng = LatLng(mapy / 10000000.0, mapx / 10000000.0)
                            } else {
                                // 그렇지 않으면 기존처럼 TM 좌표로 간주하고 변환
                                Log.d(TAG, "TM 좌표로 처리: mapx=${mapx}, mapy=${mapy}")
                                val wgs84Coords = CoordConverter.convertTmToWgs84(mapx, mapy)
                                finalLatLng = LatLng(mapx, mapy)
                            }

                            MyPlace(
                                title = android.text.Html.fromHtml(item.title, android.text.Html.FROM_HTML_MODE_LEGACY).toString(),
                                address = item.roadAddress ?: item.address ?: "",
                                latLng = finalLatLng
                            )
                        }
                        callback(results)
                    } else {
                        Log.d(TAG, "Search API 결과 없음 → Geocoding API 재시도")
                        searchWithGeocoding(query, callback)
                    }
                } else {
                    Log.e(TAG, "Search API 실패: ${response.code()}")
                    searchWithGeocoding(query, callback)
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e(TAG, "Search API 호출 실패: ${t.message}")
                searchWithGeocoding(query, callback)
            }
        })
    }

    // Geocoding API로 검색
    private fun searchWithGeocoding(query: String, callback: (List<MyPlace>) -> Unit) {
        val (geoClientId, geoClientSecret) = getGeoApiKeys()

        val retrofitGeo = Retrofit.Builder()
            .baseUrl("https://maps.apigw.ntruss.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val geocodeApi = retrofitGeo.create(GeocodingService::class.java)
        val call = geocodeApi.getGeocode(query, clientId = geoClientId, clientSecret = geoClientSecret)

        call.enqueue(object : Callback<GeocodeResponse> {
            override fun onResponse(call: Call<GeocodeResponse>, response: Response<GeocodeResponse>) {
                if (response.isSuccessful) {
                    val addresses = response.body()?.addresses ?: emptyList()
                    if (addresses.isNotEmpty()) {
                        val results = addresses.map { addr ->
                            MyPlace(
                                title = addr.roadAddress ?: addr.jibunAddress ?: query,
                                address = addr.roadAddress ?: addr.jibunAddress ?: "",
                                latLng = LatLng(addr.y.toDouble(), addr.x.toDouble())
                            )
                        }
                        Log.d(TAG, "Geocoding API 성공 - 결과 ${results.size}개")
                        callback(results)
                    } else {
                        Log.d(TAG, "Geocoding 결과 없음")
                        callback(emptyList())
                    }
                } else {
                    Log.e(TAG, "Geocoding API 실패: ${response.code()}")
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<GeocodeResponse>, t: Throwable) {
                Log.e(TAG, "Geocoding API 호출 실패: ${t.message}")
                callback(emptyList())
            }
        })
    }

    //지도 초기화
    fun onMapReady(mapView: MapView) {
        mapView.getMapAsync { map ->
            _naverMap.value = map
            Log.d(TAG, "NaverMap 초기화 완료")
        }
    }
}
