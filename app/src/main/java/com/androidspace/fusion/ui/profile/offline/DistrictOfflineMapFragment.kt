package com.androidspace.fusion.ui.profile.offline

import android.os.Bundle
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.databinding.FragmentCustomOfflineMapBinding
import com.androidspace.fusion.databinding.FragmentDistrictOfflineMapBinding
import com.androidspace.fusion.databinding.FragmentOfflineMapBinding
import com.androidspace.fusion.databinding.FragmentRegionOfflineMapBinding

@Layout(R.layout.fragment_district_offline_map)
class DistrictOfflineMapFragment : BaseFragment<FragmentDistrictOfflineMapBinding, DistrictOfflineMapViewModel>() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.model = viewModel
    }

}