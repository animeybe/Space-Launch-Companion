package com.animeybe.spacelaunchcompanion.data.mapper

import com.animeybe.spacelaunchcompanion.data.local.entity.CachedLaunchDetailEntity
import com.animeybe.spacelaunchcompanion.domain.model.*

object CachedLaunchDetailMapper {

    fun domainToCached(detail: LaunchDetail): CachedLaunchDetailEntity {
        return CachedLaunchDetailEntity(
            id = detail.id,
            name = detail.name,
            statusName = detail.status.name,
            statusDescription = detail.status.description,
            launchServiceProviderId = detail.launchServiceProvider.id,
            launchServiceProviderName = detail.launchServiceProvider.name,
            launchServiceProviderType = detail.launchServiceProvider.type,
            launchServiceProviderCountryCode = detail.launchServiceProvider.countryCode,
            missionName = detail.mission?.name,
            missionDescription = detail.mission?.description,
            missionType = detail.mission?.type,
            rocketName = detail.rocket?.configuration?.name,
            rocketFamily = detail.rocket?.configuration?.family,
            padName = detail.pad.name,
            locationName = detail.pad.location.name,
            countryCode = detail.pad.location.countryCode,
            windowStart = detail.windowStart,
            windowEnd = detail.windowEnd,
            net = detail.net,
            image = detail.image,
            infographic = detail.infographic,
            description = detail.description
        )
    }

    fun cachedToDomain(cached: CachedLaunchDetailEntity): LaunchDetail {
        return LaunchDetail(
            id = cached.id,
            name = cached.name,
            status = LaunchStatus(
                name = cached.statusName,
                description = cached.statusDescription
            ),
            launchServiceProvider = LaunchServiceProvider(
                id = cached.launchServiceProviderId,
                name = cached.launchServiceProviderName,
                type = cached.launchServiceProviderType,
                countryCode = cached.launchServiceProviderCountryCode,
                description = null,
                website = null,
                wikiUrl = null
            ),
            mission = cached.missionName?.let {
                MissionDetail(
                    id = 0,
                    name = it,
                    description = cached.missionDescription,
                    type = cached.missionType,
                    orbit = null,
                    agencies = emptyList()
                )
            },
            rocket = cached.rocketName?.let {
                RocketDetail(
                    id = 0,
                    configuration = RocketConfigurationDetail(
                        id = 0,
                        name = it,
                        family = cached.rocketFamily,
                        variant = null,
                        fullName = null,
                        description = null,
                        launchMass = null,
                        length = null,
                        diameter = null,
                        imageUrl = null,
                        infoUrl = null,
                        wikiUrl = null
                    )
                )
            },
            pad = PadDetail(
                id = 0,
                name = cached.padName,
                location = LocationDetail(
                    id = 0,
                    name = cached.locationName,
                    countryCode = cached.countryCode,
                    description = null,
                    mapImage = null,
                    totalLaunchCount = null,
                    totalLandingCount = null
                ),
                mapUrl = null,
                totalLaunchCount = null
            ),
            windowStart = cached.windowStart,
            windowEnd = cached.windowEnd,
            net = cached.net,
            image = cached.image,
            infographic = cached.infographic,
            program = emptyList(),
            videoUrls = emptyList(),
            holdReason = null,
            failReason = null,
            description = cached.description
        )
    }
}