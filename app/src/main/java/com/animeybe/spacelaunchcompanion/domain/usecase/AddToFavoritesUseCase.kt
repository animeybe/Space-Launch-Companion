package com.animeybe.spacelaunchcompanion.domain.usecase

import com.animeybe.spacelaunchcompanion.domain.repository.LaunchRepository

class AddToFavoritesUseCase(
    private val repository: LaunchRepository
) {
    suspend operator fun invoke(launchId: String) {
        repository.addToFavorites(launchId)
    }
}