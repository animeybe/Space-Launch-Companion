package com.animeybe.spacelaunchcompanion.data.repository

import android.util.Log
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
import kotlinx.coroutines.withTimeout
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class LaunchRepositoryImpl constructor(
    private val apiService: SpaceDevsApiService,
    private val launchDao: LaunchDao,
    private val networkMonitor: NetworkMonitor
) : LaunchRepository {

    companion object {
        private const val TAG = "LaunchRepository"
        private const val CACHE_DURATION_MS = 7 * 24 * 60 * 60 * 1000L // 7 дней
        private const val NETWORK_TIMEOUT_MS = 10000L // 10 секунд таймаут
    }

    // ==================== ОСНОВНЫЕ ЗАПУСКИ ====================

    override suspend fun getUpcomingLaunches(): List<Launch> {
        Log.d(TAG, "🔄 === START getUpcomingLaunches ===")

        return try {
            // ВРЕМЕННО ОТКЛЮЧАЕМ ПРОВЕРКУ СЕТИ - ВСЕГДА ИСПОЛЬЗУЕМ API
            Log.d(TAG, "🔴 TEMPORARY: Always using API (network check disabled)")

            val response = apiService.getUpcomingLaunches()
            Log.d(TAG, "📡 API Response: count=${response.count}, results=${response.results.size}")

            // Логируем данные
            response.results.take(5).forEachIndexed { index, dto ->
                Log.d(TAG, "🚀 API Launch $index: '${dto.name}'")
                Log.d(TAG, "   📅 Date: ${dto.net}")
                Log.d(TAG, "   🏢 Provider: ${dto.launchServiceProvider?.name ?: "Unknown"}")
            }

            val launches = response.results.map { LaunchMapper.dtoToDomain(it) }

            // ОЧИСТИТЬ СТАРЫЙ КЭШ И СОХРАНИТЬ НОВЫЕ ДАННЫЕ
            val weekAgo = System.currentTimeMillis() - CACHE_DURATION_MS
            launchDao.deleteOldCachedLaunches(weekAgo)
            cacheLaunches(launches)

            Log.d(TAG, "💾 Saved ${launches.size} launches to cache")
            launches

        } catch (e: Exception) {
            Log.e(TAG, "❌ API failed, using cache: ${e.message}")
            val cached = getCachedLaunches()
            Log.d(TAG, "💾 Using ${cached.size} cached launches")
            cached
        } finally {
            Log.d(TAG, "=== END getUpcomingLaunches ===")
        }
    }

    override suspend fun getLaunchDetail(launchId: String): LaunchDetail {
        Log.d(TAG, "🔍 === START getLaunchDetail: $launchId ===")

        return try {
            Log.d(TAG, "🔴 TEMPORARY: Always using API for details")

            val response = apiService.getLaunchDetail(launchId)
            Log.d(TAG, "📡 Launch Detail API Response: ${response.name}")

            // ВРЕМЕННОЕ ЛОГИРОВАНИЕ ДЛЯ ДИАГНОСТИКИ
            Log.d(TAG, "🎬 Video URLs count: ${response.videoUrls?.size ?: 0}")
            response.videoUrls?.forEachIndexed { index, videoUrl ->
                Log.d(TAG, "   Video $index: ${videoUrl.url}")
            }

            val detail = LaunchDetailMapper.dtoToDomain(response)

            // Сохраняем детали в кэш
            cacheLaunchDetail(detail)
            Log.d(TAG, "💾 Saved launch detail to cache: ${detail.name}")

            detail

        } catch (e: Exception) {
            Log.e(TAG, "❌ API failed, trying cache: ${e.message}")
            // Детальный лог ошибки
            Log.e(TAG, "🔍 Error details:", e)

            val cached = getCachedLaunchDetail(launchId)
            if (cached != null) {
                Log.d(TAG, "💾 Using cached detail: ${cached.name}")
                cached
            } else {
                Log.e(TAG, "❌ No cached detail available")
                throw mapToUserFriendlyException(e)
            }
        } finally {
            Log.d(TAG, "=== END getLaunchDetail ===")
        }
    }

    // ==================== КЭШИРОВАНИЕ ====================

    override suspend fun cacheLaunches(launches: List<Launch>) {
        try {
            val cachedEntities = launches.map { CachedLaunchMapper.domainToCached(it) }
            launchDao.insertCachedLaunches(cachedEntities)
            Log.d(TAG, "💾 Successfully cached ${launches.size} launches")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error caching launches: ${e.message}")
        }
    }

    private suspend fun cacheLaunchDetail(detail: LaunchDetail) {
        try {
            Log.d(TAG, "💾 Starting to cache launch detail: ${detail.name}")
            val cachedEntity = CachedLaunchDetailMapper.domainToCached(detail)
            Log.d(TAG, "💾 Mapped to entity, inserting to DB...")

            launchDao.insertCachedLaunchDetail(cachedEntity)
            Log.d(TAG, "✅ Successfully cached launch detail: ${detail.name}")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error caching launch detail: ${e.message}", e)
        }
    }

    // ==================== ЧТЕНИЕ КЭША ====================

    override suspend fun getCachedLaunches(): List<Launch> {
        return try {
            val cached = launchDao.getCachedLaunches()
            val launches = cached.map { CachedLaunchMapper.cachedToDomain(it) }
            Log.d(TAG, "📖 Retrieved ${launches.size} launches from cache")
            launches
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting cached launches: ${e.message}")
            emptyList()
        }
    }

    private suspend fun getCachedLaunchDetail(launchId: String): LaunchDetail? {
        return try {
            val cached = launchDao.getCachedLaunchDetail(launchId)
            if (cached != null) {
                Log.d(TAG, "💾 Found cached detail: ${cached.name}")
                CachedLaunchDetailMapper.cachedToDomain(cached)
            } else {
                Log.d(TAG, "💾 No cached detail found for: $launchId")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error reading cached detail: ${e.message}")
            null
        }
    }

    // ==================== ОЧИСТКА КЭША ====================

    override suspend fun clearCache() {
        try {
            val time = System.currentTimeMillis()
            launchDao.deleteOldCachedLaunches(time) // удалит всё
            launchDao.deleteOldCachedLaunchDetails(time)
            Log.d(TAG, "🧹 Cache cleared completely")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error clearing cache: ${e.message}")
        }
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
            Log.d(TAG, "⭐ Added launch $launchId to favorites")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error adding to favorites: ${e.message}")
            throw e
        }
    }

    override suspend fun removeFromFavorites(launchId: String) {
        try {
            launchDao.removeFromFavorites(launchId)
            Log.d(TAG, "🗑️ Removed launch $launchId from favorites")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error removing from favorites: ${e.message}")
            throw e
        }
    }

    override suspend fun isFavorite(launchId: String): Boolean {
        return try {
            val result = launchDao.isFavorite(launchId) != null
            Log.d(TAG, "❤️ Favorite status for $launchId: $result")
            result
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error checking favorite status: ${e.message}")
            false
        }
    }

    override suspend fun getFavoriteLaunches(): List<Launch> {
        return try {
            val cached = launchDao.getFavoriteLaunches()
            val launches = cached.map { CachedLaunchMapper.cachedToDomain(it) }
            Log.d(TAG, "📚 Retrieved ${launches.size} favorite launches")
            launches
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting favorite launches: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getLaunchesSortedBy(sortType: SortType): List<Launch> {
        val launches = getUpcomingLaunches() // Используем основной метод с fallback

        return when (sortType) {
            SortType.DATE_ASC -> launches.sortedBy { it.net }
            SortType.DATE_DESC -> launches.sortedByDescending { it.net }
            SortType.NAME_ASC -> launches.sortedBy { it.name }
            SortType.NAME_DESC -> launches.sortedByDescending { it.name }
            SortType.AGENCY -> launches.sortedBy { it.launchServiceProvider }
            SortType.COUNTRY -> launches.sortedBy { it.pad.location.country }
            SortType.ROCKET -> launches.sortedBy { it.rocket?.configuration?.name ?: "" }
        }.also {
            Log.d(TAG, "🔀 Sorted ${it.size} launches by $sortType")
        }
    }
}