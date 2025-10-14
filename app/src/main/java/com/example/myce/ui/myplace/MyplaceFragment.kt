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
import com.example.myce.model.MyPlace
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView

class MyplaceFragment : Fragment() {

    private lateinit var directionsViewModel: DirectionsViewModel
    private var _binding: FragmentDirectionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterA: PlaceSearchAdapter
    private lateinit var adapterB: PlaceSearchAdapter

    private var selectedPlaceA: MyPlace? = null
    private var selectedPlaceB: MyPlace? = null

    private lateinit var mapView: MapView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDirectionsBinding.inflate(inflater, container, false)
        val root = binding.root

        val factory = DirectionsViewModelFactory(requireActivity().application)
        directionsViewModel = ViewModelProvider(this, factory)[DirectionsViewModel::class.java]

        mapView = binding.mapView
        directionsViewModel.onMapReady(mapView)

        adapterA = PlaceSearchAdapter { place ->
            selectedPlaceA = place
            binding.routeA.setText(place.title)
            binding.recyclerResultsA.visibility = View.GONE
        }
        binding.recyclerResultsA.adapter = adapterA
        binding.recyclerResultsA.layoutManager = LinearLayoutManager(context)

        adapterB = PlaceSearchAdapter { place ->
            selectedPlaceB = place
            binding.routeB.setText(place.title)
            binding.recyclerResultsB.visibility = View.GONE
        }
        binding.recyclerResultsB.adapter = adapterB
        binding.recyclerResultsB.layoutManager = LinearLayoutManager(context)

        // 검색 버튼 클릭 시 Geocoding 수행
        binding.btnSearch.setOnClickListener {
            searchPlaceA()
            searchPlaceB()
        }

        return root
    }

    private fun searchPlaceA() {
        val query = binding.routeA.text.toString().trim()
        if (query.isNotEmpty()) {
            directionsViewModel.searchPlaces(query) { places ->
                if (places.isNotEmpty()) {
                    selectedPlaceA = places[0]
                    binding.recyclerResultsA.visibility = View.GONE
                    directionsViewModel.naverMap?.moveCamera(
                        CameraUpdate.scrollAndZoomTo(selectedPlaceA!!.latLng, 17.0)
                            .animate(CameraAnimation.Easing)
                    )
                } else {
                    Toast.makeText(context, "경로 A 검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun searchPlaceB() {
        val query = binding.routeB.text.toString().trim()
        if (query.isNotEmpty()) {
            directionsViewModel.searchPlaces(query) { places ->
                if (places.isNotEmpty()) {
                    selectedPlaceB = places[0]
                    binding.recyclerResultsB.visibility = View.GONE
                    directionsViewModel.naverMap?.moveCamera(
                        CameraUpdate.scrollAndZoomTo(selectedPlaceB!!.latLng, 17.0)
                            .animate(CameraAnimation.Easing)
                    )
                } else {
                    Toast.makeText(context, "경로 B 검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mapView.onDestroy()
    }

    override fun onStart() { super.onStart(); mapView.onStart() }
    override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onPause() { super.onPause(); mapView.onPause() }
    override fun onStop() { super.onStop(); mapView.onStop() }
    override fun onSaveInstanceState(outState: Bundle) { super.onSaveInstanceState(outState); mapView.onSaveInstanceState(outState) }
}
