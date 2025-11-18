package com.animeybe.spacelaunchcompanion.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
        contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
        tint = if (isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .size(24.dp)
            .clickable { onToggle() }
    )
}