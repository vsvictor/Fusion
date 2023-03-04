package com.androidspace.fusion.di

import com.androidspace.fusion.MainActViewModel
import com.androidspace.fusion.common.camera.CameraViewModel
import com.androidspace.fusion.ui.common.camera.*
import com.androidspace.fusion.ui.common.license.PDFFragment
import com.androidspace.fusion.ui.maps.MapFragment
import com.androidspace.fusion.ui.maps.MapViewModel
import com.androidspace.fusion.ui.profile.*
import com.androidspace.fusion.ui.splash.SplashViewModel
import com.infotech.mines.ui.common.license.PDFViewModel
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
        MapFragment()
    }
    viewModel {
        MapViewModel(get())
    }
    fragment {
        ProfileFragment()
    }
    viewModel {
        ProfileViewModel(get(), get())
    }
}

