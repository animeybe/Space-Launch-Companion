package com.animeybe.spacelaunchcompanion.domain.usecase

import com.animeybe.spacelaunchcompanion.domain.model.Launch
import com.animeybe.spacelaunchcompanion.domain.repository.LaunchRepository

class GetUpcomingLaunchesUseCase(
    private val repository: LaunchRepository
) {
// Чтобы мы могли вызвать объект как функцию, а не через .invoke()
    suspend operator fun invoke(): List<Launch> {
        return repository.getUpcomingLaunches()
    }
}