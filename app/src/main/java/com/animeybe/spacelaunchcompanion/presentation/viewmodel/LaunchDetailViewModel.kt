package com.animeybe.spacelaunchcompanion.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animeybe.spacelaunchcompanion.domain.repository.ApiException
import com.animeybe.spacelaunchcompanion.domain.repository.NotFoundException
import com.animeybe.spacelaunchcompanion.domain.repository.RateLimitException
import com.animeybe.spacelaunchcompanion.domain.usecase.AddToFavoritesUseCase
import com.animeybe.spacelaunchcompanion.domain.usecase.CheckIsFavoriteUseCase
import com.animeybe.spacelaunchcompanion.domain.usecase.GetLaunchDetailUseCase
import com.animeybe.spacelaunchcompanion.domain.usecase.RemoveFromFavoritesUseCase
import com.animeybe.spacelaunchcompanion.presentation.state.LaunchDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class LaunchDetailViewModel(
    private val getLaunchDetailUseCase: GetLaunchDetailUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val checkIsFavoriteUseCase: CheckIsFavoriteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "LaunchDetailViewModel"
    }

    private val launchId: String = savedStateHandle.get<String>("launchId") ?: ""

    private val _launchDetailState = MutableStateFlow<LaunchDetailState>(LaunchDetailState.Loading)
    val launchDetailState: StateFlow<LaunchDetailState> = _launchDetailState

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    init {
        Log.d(TAG, "LaunchDetailViewModel initialized for launchId: $launchId")
        if (launchId.isNotEmpty()) {
            loadLaunchDetail()
            checkFavoriteStatus()
        } else {
            _launchDetailState.value = LaunchDetailState.Error("Неверный ID запуска")
        }
    }

    private fun loadLaunchDetail() {
        viewModelScope.launch {
            Log.d(TAG, "Loading launch detail...")
            _launchDetailState.value = LaunchDetailState.Loading
            try {
                val launchDetail = getLaunchDetailUseCase(launchId)
                Log.d(TAG, "Successfully loaded launch detail: ${launchDetail.name}")
                _launchDetailState.value = LaunchDetailState.Success(launchDetail)

            } catch (e: RateLimitException) {
                val minutes = e.retryAfterSeconds / 60
                val message = "${e.message} Лимит восстановится через $minutes минут."
                Log.e(TAG, "Rate limit: ${e.message}")
                _launchDetailState.value = LaunchDetailState.Error(message)

            } catch (e: NotFoundException) {
                Log.e(TAG, "Launch not found: ${e.message}")
                _launchDetailState.value = LaunchDetailState.Error(e.message ?: "Запуск не найден")

            } catch (e: ApiException) {
                Log.e(TAG, "API error: ${e.message}")
                _launchDetailState.value = LaunchDetailState.Error(e.message ?: "Ошибка API")

            } catch (e: UnknownHostException) {
                Log.e(TAG, "No internet: ${e.message}")
                _launchDetailState.value = LaunchDetailState.Error("Нет подключения к интернету")

            } catch (e: Exception) {
                Log.e(TAG, "Error loading launch detail: ${e.message}", e)
                val userFriendlyMessage = when {
                    e is java.net.SocketTimeoutException -> "Сервер не отвечает"
                    e.message?.contains("404") == true -> "Запуск не найден"
                    else -> "Не удалось загрузить данные о запуске"
                }
                _launchDetailState.value = LaunchDetailState.Error(userFriendlyMessage)
            }
        }
    }

    private fun checkFavoriteStatus() {
        viewModelScope.launch {
            try {
                val isCurrentlyFavorite = checkIsFavoriteUseCase(launchId)
                _isFavorite.value = isCurrentlyFavorite
                Log.d(TAG, "Favorite status for $launchId: $isCurrentlyFavorite")
            } catch (e: Exception) {
                Log.e(TAG, "Error checking favorite status: ${e.message}", e)
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            try {
                val currentlyFavorite = _isFavorite.value

                if (currentlyFavorite) {
                    removeFromFavoritesUseCase(launchId)
                    _isFavorite.value = false
                    Log.d(TAG, "Removed $launchId from favorites")
                } else {
                    addToFavoritesUseCase(launchId)
                    _isFavorite.value = true
                    Log.d(TAG, "Added $launchId to favorites")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling favorite: ${e.message}", e)
            }
        }
    }

    fun retry() {
        if (launchId.isNotEmpty()) {
            loadLaunchDetail()
        }
    }
}