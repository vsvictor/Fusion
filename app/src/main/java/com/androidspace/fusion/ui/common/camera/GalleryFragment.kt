package com.androidspace.fusion.ui.common.camera

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.androidspace.fusion.R
import com.androidspace.fusion.base.BaseFragment
import com.androidspace.fusion.base.annotations.Layout
import com.androidspace.fusion.data.model.camera.ImageData
import com.androidspace.fusion.databinding.FragmentGalleryBinding
import com.androidspace.fusion.ui.common.OnBottomBarVisible
import com.androidspace.fusion.ui.common.OnProgress
import com.androidspace.fusion.ui.common.camera.data.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@Layout(R.layout.fragment_gallery)
@RuntimePermissions
class GalleryFragment : BaseFragment<FragmentGalleryBinding, GalleryViewModel>(), OnFragmentChange,
    OnLoaderShow, OnCamera, OnBack, OnProgress {
    private var previewFragment: Fragment? = null
    private var camera: CameraFragment? = null
    private var onBottomBarVisible: OnBottomBarVisible? = null
    var isAdd = false
    companion object{
        private var dest: OnBitmapDestinatation? = null
        fun setOnDestinatationListener(listener: OnBitmapDestinatation?){
            this.dest = listener
        }
    }

    //private var onNavigateView: OnNavigateView? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        onActionBarState?.onActionBarView(true)
        requireActivity().setTitle(R.string.gallery)
        onBottomBarVisible?.onBottomBarVisible(false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.model = viewModel
        viewModel.args = arguments
        viewModel.setOnFragmentCloseListener(this)
        viewModel.setOnDestinatationListener(dest)
        viewModel.setOnLoaderShowListener(this)
        viewModel.onCamera = this
        viewModel.onBack = this
        viewModel.onProgress = this
        arguments?.let {
            val back = it.getInt("back", -1)
            if(back > 0){
                viewModel.backID = back
            }
            viewModel.isAdd = it.getBoolean("add", false)
            viewModel.isMultipeSelect = it.getBoolean("multiple", false)
        }
        startLoadWithPermissionCheck()
    }
    @NeedsPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun startLoad(){
        viewModel.startLoad()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBottomBarVisible = context as OnBottomBarVisible
        //onNavigateView = context as OnNavigateView
    }

    override fun onDetach() {
        isAdd = false
        onBottomBarVisible = null
        super.onDetach()
    }
    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
    override fun onFragmentOpen(fragment: Fragment?) {
        fragment?.let {
            binding.llCamera.isVisible = true
            previewFragment = it
            requireActivity().supportFragmentManager.beginTransaction().add(R.id.llCamera, it).commit()
        }
    }
    override fun onFragmentClose(fragment: Fragment?) {
        fragment?.let {
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commit()
        }?: kotlin.run {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }
        binding.llCamera.isVisible = false
    }

    override fun omLoaderShow(isShow: Boolean) {
        onLoader?.show(isShow)
    }

    override fun onShowCamera(show: Boolean) {
/*        camera = CameraFragment()
        binding.llCamera.isVisible = show
        if(show) {
            requireActivity().supportFragmentManager.beginTransaction().add(R.id.llCamera, camera!!).commit()
        }else{
            requireActivity().supportFragmentManager.beginTransaction().remove(camera!!).commit()
        }*/
    }

    fun getSelected(): List<ImageData>{
        val res = viewModel.imageList.images.filter { it.selected }
        return res
    }

    override fun onBack(add: Boolean) {
        isAdd = add
        requireActivity().onBackPressed()
    }

    fun getPreviewFragment(): Fragment?{
        return previewFragment
    }

    override fun onProgerssVisible(visible: Boolean) {
        binding.prBar.isVisible = visible
    }
}