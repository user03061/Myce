package com.example.myce.load

import android.app.Application
import com.example.myce.BuildConfig
import com.naver.maps.map.NaverMapSdk

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NcpKeyClient(BuildConfig.NAVER_CLIENT_ID)
    }
}

