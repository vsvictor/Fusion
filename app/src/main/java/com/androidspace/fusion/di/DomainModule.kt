package com.androidspace.fusion.di

import com.androidspace.fusion.data.local.SettingsSharedPreferences
import com.androidspace.fusion.data.repository.RepositoryImpl
import com.androidspace.fusion.domain.UseCaseScaleBitmap
import com.androidspace.fusion.domain.usecase.*
import org.koin.dsl.module

val domainModule = module {
    single {
        SettingsSharedPreferences(get())
    }
    single {
        UseCaseTokens(RepositoryImpl(get(), get()))
    }
    single {
        RefreshUseCase(RepositoryImpl(get(), get()))
    }
    single {
        UseCaseResizeBitmap(get())
    }
    single {
        UseCaseScaleBitmap()
    }
    single {
        UseCaseFirstStart(RepositoryImpl(get(), get()))
    }
    single {
        UseCaseAgree(RepositoryImpl(get(), get()))
    }
    single {
        UseCaseMapType(RepositoryImpl(get(), get()))
    }
    single {
        UseCaseBasemap(RepositoryImpl(get(), get()))
    }
}
