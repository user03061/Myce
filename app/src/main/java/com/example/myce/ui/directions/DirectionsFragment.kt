package com.example.myce.ui.directions

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myce.databinding.FragmentDirectionsBinding
import com.google.android.gms.maps.CameraUpdate
import com.naver.maps.map.MapView

class DirectionsFragment : Fragment() {

    private lateinit var directionsViewModel: DirectionsViewModel

    private var _binding: FragmentDirectionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDirectionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // ViewModel을 factory로 초기화
        val factory = DirectionsViewModelFactory(requireActivity().application)
        directionsViewModel = ViewModelProvider(this, factory).get(DirectionsViewModel::class.java)

        // MapView 초기화
        mapView = binding.mapView
        directionsViewModel.onMapReady(mapView)

        // LiveData 관찰
        directionsViewModel.currentLocation.observe(viewLifecycleOwner) { latLng ->
            println("현재 위치: $latLng")
        }

        binding.routeA.setOnClickListener {
            showSearchDialog("A")
        }

        binding.routeB.setOnClickListener {
            showSearchDialog("B")
        }


        return root
    }

    fun showSearchDialog(type: String) {
        val bindingDialog = DialogSearchBinding.inflate(LayoutInflater.from(context))

        val adapter = PlaceSearchAdapter { place ->
            if (type == "A") binding.routeA.setText(place.title)
            else binding.routeB.setText(place.title)

            // 지도 이동 가능 시 추가
            directionsViewModel.naverMap?.moveCamera(CameraUpdate.scrollTo(place.latLng))
        }

        bindingDialog.recyclerResults.adapter = adapter
        bindingDialog.recyclerResults.layoutManager = LinearLayoutManager(context)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(bindingDialog.root)
            .create()
        dialog.show()

        val handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null

        bindingDialog.editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                runnable?.let { handler.removeCallbacks(it) }
                runnable = Runnable {
                    val query = s.toString()
                    if (query.length >= 2) {
                        directionsViewModel.searchPlaces(query)
                    }
                }
                handler.postDelayed(runnable!!, 300)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // LiveData 관찰
        directionsViewModel.searchResults.observe(viewLifecycleOwner) { results ->
            adapter.submitList(results)
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