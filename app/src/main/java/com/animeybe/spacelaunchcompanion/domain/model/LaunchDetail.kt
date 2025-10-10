package com.animeybe.spacelaunchcompanion.domain.model

data class LaunchDetail(
    val id: String,
    val name: String,
    val status: LaunchStatus,
    val launchServiceProvider: LaunchServiceProvider,
    val mission: MissionDetail?,
    val rocket: RocketDetail?,
    val pad: PadDetail,
    val windowStart: String,
    val windowEnd: String,
    val net: String,
    val image: String?,
    val infographic: String?,
    val program: List<Program>?,
    val videoUrls: List<String>?,
    val holdReason: String?,
    val failReason: String?,
    val description: String?
)

data class LaunchServiceProvider(
    val id: Int,
    val name: String,
    val type: String?,
    val countryCode: String?,
    val description: String?,
    val website: String?,
    val wikiUrl: String?
)

data class MissionDetail(
    val id: Int,
    val name: String,
    val description: String?,
    val type: String?,
    val orbit: Orbit?,
    val agencies: List<Agency>?
)

data class Orbit(
    val id: Int?,
    val name: String?,
    val abbreviation: String?
)

data class RocketDetail(
    val id: Int,
    val configuration: RocketConfigurationDetail
)

data class RocketConfigurationDetail(
    val id: Int,
    val name: String,
    val family: String?,
    val variant: String?,
    val fullName: String?,
    val description: String?,
    val launchMass: Int?,
    val length: Double?,
    val diameter: Double?,
    val imageUrl: String?,
    val infoUrl: String?,
    val wikiUrl: String?
)

data class PadDetail(
    val id: Int,
    val name: String,
    val location: LocationDetail,
    val mapUrl: String?,
    val totalLaunchCount: Int?
)

data class LocationDetail(
    val id: Int,
    val name: String,
    val countryCode: String,
    val description: String?,
    val mapImage: String?,
    val totalLaunchCount: Int?,
    val totalLandingCount: Int?
)

data class Agency(
    val id: Int,
    val name: String,
    val type: String?,
    val countryCode: String?,
    val description: String?
)

data class Program(
    val id: Int,
    val name: String,
    val description: String?,
    val agencies: List<Agency>?,
    val imageUrl: String?,
    val wikiUrl: String?
)