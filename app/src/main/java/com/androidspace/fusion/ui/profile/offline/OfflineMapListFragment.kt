package com.infotech.mines.ui.profile.local

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.data.model.RegionData
import com.androidspace.fusion.databinding.FragmentOfflineMapListBinding
import com.androidspace.fusion.ui.profile.data.OnDownload
import com.androidspace.fusion.ui.profile.offline.OfflineMapListViewModel
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
@Layout(R.layout.fragment_offline_map_list)
class OfflineMapListFragment : BaseFragment<FragmentOfflineMapListBinding, OfflineMapListViewModel>(), OnDownload {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.onDownload = this
        binding.model = viewModel
        viewModel.loadRegions()
    }
    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun loadfile(url: String){
        //viewModel.onDownloadMMPK(url)
        viewModel.onDownloadMMPK(url)
    }
    override fun onDownloadFile(data: RegionData) {
        data.url?.let {
            loadfileWithPermissionCheck(it)
        }
    }

}