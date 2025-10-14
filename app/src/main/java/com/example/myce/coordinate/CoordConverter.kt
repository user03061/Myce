package com.example.myce.coordinate

import org.locationtech.proj4j.BasicCoordinateTransform
import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.ProjCoordinate

// 결과를 담을 데이터 클래스 (ViewModel에서 nullable로 체크하고 있으므로 Double? 로 정의)
data class Wgs84(
    val latitude: Double?,
    val longitude: Double?
)

object CoordConverter {

    fun convertTmToWgs84(tmX: Double, tmY: Double): Wgs84 {
        try {
            val crsFactory = CRSFactory()

            // 소스 좌표계 설정 (Naver API가 사용하는 TM-128 좌표계 - EPSG:5179)
            val srcCrs = crsFactory.createFromParameters(
                "EPSG:5179",
                "+proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=GRS80 +units=m +no_defs"
            )

            // 타겟 좌표계 설정 (WGS84, 표준 위경도)
            val dstCrs = crsFactory.createFromParameters(
                "EPSG:4326",
                "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs"
            )

            val transform = BasicCoordinateTransform(srcCrs, dstCrs)

            val srcCoord = ProjCoordinate(tmX, tmY)
            val dstCoord = ProjCoordinate()

            transform.transform(srcCoord, dstCoord)

            return Wgs84(
                latitude = dstCoord.y,
                longitude = dstCoord.x
            )
        } catch (e: Exception) {
            // 변환 실패 시 로그를 남기고 null 반환
            // 잘못된 좌표가 들어올 경우 예외 발생 가능
            e.printStackTrace()
            return Wgs84(null, null)
        }
    }
}