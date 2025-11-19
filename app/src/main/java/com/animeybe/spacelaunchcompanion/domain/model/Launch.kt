package com.animeybe.spacelaunchcompanion.domain.model

data class Launch(
    val id: String,
    val name: String,
    val status: LaunchStatus,
    val launchServiceProvider: String,
    val mission: Mission?,
    val rocket: Rocket?,
    val pad: Pad,
    val net: String,
    val image: String?
)

data class LaunchStatus(
    val name: String,
    val description: String?
)

data class Mission(
    val name: String,
    val description: String?,
    val type: String?
)

data class Rocket(
    val configuration: RocketConfiguration
)

data class RocketConfiguration(
    val name: String,
    val family: String?,
    val variant: String?
)

data class Pad(
    val name: String,
    val location: Location
)

data class Location(
    val name: String,
    val country: String
)