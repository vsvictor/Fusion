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
import com.androidspace.fusion.data.model.MapData
import com.androidspace.fusion.data.model.RegionData
import com.androidspace.fusion.ui.common.FPAdapter

class Binder {
    companion object{
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