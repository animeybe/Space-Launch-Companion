package com.animeybe.spacelaunchcompanion.data.mapper

import com.animeybe.spacelaunchcompanion.data.remote.model.*
import com.animeybe.spacelaunchcompanion.domain.model.*

object LaunchDetailMapper {

    fun dtoToDomain(dto: LaunchDetailDto): LaunchDetail {
        return LaunchDetail(
            id = dto.id ?: "unknown",
            name = dto.name ?: "Unnamed Launch",
            status = LaunchStatus(
                name = dto.status.name ?: "Unknown",
                description = dto.status.description
            ),
            launchServiceProvider = dto.launchServiceProvider?.let { mapLaunchServiceProvider(it) }
                ?: LaunchServiceProvider(0, "Unknown", null, null, null, null, null),
            mission = dto.mission?.let { mapMissionDetail(it) },
            rocket = dto.rocket?.let { mapRocketDetail(it) },
            pad = mapPadDetail(dto.pad),
            windowStart = dto.windowStart ?: "",
            windowEnd = dto.windowEnd ?: "",
            net = dto.net ?: "",
            image = dto.image,
            infographic = dto.infographic,
            program = dto.program?.map { mapProgram(it) },
            videoUrls = dto.videoUrls,
            holdReason = dto.holdReason,
            failReason = dto.failReason,
            description = dto.description
        )
    }

    private fun mapLaunchServiceProvider(dto: LaunchServiceProviderDto): LaunchServiceProvider {
        return LaunchServiceProvider(
            id = dto.id,
            name = dto.name,
            type = dto.type,
            countryCode = dto.countryCode,
            description = dto.description,
            website = dto.website,
            wikiUrl = dto.wikiUrl
        )
    }

    private fun mapMissionDetail(dto: MissionDetailDto): MissionDetail {
        return MissionDetail(
            id = dto.id ?: 0,
            name = dto.name ?: "Unknown Mission",
            description = dto.description,
            type = dto.type,
            orbit = dto.orbit?.let { mapOrbit(it) },
            agencies = dto.agencies?.map { mapAgency(it) }
        )
    }

    private fun mapOrbit(dto: OrbitDto): Orbit {
        return Orbit(
            id = dto.id,
            name = dto.name,
            abbreviation = dto.abbreviation
        )
    }

    private fun mapRocketDetail(dto: RocketDetailDto): RocketDetail {
        return RocketDetail(
            id = dto.id,
            configuration = mapRocketConfigurationDetail(dto.configuration)
        )
    }

    private fun mapRocketConfigurationDetail(dto: RocketConfigurationDetailDto): RocketConfigurationDetail {
        return RocketConfigurationDetail(
            id = dto.id,
            name = dto.name,
            family = dto.family,
            variant = dto.variant,
            fullName = dto.fullName,
            description = dto.description,
            launchMass = dto.launchMass,
            length = dto.length,
            diameter = dto.diameter,
            imageUrl = dto.imageUrl,
            infoUrl = dto.infoUrl,
            wikiUrl = dto.wikiUrl
        )
    }

    private fun mapPadDetail(dto: PadDetailDto): PadDetail {
        return PadDetail(
            id = dto.id,
            name = dto.name,
            location = mapLocationDetail(dto.location),
            mapUrl = dto.mapUrl,
            totalLaunchCount = dto.totalLaunchCount
        )
    }

    private fun mapLocationDetail(dto: LocationDetailDto): LocationDetail {
        return LocationDetail(
            id = dto.id,
            name = dto.name,
            countryCode = dto.countryCode,
            description = dto.description,
            mapImage = dto.mapImage,
            totalLaunchCount = dto.totalLaunchCount,
            totalLandingCount = dto.totalLandingCount
        )
    }

    private fun mapAgency(dto: AgencyDto): Agency {
        return Agency(
            id = dto.id,
            name = dto.name,
            type = dto.type,
            countryCode = dto.countryCode,
            description = dto.description
        )
    }

    private fun mapProgram(dto: ProgramDto): Program {
        return Program(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            agencies = dto.agencies?.map { mapAgency(it) },
            imageUrl = dto.imageUrl,
            wikiUrl = dto.wikiUrl
        )
    }
}