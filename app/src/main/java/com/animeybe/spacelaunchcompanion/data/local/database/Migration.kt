package com.animeybe.spacelaunchcompanion.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `cached_launch_details` (
                `id` TEXT NOT NULL, 
                `name` TEXT NOT NULL, 
                `statusName` TEXT NOT NULL, 
                `statusDescription` TEXT, 
                `launchServiceProviderId` INTEGER NOT NULL, 
                `launchServiceProviderName` TEXT NOT NULL, 
                `launchServiceProviderType` TEXT, 
                `launchServiceProviderCountryCode` TEXT, 
                `missionName` TEXT, 
                `missionDescription` TEXT, 
                `missionType` TEXT, 
                `rocketName` TEXT, 
                `rocketFamily` TEXT, 
                `padName` TEXT NOT NULL, 
                `locationName` TEXT NOT NULL, 
                `countryCode` TEXT NOT NULL, 
                `windowStart` TEXT NOT NULL, 
                `windowEnd` TEXT NOT NULL, 
                `net` TEXT NOT NULL, 
                `image` TEXT, 
                `infographic` TEXT, 
                `description` TEXT, 
                `cachedAt` INTEGER NOT NULL, 
                PRIMARY KEY(`id`)
            )
        """)
    }
}