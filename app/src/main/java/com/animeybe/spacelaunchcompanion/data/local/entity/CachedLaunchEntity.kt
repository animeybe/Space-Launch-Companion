package com.animeybe.spacelaunchcompanion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.animeybe.spacelaunchcompanion.data.local.converters.LaunchConverters

@Entity(tableName = "cached_launches")
@TypeConverters(LaunchConverters::class)
data class CachedLaunchEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val statusName: String,
    val statusDescription: String?,
    val launchServiceProvider: String,
    val missionName: String?,
    val missionDescription: String?,
    val missionType: String?,
    val rocketName: String?,
    val rocketFamily: String?,
    val rocketVariant: String?,
    val padName: String,
    val locationName: String,
    val country: String,
    val net: String,
    val image: String?,
    val cachedAt: Long = System.currentTimeMillis()
)