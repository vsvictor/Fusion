package com.androidspace.fusion.ui.common.camera.data

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class VPAdapter(val fragment: Fragment, val list:List<Fragment>): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list.get(position)
    }
}