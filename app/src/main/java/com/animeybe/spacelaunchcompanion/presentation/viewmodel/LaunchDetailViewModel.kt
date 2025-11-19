package com.animeybe.spacelaunchcompanion.presentation.viewmodel

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

    private val launchId: String = savedStateHandle.get<String>("launchId") ?: ""

    private val _launchDetailState = MutableStateFlow<LaunchDetailState>(LaunchDetailState.Loading)
    val launchDetailState: StateFlow<LaunchDetailState> = _launchDetailState

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    init {
        if (launchId.isNotEmpty()) {
            loadLaunchDetail()
            checkFavoriteStatus()
        } else {
            _launchDetailState.value = LaunchDetailState.Error("Неверный ID запуска")
        }
    }

    private fun loadLaunchDetail() {
        viewModelScope.launch {
            _launchDetailState.value = LaunchDetailState.Loading
            try {
                val launchDetail = getLaunchDetailUseCase(launchId)
                _launchDetailState.value = LaunchDetailState.Success(launchDetail)

            } catch (e: RateLimitException) {
                val minutes = e.retryAfterSeconds / 60
                val message = "${e.message} Лимит восстановится через $minutes минут."
                _launchDetailState.value = LaunchDetailState.Error(message)

            } catch (e: NotFoundException) {
                _launchDetailState.value = LaunchDetailState.Error(e.message ?: "Запуск не найден")

            } catch (e: ApiException) {
                _launchDetailState.value = LaunchDetailState.Error(e.message ?: "Ошибка API")

            } catch (_: UnknownHostException) {
                _launchDetailState.value = LaunchDetailState.Error("Нет подключения к интернету")

            } catch (e: Exception) {
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
            } catch (e: Exception) { throw e }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            try {
                val currentlyFavorite = _isFavorite.value

                if (currentlyFavorite) {
                    removeFromFavoritesUseCase(launchId)
                    _isFavorite.value = false
                } else {
                    addToFavoritesUseCase(launchId)
                    _isFavorite.value = true
                }
            } catch (e: Exception) { throw e }
        }
    }

    fun retry() {
        if (launchId.isNotEmpty()) {
            loadLaunchDetail()
        }
    }
}