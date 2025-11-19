package com.animeybe.spacelaunchcompanion.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.animeybe.spacelaunchcompanion.presentation.state.SortType

@Composable
fun SortDialog(
    currentSort: SortType,
    onSortSelected: (SortType) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Сортировка запусков",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        },
        text = {
            LazyColumn {
                items(SortType.entries.toTypedArray()) { sortType ->
                    SortOptionItem(
                        sortType = sortType,
                        isSelected = currentSort == sortType,
                        onSelected = { onSortSelected(sortType) },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Готово", color = MaterialTheme.colorScheme.secondary)
            }
        },
    )
}

@Composable
private fun SortOptionItem(
    sortType: SortType,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelected
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = getSortTypeDisplayName(sortType),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Выбрано",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun getSortTypeDisplayName(sortType: SortType): String {
    return when (sortType) {
        SortType.DATE_ASC -> "📅 Дата (По возрастанию)"
        SortType.DATE_DESC -> "📅 Дата (По убыванию)"
        SortType.NAME_ASC -> "🔤 Название (А-Я)"
        SortType.NAME_DESC -> "🔤 Название (Я-А)"
        SortType.AGENCY -> "🏢 Агентство"
        SortType.COUNTRY -> "🌍 Страна"
        SortType.ROCKET -> "🚀 Ракета"
    }
}