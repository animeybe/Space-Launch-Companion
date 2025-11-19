package com.animeybe.spacelaunchcompanion.presentation.state

import com.animeybe.spacelaunchcompanion.domain.model.Launch

sealed interface LaunchState {
    object Loading : LaunchState
    data class Success(
        val launches: List<Launch>,
        val favorites: Set<String> = emptySet()
    ) : LaunchState
    data class Error(val message: String) : LaunchState
}

data class SortState(
    val currentSort: SortType = SortType.DATE_ASC,
    val isSortDialogVisible: Boolean = false
)

enum class SortType {
    DATE_ASC, DATE_DESC, NAME_ASC, NAME_DESC, AGENCY, COUNTRY, ROCKET
}