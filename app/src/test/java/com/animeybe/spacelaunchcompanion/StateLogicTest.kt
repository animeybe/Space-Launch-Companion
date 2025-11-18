package com.animeybe.spacelaunchcompanion

import com.animeybe.spacelaunchcompanion.domain.model.*
import com.animeybe.spacelaunchcompanion.presentation.state.LaunchState
import com.animeybe.spacelaunchcompanion.presentation.state.SortType
import org.junit.Test
import org.junit.Assert.*

class StateLogicTest {

    @Test
    fun `launch state success contains correct data`() {
        // Given
        val launches = listOf(
            createLaunch("1", "Launch 1"),
            createLaunch("2", "Launch 2")
        )
        val favorites = setOf("1")

        // When
        val state = LaunchState.Success(launches, favorites)

        // Then
        assertEquals(2, state.launches.size)
        assertEquals(1, state.favorites.size)
        assertTrue(state.favorites.contains("1"))
        assertEquals("Launch 1", state.launches[0].name)
    }

    @Test
    fun `filter favorites from launch list`() {
        // Given
        val launches = listOf(
            createLaunch("1", "Favorite Launch"),
            createLaunch("2", "Regular Launch"),
            createLaunch("3", "Another Favorite")
        )
        val favorites = setOf("1", "3")

        // When - имитация фильтрации избранных
        val favoriteLaunches = launches.filter { favorites.contains(it.id) }

        // Then
        assertEquals(2, favoriteLaunches.size)
        assertEquals("Favorite Launch", favoriteLaunches[0].name)
        assertEquals("Another Favorite", favoriteLaunches[1].name)
    }

    @Test
    fun `sort type display names`() {
        // Тестируем логику отображения типов сортировки
        val sortTypes = SortType.values()

        assertEquals(SortType.DATE_ASC, sortTypes[0])
        assertEquals(SortType.DATE_DESC, sortTypes[1])
        assertEquals(SortType.NAME_ASC, sortTypes[2])
        assertEquals(SortType.NAME_DESC, sortTypes[3])
        assertEquals(SortType.AGENCY, sortTypes[4])
        assertEquals(SortType.COUNTRY, sortTypes[5])
        assertEquals(SortType.ROCKET, sortTypes[6])
    }

    @Test
    fun `launch status color logic`() {
        // Имитация логики цветов статусов
        fun getStatusColor(status: String): String {
            return when (status.uppercase()) {
                "GO" -> "Green"
                "TBD" -> "Yellow"
                "HOLD" -> "Red"
                else -> "Gray"
            }
        }

        assertEquals("Green", getStatusColor("Go"))
        assertEquals("Yellow", getStatusColor("TBD"))
        assertEquals("Red", getStatusColor("Hold"))
        assertEquals("Gray", getStatusColor("Unknown"))
    }

    @Test
    fun `date formatting logic`() {
        // Имитация логики форматирования даты
        fun formatDisplayDate(isoDate: String): String {
            return isoDate
                .replace("T", " ")
                .replace("Z", "")
                .substring(0, 16) // "2024-01-01 12:00"
        }

        val input = "2024-01-01T12:00:00Z"
        val expected = "2024-01-01 12:00"
        assertEquals(expected, formatDisplayDate(input))
    }

    @Test
    fun `mission display logic`() {
        // Имитация логики отображения миссии
        val launchWithMission = createLaunch("1", "Test").copy(
            mission = Mission("Science Mission", "Study climate change", "Science")
        )

        val launchWithoutMission = createLaunch("2", "Test2")

        // Проверяем наличие миссии
        assertNotNull(launchWithMission.mission)
        assertNull(launchWithoutMission.mission)

        // Проверяем данные миссии
        assertEquals("Science Mission", launchWithMission.mission?.name)
        assertEquals("Study climate change", launchWithMission.mission?.description)
        assertEquals("Science", launchWithMission.mission?.type)
    }

    private fun createLaunch(id: String, name: String): Launch {
        return Launch(
            id = id,
            name = name,
            status = LaunchStatus("Go", null),
            launchServiceProvider = "Test Provider",
            mission = null,
            rocket = null,
            pad = Pad("Test Pad", Location("Test Location", "Test Country")),
            net = "2024-01-01T12:00:00Z",
            image = null
        )
    }
}