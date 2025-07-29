package com.example.myce.ui.directions

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myce.databinding.FragmentDirectionsBinding
import com.example.myce.model.MyPlace
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView

class DirectionsFragment : Fragment() {

    private lateinit var directionsViewModel: DirectionsViewModel

    private var _binding: FragmentDirectionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterA: PlaceSearchAdapter
    private lateinit var adapterB: PlaceSearchAdapter

    private var selectedPlaceA: MyPlace? = null
    private var selectedPlaceB: MyPlace? = null

    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDirectionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // ViewModel 초기화
        val factory = DirectionsViewModelFactory(requireActivity().application)
        directionsViewModel = ViewModelProvider(this, factory)[DirectionsViewModel::class.java]

        // MapView 초기화
        mapView = binding.mapView
        directionsViewModel.onMapReady(mapView)

        adapterA = PlaceSearchAdapter { place ->
            selectedPlaceA = place // 저장
            binding.routeA.setText(place.title)
            binding.recyclerResultsA.visibility = View.GONE
            directionsViewModel.naverMap?.moveCamera(
                CameraUpdate.scrollAndZoomTo(place.latLng, 15.0).animate(CameraAnimation.Easing)
            )
        }

        binding.recyclerResultsA.adapter = adapterA
        binding.recyclerResultsA.layoutManager = LinearLayoutManager(context)
        binding.recyclerResultsA.visibility = View.GONE

        adapterB = PlaceSearchAdapter { place ->
            selectedPlaceB = place // 저장
            binding.routeB.setText(place.title)
            binding.recyclerResultsB.visibility = View.GONE
            directionsViewModel.naverMap?.moveCamera(
                CameraUpdate.scrollAndZoomTo(place.latLng, 15.0).animate(CameraAnimation.Easing)
            )
        }

        binding.recyclerResultsB.adapter = adapterB
        binding.recyclerResultsB.layoutManager = LinearLayoutManager(context)
        binding.recyclerResultsB.visibility = View.GONE


        binding.routeA.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.length >= 2) {
                    directionsViewModel.searchPlaces(query)
                    binding.recyclerResultsA.visibility = View.VISIBLE
                } else {
                    binding.recyclerResultsA.visibility = View.GONE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.routeB.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.length >= 2) {
                    directionsViewModel.searchPlaces(query)
                    binding.recyclerResultsB.visibility = View.VISIBLE
                } else {
                    binding.recyclerResultsB.visibility = View.GONE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        binding.routeA.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding.recyclerResultsA.visibility = View.GONE
                selectedPlaceA?.let {
                    directionsViewModel.naverMap?.moveCamera(
                        CameraUpdate.scrollAndZoomTo(it.latLng, 15.0).animate(CameraAnimation.Easing)
                    )
                }
            }
        }

        binding.routeB.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding.recyclerResultsB.visibility = View.GONE
                selectedPlaceB?.let {
                    directionsViewModel.naverMap?.moveCamera(
                        CameraUpdate.scrollAndZoomTo(it.latLng, 15.0).animate(CameraAnimation.Easing)
                    )
                }
            }
        }


        directionsViewModel.searchResults.observe(viewLifecycleOwner) { places ->
            if (binding.routeA.hasFocus()) {
                adapterA.submitList(places)
                binding.recyclerResultsA.visibility = View.VISIBLE
            } else if (binding.routeB.hasFocus()) {
                adapterB.submitList(places)
                binding.recyclerResultsB.visibility = View.VISIBLE
            } else {
                binding.recyclerResultsA.visibility = View.GONE
                binding.recyclerResultsB.visibility = View.GONE

                Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSearch.setOnClickListener {
            val query = binding.routeA.text.toString().trim()
            if (query.length >= 2) {
                directionsViewModel.searchPlaces(query)
            }
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mapView.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }
}
