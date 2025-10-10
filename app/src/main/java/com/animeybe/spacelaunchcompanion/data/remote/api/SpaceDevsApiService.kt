package com.animeybe.spacelaunchcompanion.data.remote.api

import com.animeybe.spacelaunchcompanion.data.remote.model.LaunchDetailDto
import com.animeybe.spacelaunchcompanion.data.remote.model.LauncherConfigsResponseDto
import com.animeybe.spacelaunchcompanion.data.remote.model.LaunchesResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

// API The Space Devs имеет лимиты на количество запросов:
// Бесплатный план: ~15 запросов в час
// При превышении лимита - блокировка на 15-20 минут
interface SpaceDevsApiService {
    @GET("launch/upcoming/")
    suspend fun getUpcomingLaunches(): LaunchesResponseDto

    @GET("launch/{id}/")
    suspend fun getLaunchDetail(@Path("id") id: String): LaunchDetailDto

    @GET("config/launcher/")
    suspend fun getLauncherConfigs(): LauncherConfigsResponseDto
}