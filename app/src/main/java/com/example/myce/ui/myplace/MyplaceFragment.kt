package com.example.myce.ui.directions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myce.databinding.FragmentDirectionsBinding
import com.example.myce.databinding.FragmentMyplaceBinding
import com.example.myce.model.MyPlace
import com.example.myce.ui.myplace.MyplaceViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView

class MyplaceFragment : Fragment() {

    private lateinit var MyplaceViewModel: MyplaceViewModel
    private var _binding: FragmentMyplaceBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlaceSearchAdapter

    private var searchPlace: MyPlace? = null

    private lateinit var mapView: MapView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyplaceBinding.inflate(inflater, container, false)
        val root = binding.root


        mapView = binding.mapView
       // MyplaceViewModel.onMapReady(mapView)

        adapter = PlaceSearchAdapter { place ->
            searchPlace = place
            binding.searchPlace.setText(place.title)
        }


        binding.btnSearch.setOnClickListener {
           // searchPlace()
        }

        return root
    }

//    private fun searchPlace() {
//        val query = binding.routeA.text.toString().trim()
//        if (query.isNotEmpty()) {
//            MyplaceViewModel.searchPlaces(query) { places ->
//                if (places.isNotEmpty()) {
//                    searchPlace = places[0]
//                    MyplaceViewModel.naverMap?.moveCamera(
//                        CameraUpdate.scrollAndZoomTo(searchPlace!!.latLng, 17.0)
//                            .animate(CameraAnimation.Easing)
//                    )
//                } else {
//                    Toast.makeText(context, "경로 A 검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//        mapView.onDestroy()
//    }
//
//    override fun onStart() { super.onStart(); mapView.onStart() }
//    override fun onResume() { super.onResume(); mapView.onResume() }
//    override fun onPause() { super.onPause(); mapView.onPause() }
//    override fun onStop() { super.onStop(); mapView.onStop() }
//    override fun onSaveInstanceState(outState: Bundle) { super.onSaveInstanceState(outState); mapView.onSaveInstanceState(outState)
//    }
}
