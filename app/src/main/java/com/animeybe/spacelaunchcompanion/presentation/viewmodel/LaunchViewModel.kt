package com.animeybe.spacelaunchcompanion.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animeybe.spacelaunchcompanion.domain.usecase.*
import com.animeybe.spacelaunchcompanion.presentation.state.LaunchState
import com.animeybe.spacelaunchcompanion.presentation.state.SortState
import com.animeybe.spacelaunchcompanion.presentation.state.SortType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LaunchViewModel(
    private val getUpcomingLaunchesUseCase: GetUpcomingLaunchesUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val checkIsFavoriteUseCase: CheckIsFavoriteUseCase,
    private val getFavoriteLaunchesUseCase: GetFavoriteLaunchesUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "LaunchViewModel"
    }

    // Основное состояние
    private val _launchState = MutableStateFlow<LaunchState>(LaunchState.Loading)
    val launchState: StateFlow<LaunchState> = _launchState

    // Состояние сортировки
    private val _sortState = MutableStateFlow(SortState())
    val sortState: StateFlow<SortState> = _sortState.asStateFlow()

    // ID избранных запусков (для быстрого доступа)
    private val _favorites = MutableStateFlow<Set<String>>(emptySet())

    // Кэш загруженных данных для сортировки
    private var cachedLaunches: List<com.animeybe.spacelaunchcompanion.domain.model.Launch> = emptyList()

    init {
        Log.d(TAG, "ViewModel initialized")
        loadLaunches()
        loadFavorites()
    }

    fun loadLaunches() {
        viewModelScope.launch {
            Log.d(TAG, "Loading launches...")
            _launchState.value = LaunchState.Loading
            try {
                val launches = getUpcomingLaunchesUseCase()
                Log.d(TAG, "Successfully loaded ${launches.size} launches")

                // Сохраняем в кэш для сортировки
                cachedLaunches = launches

                // Применяем текущую сортировку
                val sortedLaunches = applySorting(launches, _sortState.value.currentSort)

                // Обновляем состояние с учетом избранного
                _launchState.value = LaunchState.Success(
                    launches = sortedLaunches,
                    favorites = _favorites.value
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error loading launches: ${e.message}", e)
                val userFriendlyMessage = when {
                    e is java.net.UnknownHostException -> "Проверьте подключение к интернету"
                    e is java.net.SocketTimeoutException -> "Сервер не отвечает"
                    e is java.io.IOException -> "Ошибка сети"
                    else -> "Не удалось загрузить данные"
                }
                _launchState.value = LaunchState.Error(userFriendlyMessage)
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                val favoriteLaunches = getFavoriteLaunchesUseCase()
                val favoriteIds = favoriteLaunches.map { it.id }.toSet()
                _favorites.value = favoriteIds
                Log.d(TAG, "Loaded ${favoriteIds.size} favorites")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading favorites: ${e.message}", e)
            }
        }
    }

    // Избранное
    fun toggleFavorite(launchId: String) {
        viewModelScope.launch {
            try {
                val isCurrentlyFavorite = _favorites.value.contains(launchId)

                if (isCurrentlyFavorite) {
                    removeFromFavoritesUseCase(launchId)
                    _favorites.value = _favorites.value - launchId
                    Log.d(TAG, "Removed $launchId from favorites")
                } else {
                    addToFavoritesUseCase(launchId)
                    _favorites.value = _favorites.value + launchId
                    Log.d(TAG, "Added $launchId to favorites")
                }

                // Обновляем UI состояние
                updateSuccessStateWithFavorites()

            } catch (e: Exception) {
                Log.e(TAG, "Error toggling favorite: ${e.message}", e)
            }
        }
    }

    private fun updateSuccessStateWithFavorites() {
        val currentState = _launchState.value
        if (currentState is LaunchState.Success) {
            _launchState.value = currentState.copy(favorites = _favorites.value)
        }
    }

    // Сортировка
    fun showSortDialog() {
        Log.d(TAG, "Showing sort dialog")
        _sortState.value = _sortState.value.copy(isSortDialogVisible = true)
    }

    fun hideSortDialog() {
        Log.d(TAG, "Hiding sort dialog")
        _sortState.value = _sortState.value.copy(isSortDialogVisible = false)
    }

    fun setSortType(sortType: SortType) {
        Log.d(TAG, "Setting sort type: $sortType")
        _sortState.value = _sortState.value.copy(
            currentSort = sortType,
            isSortDialogVisible = false
        )

        // Применяем сортировку к данным
        applyCurrentSorting()
    }

    private fun applyCurrentSorting() {
        val currentState = _launchState.value
        if (currentState is LaunchState.Success) {
            val sortedLaunches = applySorting(cachedLaunches, _sortState.value.currentSort)
            _launchState.value = currentState.copy(launches = sortedLaunches)
            Log.d(TAG, "Applied ${_sortState.value.currentSort} sorting to ${sortedLaunches.size} launches")
        }
    }

    private fun applySorting(
        launches: List<com.animeybe.spacelaunchcompanion.domain.model.Launch>,
        sortType: SortType
    ): List<com.animeybe.spacelaunchcompanion.domain.model.Launch> {
        return when (sortType) {
            SortType.DATE_ASC -> launches.sortedBy { it.net }
            SortType.DATE_DESC -> launches.sortedByDescending { it.net }
            SortType.NAME_ASC -> launches.sortedBy { it.name }
            SortType.NAME_DESC -> launches.sortedByDescending { it.name }
            SortType.AGENCY -> launches.sortedBy { it.launchServiceProvider }
            SortType.COUNTRY -> launches.sortedBy { it.pad.location.country }
            SortType.ROCKET -> launches.sortedBy { it.rocket?.configuration?.name ?: "ZZZ" }
        }
    }

    fun isFavorite(launchId: String): Boolean {
        return _favorites.value.contains(launchId)
    }
}