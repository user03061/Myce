package com.example.myce.ui.directions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myce.databinding.ItemPlaceResultBinding
import com.example.myce.model.Place

class PlaceSearchAdapter(
    private val onItemClick: (Place) -> Unit
) : ListAdapter<Place, PlaceSearchAdapter.PlaceViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ItemPlaceResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    // onBindViewHolder 수정: bind() 메서드 사용
    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlaceViewHolder(private val binding: ItemPlaceResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(place: Place) {
            binding.textTitle.text = place.title
            binding.textAddress.text = place.address
            binding.root.setOnClickListener { onItemClick(place) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem == newItem
        }
    }
}
