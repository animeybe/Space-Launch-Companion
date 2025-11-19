package com.animeybe.spacelaunchcompanion.domain.repository

import com.animeybe.spacelaunchcompanion.domain.model.Launch
import com.animeybe.spacelaunchcompanion.domain.model.LaunchDetail

interface LaunchRepository {
    // Основные методы
    suspend fun getUpcomingLaunches(): List<Launch>
    suspend fun cacheLaunches(launches: List<Launch>)
    suspend fun getCachedLaunches(): List<Launch>

    // Избранное
    suspend fun addToFavorites(launchId: String)
    suspend fun removeFromFavorites(launchId: String)
    suspend fun isFavorite(launchId: String): Boolean
    suspend fun getFavoriteLaunches(): List<Launch>

    // Сортировка
    suspend fun getLaunchesSortedBy(sortType: SortType): List<Launch>

    // Детальная информация
    suspend fun getLaunchDetail(launchId: String): LaunchDetail

    // Очистка кэша
    suspend fun clearCache()
}

enum class SortType {
    DATE_ASC, DATE_DESC, NAME_ASC, NAME_DESC, AGENCY, COUNTRY, ROCKET
}