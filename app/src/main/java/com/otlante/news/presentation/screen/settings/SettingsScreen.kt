@file:OptIn(ExperimentalMaterial3Api::class)

package com.otlante.news.presentation.screen.settings

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.otlante.news.R

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            viewModel.processCommand(SettingsCommand.SetNotificationsEnabled(it))
        }
    )
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.settings)) },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable {
                                onBackClick()
                            },
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            )
        }
    ) { innerPadding ->
        val state = viewModel.state.collectAsState()
        when (val currentState = state.value) {
            is SettingsState.Configuration -> {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SettingsCard(
                            title = stringResource(R.string.search_language),
                            subtitle = stringResource(R.string.select_language_for_news_search)
                        ) {
                            SettingsDropdown(
                                items = currentState.languages,
                                selectedItem = currentState.language,
                                onItemSelected = {
                                    viewModel.processCommand(SettingsCommand.SelectLanguage(it))
                                },
                                itemAsString = {
                                    it.toReadableFormat()
                                },
                            )
                        }
                    }
                    item {
                        SettingsCard(
                            title = stringResource(R.string.update_interval),
                            subtitle = stringResource(R.string.how_often_to_update_news)
                        ) {
                            SettingsDropdown(
                                items = currentState.intervals,
                                selectedItem = currentState.interval,
                                onItemSelected = {
                                    viewModel.processCommand(SettingsCommand.SelectInterval(it))
                                },
                                itemAsString = {
                                    it.toReadableFormat()
                                },
                            )
                        }
                    }
                    item {
                        SettingsCard(
                            title = stringResource(R.string.notifications),
                            subtitle = stringResource(R.string.show_notifications_about_articles)
                        ) {
                            Switch(
                                checked = currentState.notificationsEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(
                                            android.Manifest.permission.POST_NOTIFICATIONS
                                        )
                                    } else {
                                        viewModel.processCommand(
                                            SettingsCommand.SetNotificationsEnabled(enabled)
                                        )
                                    }
                                }
                            )
                        }
                    }
                    item {
                        SettingsCard(
                            title = stringResource(R.string.update_only_via_wi_fi),
                            subtitle = stringResource(R.string.save_mobile_data)
                        ) {
                            Switch(
                                checked = currentState.wifiOnly,
                                onCheckedChange = {
                                    viewModel.processCommand(
                                        SettingsCommand.SetWifiOnly(it)
                                    )
                                }
                            )
                        }
                    }
                }
            }
            SettingsState.Initial -> { }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            content()
        }
    }
}

@Composable
private fun<T> SettingsDropdown(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    itemAsString: @Composable (T) -> String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
            value = itemAsString(selectedItem),
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(text = itemAsString(item))
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
