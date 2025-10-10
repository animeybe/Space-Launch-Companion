package com.animeybe.spacelaunchcompanion.data.repository

import android.util.Log
import com.animeybe.spacelaunchcompanion.data.local.dao.LaunchDao
import com.animeybe.spacelaunchcompanion.data.mapper.CachedLaunchMapper
import com.animeybe.spacelaunchcompanion.data.mapper.LaunchDetailMapper
import com.animeybe.spacelaunchcompanion.data.mapper.LaunchMapper
import com.animeybe.spacelaunchcompanion.data.remote.api.SpaceDevsApiService
import com.animeybe.spacelaunchcompanion.data.util.NetworkMonitor
import com.animeybe.spacelaunchcompanion.domain.model.Launch
import com.animeybe.spacelaunchcompanion.domain.model.LaunchDetail
import com.animeybe.spacelaunchcompanion.domain.repository.ServerException
import com.animeybe.spacelaunchcompanion.domain.repository.NotFoundException
import com.animeybe.spacelaunchcompanion.domain.repository.RateLimitException
import com.animeybe.spacelaunchcompanion.domain.repository.LaunchRepository
import com.animeybe.spacelaunchcompanion.domain.repository.SortType
import kotlinx.coroutines.flow.first
import java.net.UnknownHostException

