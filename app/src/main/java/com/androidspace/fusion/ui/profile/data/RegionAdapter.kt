package com.androidspace.fusion.ui.profile.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.androidspace.fusion.R
import com.androidspace.fusion.data.model.RegionData
import com.androidspace.fusion.databinding.ItemRegionBinding

typealias OnRegionClick = (data: RegionData) -> Unit
typealias OnRegionDelete = (data: RegionData) -> Unit

class RegionAdapter(val list: List<RegionData>): RecyclerView.Adapter<RegionAdapter.RegionViHolder>() {
    var onReginClick: OnRegionClick? = null
    var onRegionDelete: OnRegionDelete? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViHolder {
        val inlater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemRegionBinding>(inlater, R.layout.item_region, parent, false)
        return RegionViHolder(binding)
    }

    override fun onBindViewHolder(holder: RegionViHolder, position: Int) {
        holder.bind(list.get(position), onReginClick, onRegionDelete)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class RegionViHolder(val binding: ItemRegionBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: RegionData, cl: OnRegionClick?, del: OnRegionDelete?){
            binding.model = item
            val gray = ContextCompat.getColor(binding.root.context, R.color.gray_text)
            val white = ContextCompat.getColor(binding.root.context, R.color.white)
            binding.tvName.setTextColor(if(item.size == "0 KB") gray else white)
            binding.ivFirst.isVisible = !item.loaded
            binding.ivSecond.isVisible = item.loaded
            binding.ivDelete.isVisible = item.loaded
            binding.tvSize.isVisible = !item.loaded
            binding.root.setOnClickListener {
                cl?.invoke(item)
            }
            binding.ivDelete.setOnClickListener {
                del?.invoke(item)
            }
        }
    }
}