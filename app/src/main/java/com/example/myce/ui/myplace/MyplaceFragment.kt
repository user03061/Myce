package com.example.myce.ui.myplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myce.databinding.FragmentMyplaceBinding
import com.naver.maps.map.MapView

class MyplaceFragment : Fragment() {

    private var _binding: FragmentMyplaceBinding? = null

    private lateinit var myplaceViewModel: MyplaceViewModel

    private lateinit var mapView: MapView //왼쪽 id 오른쪽 네이버api 명칭



    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {


        myplaceViewModel = ViewModelProvider(this).get(MyplaceViewModel::class.java)

        _binding = FragmentMyplaceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        onMapReady(mapView)


        return root
    }

    private fun onMapReady(mapView: MapView) {
        mapView.getMapAsync { naverMap ->
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mapView.onDestroy()  // 지도 리소스 해제
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()  // 지도 라이프사이클 관리
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()  // 지도 라이프사이클 관리
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()  // 지도 라이프사이클 관리
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()  // 지도 라이프사이클 관리
    }
}