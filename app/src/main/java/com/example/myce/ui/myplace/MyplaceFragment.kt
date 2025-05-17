package com.example.myce.ui.myplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.myce.R
import com.example.myce.databinding.FragmentMyplaceBinding

class MyplaceFragment : Fragment() {

    private var _binding: FragmentMyplaceBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val myplaceViewModel =
            ViewModelProvider(this).get(MyplaceViewModel::class.java)

        _binding = FragmentMyplaceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textMyplace
        myplaceViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}