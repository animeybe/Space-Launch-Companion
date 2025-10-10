package com.animeybe.spacelaunchcompanion.presentation.state

import com.animeybe.spacelaunchcompanion.domain.model.Launch

sealed interface LaunchState {
    object Loading : LaunchState
    data class Success(
        val launches: List<Launch>,
        val favorites: Set<String> = emptySet() // ID избранных запусков
    ) : LaunchState
    data class Error(val message: String) : LaunchState
}

// Состояние для сортировки
data class SortState(
    val currentSort: SortType = SortType.DATE_ASC,
    val isSortDialogVisible: Boolean = false
)

// Типы сортировки
enum class SortType {
    DATE_ASC, DATE_DESC, NAME_ASC, NAME_DESC, AGENCY, COUNTRY, ROCKET
}