package org.wentura.locoway.compose.connections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.wentura.locoway.R
import org.wentura.locoway.compose.common.TrainBrandCircle
import org.wentura.locoway.data.model.Connection
import org.wentura.locoway.data.model.TrainBrand
import org.wentura.locoway.ui.LocowayTheme
import org.wentura.locoway.util.travelTime
import org.wentura.locoway.viewmodels.ConnectionsUiState
import org.wentura.locoway.viewmodels.ConnectionsViewModel

@Composable
fun ConnectionsScreen(
    connectionsViewModel: ConnectionsViewModel = hiltViewModel(),
    onUpClick: () -> Unit = {},
    onConnectionClick: (Long) -> Unit,
    goToAuthenticationScreen: () -> Unit,
) {
    val uiState = connectionsViewModel.uiState

    ConnectionsScreen(uiState, onUpClick, onConnectionClick, goToAuthenticationScreen)
}

@Composable
fun ConnectionsScreen(
    uiState: StateFlow<ConnectionsUiState>,
    onUpClick: () -> Unit = {},
    onConnectionClick: (Long) -> Unit = {},
    goToAuthenticationScreen: () -> Unit = {},
) {
    val state by uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { ConnectionsTopAppBar(onUpClick, state.departureStation, state.arrivalStation) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (state.isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }

            LazyColumn {
                if (state.connections.isNotEmpty() || state.isLoading) {
                    items(state.connections.size) { connection ->
                        ConnectionListItem(
                            state.connections[connection],
                            state.isSignedIn,
                            onConnectionClick,
                            goToAuthenticationScreen,
                        )

                        if (connection != state.connections.size - 1) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                        }
                    }
                } else {
                    item {
                        Column(
                            modifier = Modifier.fillParentMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(stringResource(R.string.no_connections))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConnectionListItem(
    connection: Connection,
    isSignedIn: Boolean,
    onConnectionClick: (Long) -> Unit = {},
    goToAuthenticationScreen: () -> Unit = {},
) {
    Box(
        modifier =
            Modifier.clickable {
                if (isSignedIn) {
                    onConnectionClick(connection.trainId)
                } else {
                    goToAuthenticationScreen()
                }
            }
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TrainBrandCircle(connection.trainBrand)

            Spacer(modifier = Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ConnectionTime(connection.departureDateTime)
                ConnectionDate(connection.departureDateTime)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val minutes = travelTime(connection.departureDateTime, connection.arrivalDateTime)

                Text(
                    text = stringResource(R.string.duration_time, minutes),
                    style = MaterialTheme.typography.labelSmall,
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ConnectionTime(connection.arrivalDateTime)
                ConnectionDate(connection.arrivalDateTime)
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.price_from),
                    style = MaterialTheme.typography.labelMedium,
                )

                val numberFormat = NumberFormat.getCurrencyInstance()
                numberFormat.currency = Currency.getInstance("PLN")

                Text(
                    text = numberFormat.format(connection.ticketPrice.toDouble()),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun ConnectionDate(departureDateTime: LocalDateTime) {
    Text(
        text = departureDateTime.format(DateTimeFormatter.ofPattern("dd MMM")),
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
private fun ConnectionTime(localDateTime: LocalDateTime) {
    Text(
        text = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ConnectionsTopAppBar(
    onUpClick: () -> Unit,
    departureStation: String,
    arrivalStation: String,
) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(departureStation)
                Text(arrivalStation)
            }
        },
        navigationIcon = {
            IconButton(onClick = onUpClick) {
                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun ConnectionsPreview() {
    val connections =
        listOf(
            Connection(
                1,
                64340,
                TrainBrand.REG,
                "16.0",
                "Strzelce Opolskie",
                "Gliwice",
                LocalDateTime.parse("2024-06-24T12:00:00"),
                LocalDateTime.parse("2024-06-24T13:00:00"),
            ),
            Connection(
                1,
                64340,
                TrainBrand.IC,
                "10.5",
                "Strzelce Opolskie",
                "Gliwice",
                LocalDateTime.parse("2024-06-24T14:00:00"),
                LocalDateTime.parse("2024-06-24T15:00:00"),
            ),
            Connection(
                1,
                64340,
                TrainBrand.REG,
                "16.0",
                "Strzelce Opolskie",
                "Gliwice",
                LocalDateTime.parse("2024-06-24T21:55:30"),
                LocalDateTime.parse("2024-06-24T22:28:00"),
            ),
        )

    LocowayTheme {
        ConnectionsScreen(
            uiState =
                MutableStateFlow(
                    ConnectionsUiState(
                        isLoading = false,
                        departureStation = "Strzelce Opolskie",
                        arrivalStation = "Gliwice",
                        connections = connections,
                    )
                )
        )
    }
}
