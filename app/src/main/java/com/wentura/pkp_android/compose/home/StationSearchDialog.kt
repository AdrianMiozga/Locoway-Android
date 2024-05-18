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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.wentura.pkp_android.R
import com.wentura.pkp_android.data.Station

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationSearchDialog(
    stations: List<Station>,
    stationText: String,
    @StringRes dialogTitle: Int,
    onDismissRequest: () -> Unit = {},
    onType: (String) -> Unit = {},
    onStationClick: (Station) -> Unit = {},
) {
    val textFieldValue = remember {
        mutableStateOf(
            TextFieldValue(
                stationText,
                TextRange(stationText.length)
            )
        )
    }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxSize(),
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(topBar = {
            TopAppBar(title = { Text(stringResource(dialogTitle)) },
                navigationIcon = {
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Outlined.Close, contentDescription = null
                        )
                    }
                })
        }) { paddingValues ->
            val focusRequester = remember { FocusRequester() }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                item {
                    OutlinedTextField(
                        value = textFieldValue.value,
                        onValueChange = {
                            textFieldValue.value = it
                            onType(it.text)
                        },
                        label = { Text(stringResource(dialogTitle)) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    )
                }

                items(stations.size) { index ->
                    StationListItem(stations[index], onStationClick = onStationClick)
                }
            }
        }
    }
}

@Composable
fun StationListItem(station: Station, onStationClick: (Station) -> Unit = {}) {
    Column(modifier = Modifier
        .clickable {
            onStationClick(station)
        }
        .padding(16.dp)
        .fillMaxWidth()) {
        Text(station.name)
    }
}

@Preview(showBackground = true)
@Composable
fun StationSearchDialogPreview() {
    StationSearchDialog(
        listOf(
            Station("Opole Główne"),
            Station("Opoczno"),
            Station("Opole Zachodnie"),
            Station("Opole Groszowice"),
            Station("Opole Borki"),
            Station("Opoczno Południe"),
            Station("Opole Grotowice"),
            Station("Opole Wschodnie"),
        ),
        "Opo",
        R.string.departure_station,
    )
}
