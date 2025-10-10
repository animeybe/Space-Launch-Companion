package com.animeybe.spacelaunchcompanion.data.remote.model

import com.google.gson.annotations.SerializedName

data class LaunchesResponseDto(
    @SerializedName("results") val results: List<LaunchDto>,
    @SerializedName("count") val count: Int
)

data class LaunchDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: LaunchStatusDto,
    @SerializedName("launch_service_provider") val launchServiceProvider: LaunchServiceProviderDto?,
    @SerializedName("mission") val mission: MissionDto?,
    @SerializedName("rocket") val rocket: RocketDto?,
    @SerializedName("pad") val pad: PadDto,
    @SerializedName("net") val net: String,
    @SerializedName("image") val image: String?
)

data class LaunchStatusDto(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?
)

data class LaunchServiceProviderDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String?,
    @SerializedName("country_code") val countryCode: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("info_url") val website: String?,
    @SerializedName("wiki_url") val wikiUrl: String?
)

data class MissionDto(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("type") val type: String?
)

data class RocketDto(
    @SerializedName("configuration") val configuration: RocketConfigurationDto
)

data class RocketConfigurationDto(
    @SerializedName("name") val name: String,
    @SerializedName("family") val family: String?,
    @SerializedName("variant") val variant: String?
)

data class PadDto(
    @SerializedName("name") val name: String,
    @SerializedName("location") val location: LocationDto
)

data class LocationDto(
    @SerializedName("name") val name: String,
    @SerializedName("country_code") val countryCode: String
)