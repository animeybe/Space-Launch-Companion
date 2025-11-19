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
    fun `sort launches by different criteria`() {
        // Given
        val launches = listOf(
            createLaunch("1", "Charlie", "SpaceX", "2024-01-03T12:00:00Z"),
            createLaunch("2", "Alpha", "NASA", "2024-01-01T12:00:00Z"),
            createLaunch("3", "Bravo", "Roscosmos", "2024-01-02T12:00:00Z")
        )

        // When & Then - сортировка по имени
        val sortedByName = launches.sortedBy { it.name }
        assertEquals("Alpha", sortedByName[0].name)
        assertEquals("Bravo", sortedByName[1].name)
        assertEquals("Charlie", sortedByName[2].name)

        // When & Then - сортировка по дате
        val sortedByDate = launches.sortedBy { it.net }
        assertEquals("2024-01-01T12:00:00Z", sortedByDate[0].net)
        assertEquals("2024-01-02T12:00:00Z", sortedByDate[1].net)
        assertEquals("2024-01-03T12:00:00Z", sortedByDate[2].net)

        // When & Then - сортировка по провайдеру
        val sortedByProvider = launches.sortedBy { it.launchServiceProvider }
        assertEquals("NASA", sortedByProvider[0].launchServiceProvider)
        assertEquals("Roscosmos", sortedByProvider[1].launchServiceProvider)
        assertEquals("SpaceX", sortedByProvider[2].launchServiceProvider)
    }

    @Test
    fun `launch status color logic`() {
        // Имитация логики цветов статусов (как в UI)
        fun getStatusColor(status: String): String {
            return when (status.uppercase()) {
                "GO" -> "Green"
                "TBD" -> "Yellow"
                "HOLD" -> "Orange"
                "FAILED" -> "Red"
                else -> "Gray"
            }
        }

        assertEquals("Green", getStatusColor("Go"))
        assertEquals("Yellow", getStatusColor("TBD"))
        assertEquals("Orange", getStatusColor("Hold"))
        assertEquals("Red", getStatusColor("Failed"))
        assertEquals("Gray", getStatusColor("Unknown"))
    }

    @Test
    fun `date formatting logic for display`() {
        // Имитация логики форматирования даты для UI
        fun formatDisplayDate(isoDate: String): String {
            return try {
                // Упрощённая версия форматирования
                val datePart = isoDate.substring(0, 10) // "2024-01-01"
                val timePart = isoDate.substring(11, 16) // "12:00"
                "$datePart $timePart"
            } catch (e: Exception) {
                isoDate
            }
        }

        val input = "2024-01-01T12:00:00Z"
        val expected = "2024-01-01 12:00"
        assertEquals(expected, formatDisplayDate(input))
    }

    @Test
    fun `mission and rocket display logic`() {
        // Тестируем логику отображения миссии и ракеты
        val launchWithDetails = createLaunch("1", "Detailed Launch").copy(
            mission = Mission("Science Mission", "Study climate change", "Science"),
            rocket = Rocket(RocketConfiguration("Falcon 9", "Falcon", "Block 5"))
        )

        val launchWithoutDetails = createLaunch("2", "Simple Launch")

        // Проверяем наличие деталей
        assertNotNull(launchWithDetails.mission)
        assertNotNull(launchWithDetails.rocket)
        assertNull(launchWithoutDetails.mission)
        assertNull(launchWithoutDetails.rocket)

        // Проверяем данные
        assertEquals("Science Mission", launchWithDetails.mission?.name)
        assertEquals("Falcon 9", launchWithDetails.rocket?.configuration?.name)
    }

    @Test
    fun `country code to name conversion`() {
        // Имитация конвертации кодов стран в названия
        fun getCountryName(countryCode: String): String {
            return when (countryCode.uppercase()) {
                "USA" -> "США"
                "RUS" -> "Россия"
                "CHN" -> "Китай"
                "EU" -> "Европейский союз"
                "FRA" -> "Франция"
                "JPN" -> "Япония"
                else -> countryCode
            }
        }

        assertEquals("США", getCountryName("USA"))
        assertEquals("Россия", getCountryName("RUS"))
        assertEquals("Китай", getCountryName("CHN"))
        assertEquals("UNK", getCountryName("UNK"))
    }

    private fun createLaunch(id: String, name: String, provider: String = "Test Provider", net: String = "2024-01-01T12:00:00Z"): Launch {
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