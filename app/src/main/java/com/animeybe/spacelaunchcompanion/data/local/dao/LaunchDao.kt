package com.animeybe.spacelaunchcompanion.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.animeybe.spacelaunchcompanion.data.local.entity.CachedLaunchEntity
import com.animeybe.spacelaunchcompanion.data.local.entity.FavoriteLaunchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LaunchDao {

    // Cached Launches
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedLaunches(launches: List<CachedLaunchEntity>)

    @Query("SELECT * FROM cached_launches ORDER BY cachedAt DESC LIMIT 50")
    suspend fun getCachedLaunches(): List<CachedLaunchEntity>

    @Query("DELETE FROM cached_launches WHERE cachedAt < :timestamp")
    suspend fun deleteOldCachedLaunches(timestamp: Long)

    // Favorite Launches
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(favorite: FavoriteLaunchEntity)

    @Query("DELETE FROM favorite_launches WHERE launchId = :launchId")
    suspend fun removeFromFavorites(launchId: String)

    @Query("SELECT * FROM favorite_launches")
    suspend fun getAllFavorites(): List<FavoriteLaunchEntity>

    @Query("SELECT * FROM favorite_launches WHERE launchId = :launchId")
    suspend fun isFavorite(launchId: String): FavoriteLaunchEntity?

    @Query("SELECT cached_launches.* FROM cached_launches " +
            "INNER JOIN favorite_launches ON cached_launches.id = favorite_launches.launchId " +
            "ORDER BY favorite_launches.addedAt DESC")
    suspend fun getFavoriteLaunches(): List<CachedLaunchEntity>
}