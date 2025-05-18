package com.example.myce.ui.directions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class PlaceSearchAdapter(
    private val onItemClick: (DirectionsViewModel.Place) -> Unit
) : ListAdapter<DirectionsViewModel.Place, PlaceSearchAdapter.PlaceViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = getItem(position)
        holder.bind(place)
    }

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(place: DirectionsViewModel.Place) {
            (itemView as TextView).text = place.title
            itemView.setOnClickListener { onItemClick(place) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DirectionsViewModel.Place>() {
        override fun areItemsTheSame(oldItem: DirectionsViewModel.Place, newItem: DirectionsViewModel.Place): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: DirectionsViewModel.Place, newItem: DirectionsViewModel.Place): Boolean {
            return oldItem == newItem
        }
    }
}
