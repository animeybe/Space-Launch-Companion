package com.animeybe.spacelaunchcompanion

import com.animeybe.spacelaunchcompanion.domain.model.*
import com.animeybe.spacelaunchcompanion.domain.repository.LaunchRepository
import com.animeybe.spacelaunchcompanion.domain.repository.SortType
import com.animeybe.spacelaunchcompanion.domain.usecase.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class UseCaseTest {

    // Простая реализация репозитория для тестирования Use Cases
    private class TestLaunchRepository : LaunchRepository {
        private val testLaunches = listOf(
            createLaunch("1", "Zeta Launch", "SpaceX", "2024-01-03T12:00:00Z"),
            createLaunch("2", "Alpha Launch", "NASA", "2024-01-01T12:00:00Z"),
            createLaunch("3", "Beta Launch", "Roscosmos", "2024-01-02T12:00:00Z")
        )

        private val favorites = mutableSetOf<String>()

        override suspend fun getUpcomingLaunches(): List<Launch> = testLaunches

        override suspend fun cacheLaunches(launches: List<Launch>) {
            // Имитация кэширования
        }

        override suspend fun getCachedLaunches(): List<Launch> = testLaunches

        override suspend fun addToFavorites(launchId: String) {
            favorites.add(launchId)
        }

        override suspend fun removeFromFavorites(launchId: String) {
            favorites.remove(launchId)
        }

        override suspend fun isFavorite(launchId: String): Boolean = favorites.contains(launchId)

        override suspend fun getFavoriteLaunches(): List<Launch> =
            testLaunches.filter { favorites.contains(it.id) }

        override suspend fun getLaunchesSortedBy(sortType: SortType): List<Launch> {
            return when (sortType) {
                SortType.NAME_ASC -> testLaunches.sortedBy { it.name }
                SortType.NAME_DESC -> testLaunches.sortedByDescending { it.name }
                SortType.DATE_ASC -> testLaunches.sortedBy { it.net }
                SortType.DATE_DESC -> testLaunches.sortedByDescending { it.net }
                SortType.AGENCY -> testLaunches.sortedBy { it.launchServiceProvider }
                SortType.COUNTRY -> testLaunches.sortedBy { it.pad.location.country }
                SortType.ROCKET -> testLaunches.sortedBy { it.rocket?.configuration?.name ?: "" }
            }
        }

        override suspend fun getLaunchDetail(launchId: String): LaunchDetail {
            return LaunchDetail(
                id = launchId,
                name = "Test Launch Detail",
                status = LaunchStatus("Go", "All systems go"),
                launchServiceProvider = LaunchServiceProvider(1, "SpaceX", "Commercial", "USA", null, null, null),
                mission = null,
                rocket = null,
                pad = PadDetail(1, "LC-39A", LocationDetail(1, "KSC", "USA", null, null, null, null), null, null),
                windowStart = "2024-01-01T12:00:00Z",
                windowEnd = "2024-01-01T14:00:00Z",
                net = "2024-01-01T12:00:00Z",
                image = null,
                infographic = null,
                program = emptyList(),
                videoUrls = emptyList(),
                holdReason = null,
                failReason = null,
                description = null
            )
        }
    }

    @Test
    fun `GetUpcomingLaunchesUseCase returns launches`() = runBlocking {
        // Given
        val repository = TestLaunchRepository()
        val useCase = GetUpcomingLaunchesUseCase(repository)

        // When
        val result = useCase()

        // Then
        assertEquals(3, result.size)
        assertEquals("Zeta Launch", result[0].name)
    }

    @Test
    fun `AddToFavoritesUseCase adds launch to favorites`() = runBlocking {
        // Given
        val repository = TestLaunchRepository()
        val useCase = AddToFavoritesUseCase(repository)
        val launchId = "1"

        // When
        useCase(launchId)

        // Then
        assertTrue(repository.isFavorite(launchId))
    }

    @Test
    fun `RemoveFromFavoritesUseCase removes launch from favorites`() = runBlocking {
        // Given
        val repository = TestLaunchRepository()
        repository.addToFavorites("1")
        val useCase = RemoveFromFavoritesUseCase(repository)

        // When
        useCase("1")

        // Then
        assertFalse(repository.isFavorite("1"))
    }

    @Test
    fun `CheckIsFavoriteUseCase returns correct status`() = runBlocking {
        // Given
        val repository = TestLaunchRepository()
        repository.addToFavorites("1")
        val useCase = CheckIsFavoriteUseCase(repository)

        // When
        val isFavorite1 = useCase("1")
        val isFavorite2 = useCase("2")

        // Then
        assertTrue(isFavorite1)
        assertFalse(isFavorite2)
    }

    @Test
    fun `GetFavoriteLaunchesUseCase returns only favorites`() = runBlocking {
        // Given
        val repository = TestLaunchRepository()
        repository.addToFavorites("1")
        repository.addToFavorites("3")
        val useCase = GetFavoriteLaunchesUseCase(repository)

        // When
        val favorites = useCase()

        // Then
        assertEquals(2, favorites.size)
        assertTrue(favorites.any { it.id == "1" })
        assertTrue(favorites.any { it.id == "3" })
        assertFalse(favorites.any { it.id == "2" })
    }

    @Test
    fun `repository sorts launches by name ascending`() = runBlocking {
        // Given
        val repository = TestLaunchRepository()

        // When
        val sorted = repository.getLaunchesSortedBy(SortType.NAME_ASC)

        // Then
        assertEquals("Alpha Launch", sorted[0].name)
        assertEquals("Beta Launch", sorted[1].name)
        assertEquals("Zeta Launch", sorted[2].name)
    }

    @Test
    fun `repository sorts launches by date ascending`() = runBlocking {
        // Given
        val repository = TestLaunchRepository()

        // When
        val sorted = repository.getLaunchesSortedBy(SortType.DATE_ASC)

        // Then
        assertEquals("2024-01-01T12:00:00Z", sorted[0].net)
        assertEquals("2024-01-02T12:00:00Z", sorted[1].net)
        assertEquals("2024-01-03T12:00:00Z", sorted[2].net)
    }

    private companion object {
        fun createLaunch(id: String, name: String, provider: String, net: String): Launch {
            return Launch(
                id = id,
                name = name,
                status = LaunchStatus("Go", null),
                launchServiceProvider = provider,
                mission = null,
                rocket = null,
                pad = Pad("Test Pad", Location("Test Location", "Test Country")),
                net = net,
                image = null
            )
        }
    }
}