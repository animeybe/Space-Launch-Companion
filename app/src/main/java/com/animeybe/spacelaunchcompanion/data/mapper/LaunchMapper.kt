package com.animeybe.spacelaunchcompanion.data.mapper

import com.animeybe.spacelaunchcompanion.data.remote.model.LaunchDto
import com.animeybe.spacelaunchcompanion.domain.model.Launch
import com.animeybe.spacelaunchcompanion.domain.model.LaunchStatus
import com.animeybe.spacelaunchcompanion.domain.model.Location
import com.animeybe.spacelaunchcompanion.domain.model.Mission
import com.animeybe.spacelaunchcompanion.domain.model.Pad
import com.animeybe.spacelaunchcompanion.domain.model.Rocket
import com.animeybe.spacelaunchcompanion.domain.model.RocketConfiguration

object LaunchMapper {
    fun dtoToDomain(dto: LaunchDto): Launch {
        return Launch(
            id = dto.id,
            name = dto.name,
            status = LaunchStatus(
                name = dto.status.name,
                description = dto.status.description
            ),
            launchServiceProvider = dto.launchServiceProvider?.name ?: "Unknown Provider",
            mission = dto.mission?.let { missionDto ->
                Mission(
                    name = missionDto.name,
                    description = missionDto.description,
                    type = missionDto.type
                )
            },
            rocket = dto.rocket?.let { rocketDto ->
                Rocket(
                    configuration = RocketConfiguration(
                        name = rocketDto.configuration.name,
                        family = rocketDto.configuration.family,
                        variant = rocketDto.configuration.variant
                    )
                )
            },
            pad = Pad(
                name = dto.pad.name,
                location = Location(
                    name = dto.pad.location.name,
                    country = dto.pad.location.countryCode
                )
            ),
            net = dto.net,
            image = dto.image
        )
    }
}