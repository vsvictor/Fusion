package com.androidspace.fusion.di

import com.androidspace.fusion.data.repository.*
import org.koin.dsl.module

val repositoryModule = module {
    single {
        LocalDataSourceImpl(get())
    }
    single<LocalDataSource> {
        LocalDataSourceImpl(get())
    }
    single {
        RemoteDataSourceImpl(get())
    }
    single<RemoteDataSource> {
        RemoteDataSourceImpl(get())
    }
    single {
        RepositoryImpl(get(), get())
    }
}