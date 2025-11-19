package com.animeybe.spacelaunchcompanion.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.animeybe.spacelaunchcompanion.presentation.components.ErrorState
import com.animeybe.spacelaunchcompanion.presentation.state.LaunchDetailState
import com.animeybe.spacelaunchcompanion.presentation.viewmodel.LaunchDetailViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchDetailScreen(
    launchId: String,
    onBackClick: () -> Unit,
    viewModel: LaunchDetailViewModel = koinViewModel(
        parameters = { org.koin.core.parameter.parametersOf(launchId) }
    )
) {
    val launchDetailState by viewModel.launchDetailState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Детали запуска",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                            tint = if (isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        }
    ) { innerPadding ->
        when (val state = launchDetailState) {
            is LaunchDetailState.Loading -> LoadingDetail()
            is LaunchDetailState.Success -> LaunchDetailContent(
                launchDetail = state.launchDetail,
                modifier = Modifier.padding(innerPadding)
            )
            is LaunchDetailState.Error -> ErrorDetail(
                message = state.message,
                onRetry = viewModel::retry,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun LoadingDetail() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorDetail(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ErrorState(
            message = message,
            onRetry = onRetry,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun LaunchDetailContent(
    launchDetail: com.animeybe.spacelaunchcompanion.domain.model.LaunchDetail,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Изображение
        launchDetail.image?.let { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = "Изображение запуска ${launchDetail.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Название и статус
        Text(
            text = launchDetail.name,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = launchDetail.status.name,
            style = MaterialTheme.typography.titleMedium,
            color = when (launchDetail.status.name) {
                "Go" -> MaterialTheme.colorScheme.primary
                "TBD" -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        launchDetail.status.description?.let { description ->
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        launchDetail.description?.let { description ->
            SectionTitle("Описание миссии")
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Окно запуска
        SectionTitle("Окно запуска")
        Text(
            text = "Начало: ${formatDateTime(launchDetail.windowStart)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Окончание: ${formatDateTime(launchDetail.windowEnd)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Планируемое время: ${formatDateTime(launchDetail.net)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Площадка
        SectionTitle("Стартовая площадка")
        Text(
            text = launchDetail.pad.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "${launchDetail.pad.location.name}, ${getCountryName(launchDetail.pad.location.countryCode)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        launchDetail.pad.location.description?.let { description ->
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Оператор
        SectionTitle("Оператор запуска")
        Text(
            text = launchDetail.launchServiceProvider.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        launchDetail.launchServiceProvider.type?.let { type ->
            Text(
                text = "Тип: $type",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        launchDetail.launchServiceProvider.description?.let { description ->
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ракета
        launchDetail.rocket?.let { rocket ->
            SectionTitle("Ракета-носитель")
            Text(
                text = rocket.configuration.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            rocket.configuration.fullName?.let { fullName ->
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            rocket.configuration.family?.let { family ->
                Text(
                    text = "Семейство: $family",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            rocket.configuration.description?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Миссия
        launchDetail.mission?.let { mission ->
            SectionTitle("Миссия")
            Text(
                text = mission.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            mission.type?.let { type ->
                Text(
                    text = "Тип: $type",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            mission.orbit?.let { orbit ->
                orbit.name?.let { orbitName ->
                    Text(
                        text = "Орбита: $orbitName",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            mission.description?.let { description ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

private fun formatDateTime(dateTimeString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy 'в' HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateTimeString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateTimeString
    }
}

private fun getCountryName(countryCode: String): String {
    return when (countryCode) {
        "USA" -> "США"
        "RUS" -> "Россия"
        "CHN" -> "Китай"
        "EU" -> "Европейский союз"
        "JPN" -> "Япония"
        "IND" -> "Индия"
        "ISR" -> "Израиль"
        "NZL" -> "Новая Зеландия"
        "FRA" -> "Франция"
        "GBR" -> "Великобритания"
        "CAN" -> "Канада"
        "BRA" -> "Бразилия"
        "KOR" -> "Южная Корея"
        "UNK" -> "Неизвестно"
        else -> countryCode
    }
}