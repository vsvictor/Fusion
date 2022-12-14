package com.androidspace.fusion.di

import com.androidspace.fusion.MainActViewModel
import com.androidspace.fusion.common.camera.CameraViewModel
import com.androidspace.fusion.ui.common.camera.*
import com.androidspace.fusion.ui.common.license.PDFFragment
import com.androidspace.fusion.ui.navigation.NavigationFragment
import com.androidspace.fusion.ui.navigation.NavigationViewModel
import com.androidspace.fusion.ui.profile.*
import com.androidspace.fusion.ui.profile.offline.*
import com.androidspace.fusion.ui.route.RouteFragment
import com.androidspace.fusion.ui.route.RouteViewModel
import com.androidspace.fusion.ui.splash.SplashViewModel
import com.infotech.mines.ui.common.license.PDFViewModel
import com.infotech.mines.ui.profile.local.OfflineMapListFragment
import com.infotech.mines.ui.splash.SplashFragment
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    viewModel {
        MainActViewModel(get())
    }
    fragment {
        CameraFragment()
    }
    viewModel {
        CameraViewModel(get())
    }
    fragment {
        PhotoFragment()
    }
    viewModel {
        PhotoViewModel(get())
    }
    fragment {
        VideoFragment()
    }
    viewModel {
        VideoViewModel(get())
    }
    fragment {
        GalleryFragment()
    }
    viewModel {
        GalleryViewModel(get(), get())
    }
    fragment {
        CropFragment()
    }
    viewModel {
        CropViewModel(get(), get(), get(), get())
    }
    fragment {
        ShowPhotoFragment()
    }
    fragment {
        AlphaPhotoFragment()
    }
    viewModel {
        ShowPhotoViewModel(get())
    }
    fragment {
        ShowVideoFragment()
    }
    fragment {
        AlphaVideoFragment()
    }
    viewModel {
        ShowVideoViewModel(get())
    }
    fragment {
        SplashFragment()
    }
    viewModel {
        SplashViewModel(get(), get(), get())
    }
    fragment {
        PDFFragment()
    }
    viewModel {
        PDFViewModel(get())
    }

    fragment {
        NavigationFragment()
    }
    viewModel {
        NavigationViewModel(get())
    }
    fragment {
        RouteFragment()
    }
    viewModel {
        RouteViewModel(get(), get())
    }
    fragment {
        ProfileFragment()
    }
    viewModel {
        ProfileViewModel(get(), get())
    }
    fragment {
        RegionListFragment()
    }
    viewModel {
        RegionListViewModel(get(), get())
    }
    fragment {
        RegionMapFragment()
    }
    viewModel {
        RegionMapViewModel(get())
    }
    fragment {
        BasemapFragment()
    }
    viewModel {
        BasemapViewModel(get(), get())
    }
    fragment {
        OfflineMapFragment()
    }
    viewModel {
        OfflineMapViewModel(get(), get())
    }
    fragment {
        RegionOfflineMapFragment()
    }
    viewModel {
        RegionOfflineMapViewModel(get(), get())
    }
    fragment {
        DistrictOfflineMapFragment()
    }
    viewModel {
        DistrictOfflineMapViewModel(get())
    }
    fragment {
        CustomOfflineMapFragment()
    }
    viewModel {
        CustomOfflineMapViewModel(get())
    }
    fragment {
        OfflineMapListFragment()
    }
    viewModel {
        OfflineMapListViewModel(get())
    }
}