class LaunchRepositoryImpl constructor(
    private val apiService: SpaceDevsApiService,
    private val launchDao: LaunchDao,
    private val networkMonitor: NetworkMonitor
) : LaunchRepository {

    companion object {
        private const val TAG = "LaunchRepository"
        private const val CACHE_DURATION_MS = 7 * 24 * 60 * 60 * 1000L // 7 дней
    }

    override suspend fun getUpcomingLaunches(): List<Launch> {
        Log.d(TAG, "Getting upcoming launches")

        val isOnline = networkMonitor.isOnline.first()

        if (!isOnline) {
            Log.w(TAG, "Device is offline, using cached data")
            return getCachedLaunchesWithFallbackMessage("Устройство offline. Используются кэшированные данные.")
        }

        return try {
            Log.d(TAG, "Trying to fetch from API")
            val response = apiService.getUpcomingLaunches()
            val launches = response.results.map { LaunchMapper.dtoToDomain(it) }

            Log.d(TAG, "API success, caching ${launches.size} launches")
            cacheLaunches(launches)

            launches

        } catch (e: UnknownHostException) {
            Log.w(TAG, "No internet connection, using cached data")
            getCachedLaunches()

        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                429 -> {
                    Log.w(TAG, "Rate limit exceeded, using cached data")
                    getCachedLaunchesWithFallbackMessage("Превышен лимит запросов. Используются кэшированные данные.")
                }
                500, 502, 503 -> {
                    Log.w(TAG, "Server error, using cached data")
                    getCachedLaunchesWithFallbackMessage("Проблемы с сервером. Используются кэшированные данные.")
                }
                else -> {
                    Log.e(TAG, "HTTP error: ${e.code()}, trying cache", e)
                    getCachedLaunches()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "API error: ${e.message}, trying cache", e)
            getCachedLaunches()
        }
    }

    override suspend fun cacheLaunches(launches: List<Launch>) {
        try {
            val cachedEntities = launches.map { CachedLaunchMapper.domainToCached(it) }
            launchDao.insertCachedLaunches(cachedEntities)

            // Удаляем старые записи
            val weekAgo = System.currentTimeMillis() - CACHE_DURATION_MS
            launchDao.deleteOldCachedLaunches(weekAgo)

            Log.d(TAG, "Successfully cached ${launches.size} launches")
        } catch (e: Exception) {
            Log.e(TAG, "Error caching launches: ${e.message}", e)
        }
    }

    override suspend fun getCachedLaunches(): List<Launch> {
        return try {
            val cached = launchDao.getCachedLaunches()
            val launches = cached.map { CachedLaunchMapper.cachedToDomain(it) }
            Log.d(TAG, "Retrieved ${launches.size} launches from cache")
            launches
        } catch (e: Exception) {
            Log.e(TAG, "Error getting cached launches: ${e.message}", e)
            emptyList()
        }
    }

    // Реализация методов избранного
    override suspend fun addToFavorites(launchId: String) {
        try {
            launchDao.addToFavorites(com.animeybe.spacelaunchcompanion.data.local.entity.FavoriteLaunchEntity(launchId))
            Log.d(TAG, "Added launch $launchId to favorites")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding to favorites: ${e.message}", e)
            throw e
        }
    }

    override suspend fun removeFromFavorites(launchId: String) {
        try {
            launchDao.removeFromFavorites(launchId)
            Log.d(TAG, "Removed launch $launchId from favorites")
        } catch (e: Exception) {
            Log.e(TAG, "Error removing from favorites: ${e.message}", e)
            throw e
        }
    }

    override suspend fun isFavorite(launchId: String): Boolean {
        return try {
            val result = launchDao.isFavorite(launchId) != null
            Log.d(TAG, "Checked favorite status for $launchId: $result")
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error checking favorite status: ${e.message}", e)
            false
        }
    }

    override suspend fun getFavoriteLaunches(): List<Launch> {
        return try {
            val cached = launchDao.getFavoriteLaunches()
            val launches = cached.map { CachedLaunchMapper.cachedToDomain(it) }
            Log.d(TAG, "Retrieved ${launches.size} favorite launches")
            launches
        } catch (e: Exception) {
            Log.e(TAG, "Error getting favorite launches: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getLaunchesSortedBy(sortType: SortType): List<Launch> {
        val launches = getUpcomingLaunches()
        return when (sortType) {
            SortType.DATE_ASC -> launches.sortedBy { it.net }
            SortType.DATE_DESC -> launches.sortedByDescending { it.net }
            SortType.NAME_ASC -> launches.sortedBy { it.name }
            SortType.NAME_DESC -> launches.sortedByDescending { it.name }
            SortType.AGENCY -> launches.sortedBy { it.launchServiceProvider }
            SortType.COUNTRY -> launches.sortedBy { it.pad.location.country }
            SortType.ROCKET -> launches.sortedBy { it.rocket?.configuration?.name ?: "" }
        }.also {
            Log.d(TAG, "Sorted ${it.size} launches by $sortType")
        }
    }

    override suspend fun getLaunchDetail(launchId: String): LaunchDetail {
        Log.d(TAG, "Getting launch detail for ID: $launchId")
        return try {
            Log.d(TAG, "Making API request to: launch/$launchId/")
            val response = apiService.getLaunchDetail(launchId)
            Log.d(TAG, "API response received successfully")
            Log.d(TAG, "Launch name: ${response.name}")
            Log.d(TAG, "Launch status: ${response.status.name}")

            val launchDetail = LaunchDetailMapper.dtoToDomain(response)
            Log.d(TAG, "Successfully mapped launch detail: ${launchDetail.name}")
            launchDetail
        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                429 -> {
                    val retryAfter = e.response()?.headers()?.get("Retry-After")?.toIntOrNull() ?: 900
                    val minutes = retryAfter / 60
                    throw RateLimitException("Превышен лимит запросов. Попробуйте через $minutes минут.", retryAfter)
                }
                404 -> throw NotFoundException("Запуск не найден")
                else -> throw ServerException("Ошибка сервера: ${e.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading launch detail for ID $launchId", e)
            Log.e(TAG, "Error type: ${e.javaClass.simpleName}")
            Log.e(TAG, "Error message: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    private suspend fun getCachedLaunchesWithFallbackMessage(message: String): List<Launch> {
        val cached = getCachedLaunches()
        if (cached.isNotEmpty()) {
            Log.i(TAG, "$message Loaded ${cached.size} cached launches")
        }
        return cached
    }
}