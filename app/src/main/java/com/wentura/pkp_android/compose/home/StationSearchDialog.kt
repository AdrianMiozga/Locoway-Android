package com.wentura.pkp_android.compose.home

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.wentura.pkp_android.R
import com.wentura.pkp_android.data.Station

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationSearchDialog(
    @StringRes dialogTitle: Int,
    query: String,
    stations: List<Station>,
    recentStations: List<Station>,
    onQueryUpdate: (String) -> Unit = {},
    onQueryClear: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
    onStationClick: (String) -> Unit = {},
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxSize(),
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(topBar = {
            TopAppBar(title = { Text(stringResource(dialogTitle)) }, navigationIcon = {
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Outlined.Close, contentDescription = null
                    )
                }
            })
        }) { paddingValues ->
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                item {
                    OutlinedTextField(
                        value = query,
                        onValueChange = onQueryUpdate,
                        singleLine = true,
                        label = { Text(stringResource(dialogTitle)) },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = onQueryClear) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                    )
                }

                item {
                    val label = if (stations.isNotEmpty()) {
                        R.string.results
                    } else {
                        R.string.recent_searches
                    }

                    Text(
                        stringResource(label),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .padding(bottom = 4.dp)
                    )
                }

                if (stations.isNotEmpty()) {
                    items(stations.size) { index ->
                        StationListItem(stations[index], onStationClick = onStationClick)
                    }
                } else {
                    items(recentStations.size) { index ->
                        StationListItem(recentStations[index], onStationClick = onStationClick)
                    }
                }
            }
        }
    }
}

@Composable
fun StationListItem(station: Station, onStationClick: (String) -> Unit = {}) {
    Column(modifier = Modifier
        .clickable {
            onStationClick(station.name)
        }
        .padding(horizontal = 20.dp, vertical = 16.dp)
        .fillMaxWidth()) {
        Text(station.name)
    }
}

@Preview(showBackground = true)
@Composable
fun StationSearchDialogPreview() {
    StationSearchDialog(
        dialogTitle = R.string.departure_station,
        query = "Opo",
        stations = listOf(
            Station("Opole Główne"),
            Station("Opoczno"),
            Station("Opole Zachodnie"),
            Station("Opole Groszowice"),
            Station("Opole Borki"),
            Station("Opoczno Południe"),
            Station("Opole Grotowice"),
            Station("Opole Wschodnie"),
        ),
        recentStations = emptyList(),
    )
}

@Preview(showBackground = true)
@Composable
fun EmptyStationSearchDialogPreview() {
    StationSearchDialog(
        dialogTitle = R.string.departure_station,
        query = "",
        stations = emptyList(),
        recentStations = listOf(
            Station("Wrocław Główny"),
            Station("Opole Główne"),
            Station("Katowice"),
        ),
    )
}
