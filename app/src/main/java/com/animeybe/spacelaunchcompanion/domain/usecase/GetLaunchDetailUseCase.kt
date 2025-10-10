package com.animeybe.spacelaunchcompanion.domain.usecase

import com.animeybe.spacelaunchcompanion.domain.model.LaunchDetail
import com.animeybe.spacelaunchcompanion.domain.repository.LaunchRepository

class GetLaunchDetailUseCase(
    private val repository: LaunchRepository
) {
    suspend operator fun invoke(launchId: String): LaunchDetail {
        return repository.getLaunchDetail(launchId)
    }
}