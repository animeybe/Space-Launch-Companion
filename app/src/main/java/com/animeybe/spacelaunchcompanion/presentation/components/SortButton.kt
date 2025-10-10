package com.animeybe.spacelaunchcompanion.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.animeybe.spacelaunchcompanion.presentation.state.SortType

@Composable
fun SortButton(
    onClick: () -> Unit,
    sortType: SortType, // Добавляем текущий тип сортировки
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(56.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = getSortButtonDescription(sortType),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

private fun getSortButtonDescription(sortType: SortType): String {
    val sortDescription = when (sortType) {
        SortType.DATE_ASC -> "сортировка по дате (старые сначала)"
        SortType.DATE_DESC -> "сортировка по дате (новые сначала)"
        SortType.NAME_ASC -> "сортировка по названию (А-Я)"
        SortType.NAME_DESC -> "сортировка по названию (Я-А)"
        SortType.AGENCY -> "сортировка по агентству"
        SortType.COUNTRY -> "сортировка по стране"
        SortType.ROCKET -> "сортировка по ракете"
    }
    return "Открыть диалог сортировки. Текущая: $sortDescription"
}