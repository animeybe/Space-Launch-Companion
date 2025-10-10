package com.animeybe.spacelaunchcompanion.data.mapper

import com.animeybe.spacelaunchcompanion.data.remote.model.LaunchDto
import com.animeybe.spacelaunchcompanion.domain.model.Launch
import com.animeybe.spacelaunchcompanion.domain.model.LaunchStatus
import com.animeybe.spacelaunchcompanion.domain.model.Location
import com.animeybe.spacelaunchcompanion.domain.model.Mission
import com.animeybe.spacelaunchcompanion.domain.model.Pad
import com.animeybe.spacelaunchcompanion.domain.model.Rocket
import com.animeybe.spacelaunchcompanion.domain.model.RocketConfiguration

// Разделение классов описанных в слое бизнес-логики и DTO
object LaunchMapper {
    fun dtoToDomain(dto: LaunchDto): Launch {
        return Launch(
            id = dto.id,
            name = dto.name,
            status = LaunchStatus(
                name = dto.status.name ?: "Unknown", // Защита от null
                description = dto.status.description // Теперь может быть null
            ),
            launchServiceProvider = dto.launchServiceProvider?.name ?: "Unknown Provider",
            mission = dto.mission?.let { missionDto ->
                Mission(
                    name = missionDto.name ?: "Unknown Mission",
                    description = missionDto.description,
                    type = missionDto.type
                )
            },
            rocket = dto.rocket?.let { rocketDto ->
                Rocket(
                    configuration = RocketConfiguration(
                        name = rocketDto.configuration.name ?: "Unknown Rocket",
                        family = rocketDto.configuration.family,
                        variant = rocketDto.configuration.variant
                    )
                )
            },
            pad = Pad(
                name = dto.pad.name ?: "Unknown Pad",
                location = Location(
                    name = dto.pad.location.name ?: "Unknown Location",
                    country = dto.pad.location.countryCode ?: "Unknown"
                )
            ),
            net = dto.net,
            image = dto.image
        )
    }
}