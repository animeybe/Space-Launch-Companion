package com.animeybe.spacelaunchcompanion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_launches")
data class FavoriteLaunchEntity(
    @PrimaryKey
    val launchId: String,
    val addedAt: Long = System.currentTimeMillis()
)