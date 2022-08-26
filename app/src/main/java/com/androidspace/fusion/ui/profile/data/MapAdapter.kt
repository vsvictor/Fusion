package com.androidspace.fusion.ui.profile.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.androidspace.fusion.R
import com.androidspace.fusion.data.model.MapData
import com.androidspace.fusion.data.model.RegionData
import com.androidspace.fusion.databinding.ItemMapBinding
import com.androidspace.fusion.databinding.ItemRegionBinding

typealias OnMapClick = (data: MapData) -> Unit

class MapAdapter(val list: List<MapData>): RecyclerView.Adapter<MapAdapter.MapViewHolder>() {
    var onMapClick: OnMapClick? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapViewHolder {
        val inlater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemMapBinding>(inlater, R.layout.item_map, parent, false)
        return MapViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MapViewHolder, position: Int) {
        holder.bind(list.get(position), onMapClick)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MapViewHolder(val binding: ItemMapBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: MapData, cl: OnMapClick?){
            binding.model = item
            binding.root.setOnClickListener {
                cl?.invoke(item)
            }
        }
    }
}