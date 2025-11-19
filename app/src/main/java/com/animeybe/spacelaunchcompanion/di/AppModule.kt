package com.animeybe.spacelaunchcompanion.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.animeybe.spacelaunchcompanion.data.local.database.LaunchDatabase
import com.animeybe.spacelaunchcompanion.data.local.dao.LaunchDao
import com.animeybe.spacelaunchcompanion.data.remote.api.SpaceDevsApiService
import com.animeybe.spacelaunchcompanion.data.repository.LaunchRepositoryImpl
import com.animeybe.spacelaunchcompanion.data.util.NetworkMonitor
import com.animeybe.spacelaunchcompanion.data.util.NetworkMonitorImpl
import com.animeybe.spacelaunchcompanion.domain.repository.LaunchRepository
import com.animeybe.spacelaunchcompanion.domain.usecase.*
import com.animeybe.spacelaunchcompanion.presentation.viewmodel.LaunchDetailViewModel
import com.animeybe.spacelaunchcompanion.presentation.viewmodel.LaunchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    // ==================== NETWORK ====================

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://ll.thespacedevs.com/2.0.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<SpaceDevsApiService> {
        get<Retrofit>().create(SpaceDevsApiService::class.java)
    }

    // ==================== NETWORK MONITOR ====================

    single<NetworkMonitor> {
        NetworkMonitorImpl(androidContext())
    }

    // ==================== DATABASE ====================

    single<LaunchDatabase> {
        LaunchDatabase.getInstance(androidContext())
    }

    single<LaunchDao> {
        get<LaunchDatabase>().launchDao()
    }

    // ==================== REPOSITORY ====================

    single<LaunchRepository> {
        LaunchRepositoryImpl(
            apiService = get(),
            launchDao = get(),
            networkMonitor = get()
        )
    }

    // ==================== USE CASES ====================

    // Основные Use Cases
    single { GetUpcomingLaunchesUseCase(repository = get()) }
    single { GetLaunchDetailUseCase(repository = get()) }

    // Use Cases для работы с избранным
    single { AddToFavoritesUseCase(repository = get()) }
    single { RemoveFromFavoritesUseCase(repository = get()) }
    single { CheckIsFavoriteUseCase(repository = get()) }
    single { GetFavoriteLaunchesUseCase(repository = get()) }

    // ==================== VIEWMODELS ====================

    // ViewModel для главного экрана
    viewModel {
        LaunchViewModel(
            getUpcomingLaunchesUseCase = get(),
            addToFavoritesUseCase = get(),
            removeFromFavoritesUseCase = get(),
            checkIsFavoriteUseCase = get(),
            getFavoriteLaunchesUseCase = get(),
            repository = get() // Добавляем репозиторий
        )
    }

    // ViewModel для экрана деталей
    viewModel { (savedStateHandle: SavedStateHandle) ->
        LaunchDetailViewModel(
            getLaunchDetailUseCase = get(),
            addToFavoritesUseCase = get(),
            removeFromFavoritesUseCase = get(),
            checkIsFavoriteUseCase = get(),
            savedStateHandle = savedStateHandle
        )
    }
}