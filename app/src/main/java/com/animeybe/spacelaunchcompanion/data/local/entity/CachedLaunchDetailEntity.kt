package com.animeybe.spacelaunchcompanion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.animeybe.spacelaunchcompanion.data.local.converters.LaunchDetailConverters

@Entity(tableName = "cached_launch_details")
@TypeConverters(LaunchDetailConverters::class)
data class CachedLaunchDetailEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val statusName: String,
    val statusDescription: String?,
    val launchServiceProviderId: Int,
    val launchServiceProviderName: String,
    val launchServiceProviderType: String?,
    val launchServiceProviderCountryCode: String?,
    val missionName: String?,
    val missionDescription: String?,
    val missionType: String?,
    val rocketName: String?,
    val rocketFamily: String?,
    val padName: String,
    val locationName: String,
    val countryCode: String,
    val windowStart: String,
    val windowEnd: String,
    val net: String,
    val image: String?,
    val infographic: String?,
    val description: String?,
    val cachedAt: Long = System.currentTimeMillis()
)