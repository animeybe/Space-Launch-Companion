package com.animeybe.spacelaunchcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.animeybe.spacelaunchcompanion.presentation.navigation.AppNavigation
import com.animeybe.spacelaunchcompanion.ui.theme.SpaceLaunchCompanionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpaceLaunchCompanionTheme {
                AppNavigation()
            }
        }
    }
}