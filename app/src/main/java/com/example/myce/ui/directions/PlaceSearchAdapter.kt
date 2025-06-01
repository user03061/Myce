package com.example.myce.ui.directions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myce.databinding.ItemPlaceResultBinding
import com.example.myce.model.MyPlace

class PlaceSearchAdapter(
    private val onItemClick: (MyPlace) -> Unit
) : ListAdapter<MyPlace, PlaceSearchAdapter.PlaceViewHolder>(DiffCallback()) {

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

        fun bind(place: MyPlace) {
            binding.textTitle.text = place.title
            binding.textAddress.text = place.latLng.toString()
            binding.root.setOnClickListener { onItemClick(place) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MyPlace>() {
        override fun areItemsTheSame(oldItem: MyPlace, newItem: MyPlace): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: MyPlace, newItem: MyPlace): Boolean {
            return oldItem == newItem
        }
    }
}
