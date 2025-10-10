package com.animeybe.spacelaunchcompanion.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.animeybe.spacelaunchcompanion.presentation.screen.LaunchDetailScreen
import com.animeybe.spacelaunchcompanion.presentation.screen.LaunchListScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object LaunchList : Screen("launch_list")
    object LaunchDetail : Screen("launch_detail")
}

fun NavGraphBuilder.appNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.LaunchList.route,
        route = "main"
    ) {
        composable(Screen.LaunchList.route) {
            LaunchListScreen(
                onLaunchClick = { launchId ->
                    navController.navigate("${Screen.LaunchDetail.route}/$launchId")
                }
            )
        }
        composable(
            route = "${Screen.LaunchDetail.route}/{launchId}",
            arguments = listOf(
                navArgument("launchId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val launchId = backStackEntry.arguments?.getString("launchId") ?: ""
            LaunchDetailScreen(
                launchId = launchId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}