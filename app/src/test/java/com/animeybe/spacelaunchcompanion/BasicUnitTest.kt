package com.animeybe.spacelaunchcompanion

import com.animeybe.spacelaunchcompanion.domain.model.*
import com.animeybe.spacelaunchcompanion.presentation.state.LaunchState
import com.animeybe.spacelaunchcompanion.presentation.state.SortType
import org.junit.Test
import org.junit.Assert.*

class BasicUnitTest {

    @Test
    fun `test domain models creation`() {
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
            image = null
        )

        assertEquals("test-1", launch.id)
        assertEquals("Test Launch", launch.name)
        assertEquals("Go", launch.status.name)
        assertEquals("SpaceX", launch.launchServiceProvider)
        assertEquals("Falcon 9", launch.rocket?.configuration?.name)
        assertEquals("LC-39A", launch.pad.name)
    }

    @Test
    fun `test launch state enums`() {
        // Тестируем состояния
        assertTrue(LaunchState.Loading is LaunchState)

        val successState = LaunchState.Success(emptyList(), emptySet())
        assertTrue(successState is LaunchState)
        assertEquals(0, successState.launches.size)
        assertEquals(0, successState.favorites.size)

        val errorState = LaunchState.Error("Test error")
        assertTrue(errorState is LaunchState)
        assertEquals("Test error", errorState.message)
    }

    @Test
    fun `test sort type enum`() {
        // Тестируем типы сортировки
        assertEquals(7, SortType.values().size)
        assertTrue(SortType.values().contains(SortType.DATE_ASC))
        assertTrue(SortType.values().contains(SortType.NAME_ASC))
        assertTrue(SortType.values().contains(SortType.AGENCY))
    }

    @Test
    fun `test collections operations`() {
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
    fun `test string operations for display`() {
        // Тестируем строковые операции (имитация форматирования)
        val dateString = "2024-01-01T12:00:00Z"

        // Имитация парсинга даты
        val displayDate = dateString.replace("T", " ").replace("Z", "")
        assertEquals("2024-01-01 12:00:00", displayDate)

        // Имитация получения имени ракеты
        val rocketName = "Falcon 9 Block 5"
        val shortName = rocketName.split(" ").first()
        assertEquals("Falcon", shortName)
    }

    private fun createLaunch(id: String, name: String, net: String): Launch {
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