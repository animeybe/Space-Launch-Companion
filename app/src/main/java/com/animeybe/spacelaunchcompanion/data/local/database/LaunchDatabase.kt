package com.animeybe.spacelaunchcompanion.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.animeybe.spacelaunchcompanion.data.local.dao.LaunchDao
import com.animeybe.spacelaunchcompanion.data.local.entity.CachedLaunchEntity
import com.animeybe.spacelaunchcompanion.data.local.entity.CachedLaunchDetailEntity
import com.animeybe.spacelaunchcompanion.data.local.entity.FavoriteLaunchEntity

@Database(
    entities = [
        CachedLaunchEntity::class,
        FavoriteLaunchEntity::class,
        CachedLaunchDetailEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LaunchDatabase : RoomDatabase() {

    abstract fun launchDao(): LaunchDao

    companion object {
        @Volatile
        private var INSTANCE: LaunchDatabase? = null

        fun getInstance(context: Context): LaunchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LaunchDatabase::class.java,
                    "launch_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}