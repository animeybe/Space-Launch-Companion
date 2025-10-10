package com.animeybe.spacelaunchcompanion.domain.model

data class Launch(
    val id: String,
    val name: String,
    val status: LaunchStatus,
    val launchServiceProvider: String,
    val mission: Mission?,
    val rocket: Rocket?,
    val pad: Pad, // Стартовая площадка
    val net: String, // Дата и время запуска
    val image: String?
)

data class LaunchStatus(
    val name: String, // "Go", "TBD" и т.д.
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