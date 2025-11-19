package com.animeybe.spacelaunchcompanion

import com.animeybe.spacelaunchcompanion.domain.model.*
import com.animeybe.spacelaunchcompanion.presentation.state.LaunchState
import com.animeybe.spacelaunchcompanion.presentation.state.SortType
import org.junit.Test
import org.junit.Assert.*

class BasicUnitTest {

    @Test
    fun testDomainModelsCreation() {
        // Тестируем создание моделей домена
        val launch = Launch(
            id = "test-1",
            name = "Test Launch",
            status = LaunchStatus("Go", "All systems go"),
            launchServiceProvider = "SpaceX",
            mission = Mission("Test Mission", "Test description", "Communication"),
            rocket = Rocket(RocketConfiguration("Falcon 9", "Falcon", "Block 5")),
            pad = Pad("LC-39A", Location("KSC", "USA")),
            net = "2024-01-01T12:00:00Z",
            image = "https://example.com/image.jpg"
        )

        assertEquals("test-1", launch.id)
        assertEquals("Test Launch", launch.name)
        assertEquals("Go", launch.status.name)
        assertEquals("SpaceX", launch.launchServiceProvider)
        assertEquals("Falcon 9", launch.rocket?.configuration?.name)
        assertEquals("LC-39A", launch.pad.name)
        assertEquals("https://example.com/image.jpg", launch.image)
    }

    @Test
    fun testLaunchDetailModelCreation() {
        // Тестируем создание модели деталей запуска
        val launchDetail = LaunchDetail(
            id = "detail-1",
            name = "Test Launch Detail",
            status = LaunchStatus("Go", "All systems go"),
            launchServiceProvider = LaunchServiceProvider(
                id = 1,
                name = "SpaceX",
                type = "Commercial",
                countryCode = "USA",
                description = "Space Exploration Technologies",
                website = "https://spacex.com",
                wikiUrl = null
            ),
            mission = MissionDetail(
                id = 1,
                name = "Test Mission",
                description = "Test mission description",
                type = "Communication",
                orbit = Orbit(1, "LEO", "Low Earth Orbit"),
                agencies = emptyList()
            ),
            rocket = RocketDetail(
                id = 1,
                configuration = RocketConfigurationDetail(
                    id = 1,
                    name = "Falcon 9",
                    family = "Falcon",
                    variant = "Block 5",
                    fullName = "Falcon 9 Block 5",
                    description = "Reusable rocket",
                    launchMass = 549000,
                    length = 70.0,
                    diameter = 3.7,
                    imageUrl = null,
                    infoUrl = null,
                    wikiUrl = null
                )
            ),
            pad = PadDetail(
                id = 1,
                name = "LC-39A",
                location = LocationDetail(
                    id = 1,
                    name = "Kennedy Space Center",
                    countryCode = "USA",
                    description = "Historic launch complex",
                    mapImage = null,
                    totalLaunchCount = 100,
                    totalLandingCount = 50
                ),
                mapUrl = null,
                totalLaunchCount = 100
            ),
            windowStart = "2024-01-01T12:00:00Z",
            windowEnd = "2024-01-01T14:00:00Z",
            net = "2024-01-01T12:00:00Z",
            image = "https://example.com/image.jpg",
            infographic = null,
            program = emptyList(),
            videoUrls = listOf("https://youtube.com/watch?v=test"),
            holdReason = null,
            failReason = null,
            description = "Test launch description"
        )

        assertEquals("detail-1", launchDetail.id)
        assertEquals("SpaceX", launchDetail.launchServiceProvider.name)
        assertEquals("Falcon 9", launchDetail.rocket?.configuration?.name)
        assertEquals("LC-39A", launchDetail.pad.name)
        assertEquals(1, launchDetail.videoUrls?.size ?: 0)
    }

    @Test
    fun testLaunchStateSuccessProperties() {
        // Given
        val launches = listOf(
            createLaunch("1", "Test Launch 1"),
            createLaunch("2", "Test Launch 2")
        )
        val favorites = setOf("1")

        // When
        val successState = LaunchState.Success(launches, favorites)

        // Then
        assertEquals(2, successState.launches.size)
        assertEquals(1, successState.favorites.size)
        assertTrue(successState.favorites.contains("1"))
        assertEquals("Test Launch 1", successState.launches[0].name)
        assertEquals("Test Launch 2", successState.launches[1].name)
    }

    @Test
    fun testLaunchStateErrorProperties() {
        // Given
        val errorMessage = "Network error occurred"

        // When
        val errorState = LaunchState.Error(errorMessage)

        // Then
        assertEquals(errorMessage, errorState.message)
    }

    @Test
    fun testLaunchStateLoadingIsSingleton() {
        // When
        val loading1 = LaunchState.Loading
        val loading2 = LaunchState.Loading

        // Then - объекты должны быть одинаковыми (object declaration)
        assertSame(loading1, loading2)
    }

    @Test
    fun testSortTypeEnum() {
        // Тестируем типы сортировки
        assertEquals(7, SortType.entries.size)
        assertTrue(SortType.entries.contains(SortType.DATE_ASC))
        assertTrue(SortType.entries.contains(SortType.NAME_ASC))
        assertTrue(SortType.entries.contains(SortType.AGENCY))
        assertTrue(SortType.entries.contains(SortType.ROCKET))
    }

    @Test
    fun testCollectionsOperations() {
        // Тестируем операции с коллекциями (имитация сортировки)
        val launches = listOf(
            createLaunch("3", "Charlie", "2024-01-03T12:00:00Z"),
            createLaunch("1", "Alpha", "2024-01-01T12:00:00Z"),
            createLaunch("2", "Bravo", "2024-01-02T12:00:00Z")
        )

        // Сортировка по имени (имитация NAME_ASC)
        val sortedByName = launches.sortedBy { it.name }
        assertEquals("Alpha", sortedByName[0].name)
        assertEquals("Bravo", sortedByName[1].name)
        assertEquals("Charlie", sortedByName[2].name)

        // Сортировка по дате (имитация DATE_ASC)
        val sortedByDate = launches.sortedBy { it.net }
        assertEquals("2024-01-01T12:00:00Z", sortedByDate[0].net)
        assertEquals("2024-01-02T12:00:00Z", sortedByDate[1].net)
        assertEquals("2024-01-03T12:00:00Z", sortedByDate[2].net)
    }

    @Test
    fun testImageUrlHandling() {
        // Тестируем обработку URL изображений
        val launchWithImage = createLaunch("1", "With Image").copy(
            image = "https://example.com/image.jpg"
        )

        val launchWithoutImage = createLaunch("2", "Without Image").copy(
            image = null
        )

        assertTrue(launchWithImage.image?.isNotEmpty() == true)
        assertTrue(launchWithoutImage.image.isNullOrEmpty())
    }

    private fun createLaunch(id: String, name: String, net: String = "2024-01-01T12:00:00Z"): Launch {
        return Launch(
            id = id,
            name = name,
            status = LaunchStatus("Go", null),
            launchServiceProvider = "Test Provider",
            mission = null,
            rocket = null,
            pad = Pad("Test Pad", Location("Test Location", "Test Country")),
            net = net,
            image = null
        )
    }
}