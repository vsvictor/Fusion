package com.androidspace.fusion.ui.profile.data

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.androidspace.fusion.base.BaseViewModel
import com.androidspace.fusion.data.model.MapData
import com.androidspace.fusion.data.model.RegionData
import com.androidspace.fusion.ui.common.FPAdapter
import com.androidspace.fusion.ui.profile.RegionListViewModel
import com.androidspace.fusion.ui.profile.BasemapViewModel
import com.androidspace.fusion.ui.profile.offline.OfflineMapListViewModel

class Binder {
    companion object{
        @JvmStatic
        @BindingAdapter("app:regions")
        fun bindRegions(rv: RecyclerView, list: List<RegionData>){
            val adapter = RegionAdapter(list)
            val vm = ViewModelProvider(rv.context as AppCompatActivity).get(RegionListViewModel::class.java)
            adapter.onReginClick = {
                vm.onRegion(it)
            }
            adapter.onRegionDelete = {
                vm.onRegionDelete(it)
            }
            rv.adapter = adapter
        }
        @JvmStatic
        @BindingAdapter("app:offline")
        fun bindOffline(rv: RecyclerView, list: List<RegionData>){
            val adapter = RegionAdapter(list)
            val vm = ViewModelProvider(rv.context as AppCompatActivity).get(OfflineMapListViewModel::class.java)
            adapter.onReginClick = {
                vm.onRegion(it)
            }
            adapter.onRegionDelete = {
                vm.onRegionDelete(it)
            }
            rv.adapter = adapter
        }
        @JvmStatic
        @BindingAdapter("app:basemaps")
        fun bindBasemaps(rv: RecyclerView, list:List<MapData>){
            val adapter = MapAdapter(list)
            val vm = ViewModelProvider(rv.context as AppCompatActivity).get(BasemapViewModel::class.java)
            adapter.onMapClick = {
                vm.onMap(it)
            }
            rv.adapter = adapter
        }
        @JvmStatic
        @BindingAdapter("app:mapthumb")
        fun bindMapThumb(iv: AppCompatImageView, bitmap: Bitmap){
            iv.setImageBitmap(bitmap)
        }
        @JvmStatic
        @BindingAdapter("app:fragments")
        fun bindOfflineFragments(pager: ViewPager2, list: List<Fragment>){
            val owner = FragmentManager.findFragment<Fragment>(pager)
            val adapter = FPAdapter(owner, list)
            pager.adapter = adapter
        }
    }
}