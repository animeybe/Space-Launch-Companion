package com.animeybe.spacelaunchcompanion.presentation.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.animeybe.spacelaunchcompanion.data.util.NetworkMonitor
import com.animeybe.spacelaunchcompanion.presentation.components.ClearCacheButton
import com.animeybe.spacelaunchcompanion.presentation.components.ClearCacheDialog
import com.animeybe.spacelaunchcompanion.presentation.components.ErrorState
import com.animeybe.spacelaunchcompanion.presentation.components.LaunchItem
import com.animeybe.spacelaunchcompanion.presentation.components.LoadingIndicator
import com.animeybe.spacelaunchcompanion.presentation.components.NetworkStatusIndicator
import com.animeybe.spacelaunchcompanion.presentation.components.SortButton
import com.animeybe.spacelaunchcompanion.presentation.components.SortDialog
import com.animeybe.spacelaunchcompanion.presentation.state.LaunchState
import com.animeybe.spacelaunchcompanion.presentation.viewmodel.LaunchViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun LaunchListScreen(
    onLaunchClick: (String) -> Unit,
    viewModel: LaunchViewModel = koinViewModel(),
    networkMonitor: NetworkMonitor = koinInject()
) {
    val launchState by viewModel.launchState.collectAsState()
    val sortState by viewModel.sortState.collectAsState()
    val isOnline by networkMonitor.isOnline.collectAsState(initial = true)
    var showClearCacheDialog by remember { mutableStateOf(false) }

    if (sortState.isSortDialogVisible) {
        SortDialog(
            currentSort = sortState.currentSort,
            onSortSelected = viewModel::setSortType,
            onDismiss = viewModel::hideSortDialog
        )
    }

    if (showClearCacheDialog) {
        ClearCacheDialog(
            onConfirm = {
                viewModel.clearCache()
                showClearCacheDialog = false
            },
            onDismiss = { showClearCacheDialog = false }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Column {
                ClearCacheButton(
                    onClick = { showClearCacheDialog = true },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SortButton(
                    onClick = viewModel::showSortDialog,
                    sortType = sortState.currentSort
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NetworkStatusIndicator(
                isOnline = isOnline,
                modifier = Modifier.fillMaxWidth()
            )

            MainTitle()
            MainContent(
                launchState = launchState,
                onRetry = viewModel::loadLaunches,
                onToggleFavorite = viewModel::toggleFavorite,
                onLaunchClick = onLaunchClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MainTitle() {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Space Launch Companion",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Следи за запуском ракет удобно!",
            fontSize = 16.sp,
        )
    }
}

@Composable
fun MainContent(
    launchState: LaunchState,
    onRetry: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onLaunchClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (launchState) {
        is LaunchState.Loading -> LoadingIndicator()
        is LaunchState.Success -> LaunchList(
            launches = launchState.launches,
            favorites = launchState.favorites,
            onToggleFavorite = onToggleFavorite,
            onLaunchClick = onLaunchClick
        )
        is LaunchState.Error -> ErrorState(
            message = launchState.message,
            onRetry = onRetry,
            modifier = modifier
        )
    }
}

@Composable
fun LaunchList(
    launches: List<com.animeybe.spacelaunchcompanion.domain.model.Launch>,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onLaunchClick: (String) -> Unit
) {
    if (launches.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Нет данных для отображения")
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            items(launches) { launch ->
                LaunchItem(
                    launch = launch,
                    isFavorite = favorites.contains(launch.id),
                    onToggleFavorite = { onToggleFavorite(launch.id) },
                    onClick = {
                        Log.d("LaunchListScreen", "Navigating to launch details: ${launch.id}")
                        onLaunchClick(launch.id)
                    }
                )
            }
        }
    }
}