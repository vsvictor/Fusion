package com.androidspace.fusion.ui.common

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FPAdapter(private val owner: Fragment, private val list: List<Fragment>): FragmentStateAdapter(owner) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list.get(position)
    }
}