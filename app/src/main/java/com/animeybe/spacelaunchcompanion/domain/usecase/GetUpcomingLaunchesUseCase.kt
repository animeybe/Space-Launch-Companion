package com.animeybe.spacelaunchcompanion.domain.usecase

import com.animeybe.spacelaunchcompanion.domain.model.Launch
import com.animeybe.spacelaunchcompanion.domain.repository.LaunchRepository

class GetUpcomingLaunchesUseCase(
    private val repository: LaunchRepository
) {
    suspend operator fun invoke(): List<Launch> {
        return repository.getUpcomingLaunches()
    }
}