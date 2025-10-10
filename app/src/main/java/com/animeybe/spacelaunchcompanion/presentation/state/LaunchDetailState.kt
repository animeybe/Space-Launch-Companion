package com.animeybe.spacelaunchcompanion.presentation.state

import com.animeybe.spacelaunchcompanion.domain.model.LaunchDetail

sealed interface LaunchDetailState {
    object Loading : LaunchDetailState
    data class Success(val launchDetail: LaunchDetail) : LaunchDetailState
    data class Error(val message: String) : LaunchDetailState
}