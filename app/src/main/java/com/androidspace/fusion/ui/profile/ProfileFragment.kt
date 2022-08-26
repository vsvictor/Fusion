package com.androidspace.fusion.ui.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.databinding.FragmentPhotoBinding
import com.androidspace.fusion.databinding.FragmentProfileBinding
import com.androidspace.fusion.ui.profile.data.OnProfile

@Layout(R.layout.fragment_profile)
class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>(), OnProfile {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.onProfile = this
        binding.model = viewModel
        viewModel.checkLogin()
    }

    override fun onProfileLoad() {
        binding.ivEdit.isVisible = true
        binding.clBottom.isVisible = true
        binding.clDataProfile.isVisible = true
        binding.clEmptyProfile.isVisible = false
        viewModel.loadProfile()
    }

    override fun onProfileEmpty() {
        binding.ivEdit.isVisible = false
        binding.clBottom.isVisible = false
        binding.clDataProfile.isVisible = false
        binding.clEmptyProfile.isVisible = true
    }
}