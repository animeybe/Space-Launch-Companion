package com.animeybe.spacelaunchcompanion.data.remote.model

import com.google.gson.annotations.SerializedName

data class LaunchDetailDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: LaunchStatusDto,
    @SerializedName("launch_service_provider") val launchServiceProvider: LaunchServiceProviderDto?,
    @SerializedName("mission") val mission: MissionDetailDto?,
    @SerializedName("rocket") val rocket: RocketDetailDto?,
    @SerializedName("pad") val pad: PadDetailDto,
    @SerializedName("window_start") val windowStart: String,
    @SerializedName("window_end") val windowEnd: String,
    @SerializedName("net") val net: String,
    @SerializedName("image") val image: String?,
    @SerializedName("infographic") val infographic: String?,
    @SerializedName("program") val program: List<ProgramDto>?,
    @SerializedName("vidURLs") val videoUrls: List<String>?,
    @SerializedName("holdreason") val holdReason: String?,
    @SerializedName("failreason") val failReason: String?,
    @SerializedName("mission_description") val description: String?
)

data class MissionDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("orbit") val orbit: OrbitDto?,
    @SerializedName("agencies") val agencies: List<AgencyDto>?
)

data class OrbitDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("abbrev") val abbreviation: String?
)


data class RocketDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("configuration") val configuration: RocketConfigurationDetailDto
)

data class RocketConfigurationDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("family") val family: String?,
    @SerializedName("variant") val variant: String?,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("launch_mass") val launchMass: Int?,
    @SerializedName("length") val length: Double?,
    @SerializedName("diameter") val diameter: Double?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("info_url") val infoUrl: String?,
    @SerializedName("wiki_url") val wikiUrl: String?
)

data class PadDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("location") val location: LocationDetailDto,
    @SerializedName("map_url") val mapUrl: String?,
    @SerializedName("total_launch_count") val totalLaunchCount: Int?
)

data class LocationDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("country_code") val countryCode: String,
    @SerializedName("description") val description: String?,
    @SerializedName("map_image") val mapImage: String?,
    @SerializedName("total_launch_count") val totalLaunchCount: Int?,
    @SerializedName("total_landing_count") val totalLandingCount: Int?
)

data class AgencyDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String?,
    @SerializedName("country_code") val countryCode: String?,
    @SerializedName("description") val description: String?
)

data class ProgramDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("agencies") val agencies: List<AgencyDto>?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("wiki_url") val wikiUrl: String?
)

data class LauncherConfigsResponseDto(
    @SerializedName("results") val results: List<LauncherConfigDto>,
    @SerializedName("count") val count: Int
)

data class LauncherConfigDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("family") val family: String?,
    @SerializedName("variant") val variant: String?,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("description") val description: String?
)