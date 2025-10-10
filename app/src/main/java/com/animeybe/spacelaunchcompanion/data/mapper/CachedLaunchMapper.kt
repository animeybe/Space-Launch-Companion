package com.animeybe.spacelaunchcompanion.data.mapper

import com.animeybe.spacelaunchcompanion.data.local.entity.CachedLaunchEntity
import com.animeybe.spacelaunchcompanion.domain.model.Launch
import com.animeybe.spacelaunchcompanion.domain.model.LaunchStatus
import com.animeybe.spacelaunchcompanion.domain.model.Location
import com.animeybe.spacelaunchcompanion.domain.model.Mission
import com.animeybe.spacelaunchcompanion.domain.model.Pad
import com.animeybe.spacelaunchcompanion.domain.model.Rocket
import com.animeybe.spacelaunchcompanion.domain.model.RocketConfiguration

object CachedLaunchMapper {

    fun domainToCached(launch: Launch): CachedLaunchEntity {
        return CachedLaunchEntity(
            id = launch.id,
            name = launch.name,
            statusName = launch.status.name,
            statusDescription = launch.status.description,
            launchServiceProvider = launch.launchServiceProvider,
            missionName = launch.mission?.name,
            missionDescription = launch.mission?.description,
            missionType = launch.mission?.type,
            rocketName = launch.rocket?.configuration?.name,
            rocketFamily = launch.rocket?.configuration?.family,
            rocketVariant = launch.rocket?.configuration?.variant,
            padName = launch.pad.name,
            locationName = launch.pad.location.name,
            country = launch.pad.location.country,
            net = launch.net,
            image = launch.image
        )
    }

    fun cachedToDomain(cached: CachedLaunchEntity): Launch {
        return Launch(
            id = cached.id,
            name = cached.name,
            status = LaunchStatus(
                name = cached.statusName,
                description = cached.statusDescription
            ),
            launchServiceProvider = cached.launchServiceProvider,
            mission = cached.missionName?.let {
                Mission(
                    name = it,
                    description = cached.missionDescription,
                    type = cached.missionType
                )
            },
            rocket = cached.rocketName?.let {
                Rocket(
                    configuration = RocketConfiguration(
                        name = it,
                        family = cached.rocketFamily,
                        variant = cached.rocketVariant
                    )
                )
            },
            pad = Pad(
                name = cached.padName,
                location = Location(
                    name = cached.locationName,
                    country = cached.country
                )
            ),
            net = cached.net,
            image = cached.image
        )
    }
}