package com.animeybe.spacelaunchcompanion.domain.usecase

import com.animeybe.spacelaunchcompanion.domain.repository.LaunchRepository

class CheckIsFavoriteUseCase(
    private val repository: LaunchRepository
) {
    suspend operator fun invoke(launchId: String): Boolean {
        return repository.isFavorite(launchId)
    }
}