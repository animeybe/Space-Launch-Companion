package com.animeybe.spacelaunchcompanion.data.repository

import com.animeybe.spacelaunchcompanion.data.local.dao.LaunchDao
import com.animeybe.spacelaunchcompanion.data.mapper.CachedLaunchDetailMapper
import com.animeybe.spacelaunchcompanion.data.mapper.CachedLaunchMapper
import com.animeybe.spacelaunchcompanion.data.mapper.LaunchDetailMapper
import com.animeybe.spacelaunchcompanion.data.mapper.LaunchMapper
import com.animeybe.spacelaunchcompanion.data.remote.api.SpaceDevsApiService
import com.animeybe.spacelaunchcompanion.data.util.NetworkMonitor
import com.animeybe.spacelaunchcompanion.domain.model.Launch
import com.animeybe.spacelaunchcompanion.domain.model.LaunchDetail
import com.animeybe.spacelaunchcompanion.domain.repository.LaunchRepository
import com.animeybe.spacelaunchcompanion.domain.repository.SortType
import kotlinx.coroutines.TimeoutCancellationException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class LaunchRepositoryImpl constructor(
    private val apiService: SpaceDevsApiService,
    private val launchDao: LaunchDao,
    private val networkMonitor: NetworkMonitor
) : LaunchRepository {

    companion object {
        private const val CACHE_DURATION_MS = 7 * 24 * 60 * 60 * 1000L
        private const val NETWORK_TIMEOUT_MS = 10000L
    }

    // ==================== ОСНОВНЫЕ ЗАПУСКИ ====================

    override suspend fun getUpcomingLaunches(): List<Launch> {
        return try {
            val response = apiService.getUpcomingLaunches()

            val launches = response.results.map { LaunchMapper.dtoToDomain(it) }

            val weekAgo = System.currentTimeMillis() - CACHE_DURATION_MS
            launchDao.deleteOldCachedLaunches(weekAgo)
            cacheLaunches(launches)

            launches

        } catch (_: Exception) {
            val cached = getCachedLaunches()
            cached
        }
    }

    override suspend fun getLaunchDetail(launchId: String): LaunchDetail {
        return try {
            val response = apiService.getLaunchDetail(launchId)
            val detail = LaunchDetailMapper.dtoToDomain(response)
            cacheLaunchDetail(detail)
            detail

        } catch (e: Exception) {
            val cached = getCachedLaunchDetail(launchId)
            cached ?: throw mapToUserFriendlyException(e)
        }
    }

    // ==================== КЭШИРОВАНИЕ ====================

    override suspend fun cacheLaunches(launches: List<Launch>) {
        try {
            val cachedEntities = launches.map { CachedLaunchMapper.domainToCached(it) }
            launchDao.insertCachedLaunches(cachedEntities)
        } catch (e: Exception) { throw e }
    }

    private suspend fun cacheLaunchDetail(detail: LaunchDetail) {
        try {
            val cachedEntity = CachedLaunchDetailMapper.domainToCached(detail)
            launchDao.insertCachedLaunchDetail(cachedEntity)

        } catch (e: Exception) { throw e }
    }

    // ==================== ЧТЕНИЕ КЭША ====================

    override suspend fun getCachedLaunches(): List<Launch> {
        return try {
            val cached = launchDao.getCachedLaunches()
            val launches = cached.map { CachedLaunchMapper.cachedToDomain(it) }
            launches
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun getCachedLaunchDetail(launchId: String): LaunchDetail? {
        return try {
            val cached = launchDao.getCachedLaunchDetail(launchId)
            if (cached != null) {
                CachedLaunchDetailMapper.cachedToDomain(cached)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    // ==================== ОЧИСТКА КЭША ====================

    override suspend fun clearCache() {
        try {
            val time = System.currentTimeMillis()
            launchDao.deleteOldCachedLaunches(time)
            launchDao.deleteOldCachedLaunchDetails(time)
        } catch (e: Exception) { throw e }
    }

    // ==================== ОБРАБОТКА ОШИБОК ====================

    private fun mapToUserFriendlyException(e: Exception): Exception {
        return when (e) {
            is TimeoutCancellationException, is SocketTimeoutException ->
                Exception("Сервер не отвежает. Проверьте подключение к интернету")
            is UnknownHostException ->
                Exception("Нет подключения к интернету")
            is SSLHandshakeException ->
                Exception("Ошибка безопасности соединения")
            else ->
                Exception("Не удалось загрузить данные: ${e.message ?: "Неизвестная ошибка"}")
        }
    }

    // ==================== ИЗБРАННОЕ ====================

    override suspend fun addToFavorites(launchId: String) {
        try {
            launchDao.addToFavorites(com.animeybe.spacelaunchcompanion.data.local.entity.FavoriteLaunchEntity(launchId))
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun removeFromFavorites(launchId: String) {
        try {
            launchDao.removeFromFavorites(launchId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun isFavorite(launchId: String): Boolean {
        return try {
            val result = launchDao.isFavorite(launchId) != null
            result
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun getFavoriteLaunches(): List<Launch> {
        return try {
            val cached = launchDao.getFavoriteLaunches()
            val launches = cached.map { CachedLaunchMapper.cachedToDomain(it) }
            launches
        } catch (_: Exception) {
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
        }
    }
}