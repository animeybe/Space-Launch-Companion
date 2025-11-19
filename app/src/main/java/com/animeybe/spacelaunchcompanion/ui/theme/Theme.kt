package com.animeybe.spacelaunchcompanion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = background_color_night,
    surface = surface_color_night,
    onBackground = primary_text_color_night,
    onSurface = surface_variant_color_night,
    onSurfaceVariant = surface_variant_color_night,

    primary = primary_text_color_night,
    secondary = secondary_text_color_night,

    tertiary = favoirte_button_color_night
)

private val LightColorScheme = lightColorScheme(
    background = background_color_light,
    surface = surface_color_light,
    onBackground = primary_text_color_light,
    onSurface = surface_variant_color_light,
    onSurfaceVariant = surface_variant_color_light,

    primary = primary_text_color_light,
    secondary = secondary_text_color_light,

    tertiary = favoirte_button_color_light
)

@Composable
fun SpaceLaunchCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}