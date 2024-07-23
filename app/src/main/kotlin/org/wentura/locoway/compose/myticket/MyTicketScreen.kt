package org.wentura.locoway.compose.myticket

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.wentura.locoway.R
import org.wentura.locoway.compose.common.TrainBrandWide
import org.wentura.locoway.data.model.Passenger
import org.wentura.locoway.data.model.Ticket
import org.wentura.locoway.data.model.TrainBrand
import org.wentura.locoway.ui.LocowayTheme
import org.wentura.locoway.util.travelTime
import org.wentura.locoway.viewmodels.MyTicketUiState
import org.wentura.locoway.viewmodels.MyTicketViewModel

@Composable
fun MyTicketScreen(
    onUpClick: () -> Unit,
    myTicketViewModel: MyTicketViewModel = hiltViewModel(),
) {
    MyTicketScreen(onUpClick = onUpClick, uiState = myTicketViewModel.uiState)
}

@Composable
fun MyTicketScreen(
    onUpClick: () -> Unit = {},
    uiState: StateFlow<MyTicketUiState>,
) {
    val state by uiState.collectAsStateWithLifecycle()
    val trainBrand = TrainBrand.valueOf(state.ticket.trainBrand)

    Scaffold(topBar = { MyTicketTopAppBar(onUpClick) }) { innerPadding ->
        Column(
            modifier =
                Modifier.padding(innerPadding).fillMaxWidth().verticalScroll(rememberScrollState()),
        ) {
            Icon(
                painter = painterResource(R.drawable.qr_code),
                contentDescription = null,
                modifier = Modifier.size(256.dp).align(Alignment.CenterHorizontally).padding(10.dp),
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp).fillMaxWidth(),
                ) {
                    TrainBrandWide(
                        trainBrand,
                        state.ticket.trainNumber,
                        Modifier.align(Alignment.CenterHorizontally),
                    )

                    val departureDateTime = LocalDateTime.parse(state.ticket.departureDate)
                    val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm, dd.MM.yyyy")
                    val arrivalDateTime = LocalDateTime.parse(state.ticket.arrivalDate)

                    Text(
                        stringResource(
                            R.string.journey,
                            state.ticket.departureStation,
                            state.ticket.arrivalStation,
                        ),
                    )

                    Text(
                        stringResource(
                            R.string.departure,
                            departureDateTime.format(dateTimeFormatter),
                        ),
                    )

                    Text(
                        stringResource(
                            R.string.arrival,
                            arrivalDateTime.format(dateTimeFormatter),
                        ),
                    )

                    Text(
                        stringResource(
                            R.string.travel_time,
                            travelTime(departureDateTime, arrivalDateTime),
                        ),
                    )

                    if (trainBrand != TrainBrand.REG) {
                        Text(stringResource(R.string.class_with_value, state.ticket.trainClass))
                        Text(stringResource(R.string.seat_number, state.ticket.seat))
                    }
                }
            }

            Text(
                stringResource(R.string.passengers),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(10.dp),
            )

            state.ticket.passengers.forEachIndexed { index, passenger ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(start = 24.dp, end = 16.dp)
                            .padding(vertical = 8.dp),
                ) {
                    Column {
                        Text(passenger.name, style = MaterialTheme.typography.bodyLarge)

                        val discount = stringArrayResource(R.array.discounts)[passenger.discount]

                        if (passenger.hasREGIOCard) {
                            Text(
                                stringResource(R.string.passenger_with_regio_card, discount),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        } else {
                            Text(discount, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                if (index != state.ticket.passengers.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }

            if (trainBrand == TrainBrand.REG &&
                (state.ticket.dog > 0 ||
                    state.ticket.bicycle > 0 ||
                    state.ticket.additionalLuggage > 0)) {
                Text(
                    stringResource(R.string.optionals),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(10.dp),
                )

                if (state.ticket.dog > 0) {
                    Text(
                        text =
                            pluralStringResource(
                                id = R.plurals.dogs, count = state.ticket.dog, state.ticket.dog),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(10.dp),
                    )
                }

                if (state.ticket.bicycle > 0) {
                    Text(
                        text =
                            pluralStringResource(
                                id = R.plurals.bikes,
                                count = state.ticket.bicycle,
                                state.ticket.bicycle),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(10.dp),
                    )
                }

                if (state.ticket.additionalLuggage > 0) {
                    Text(
                        text =
                            pluralStringResource(
                                id = R.plurals.luggage,
                                count = state.ticket.additionalLuggage,
                                state.ticket.additionalLuggage),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(10.dp),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyTicketTopAppBar(onUpClick: () -> Unit) {
    TopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.my_ticket))
            }
        },
        navigationIcon = {
            IconButton(onClick = onUpClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                )
            }
        },
    )
}

@Preview(showBackground = true, locale = "pl")
@Composable
fun MyTicketREGScreenPreview() {
    LocowayTheme {
        MyTicketScreen(
            uiState =
                MutableStateFlow(
                    MyTicketUiState(
                        ticket =
                            Ticket(
                                trainNumber = 64326,
                                trainBrand = "REG",
                                departureStation = "Strzelce Opolskie",
                                departureDate = "2024-04-26T09:19:00",
                                arrivalStation = "Gliwice",
                                arrivalDate = "2024-04-26T10:01:00",
                                bicycle = 2,
                                passengers =
                                    listOf(
                                        Passenger(name = "Adam Majewski"),
                                        Passenger(name = "Roman Zawadzki", discount = 2),
                                    ),
                            ),
                    ),
                ),
        )
    }
}

@Preview(showBackground = true, locale = "pl")
@Composable
fun MyTicketICScreenPreview() {
    LocowayTheme {
        MyTicketScreen(
            uiState =
                MutableStateFlow(
                    MyTicketUiState(
                        ticket =
                            Ticket(
                                trainNumber = 6404,
                                trainBrand = "IC",
                                departureStation = "Strzelce Opolskie",
                                departureDate = "2024-04-26T09:19:00",
                                arrivalStation = "Gliwice",
                                arrivalDate = "2024-04-26T10:01:00",
                                trainClass = 2,
                                seat = 64,
                                passengers =
                                    listOf(
                                        Passenger(name = "Adam Majewski"),
                                    ),
                            ),
                    ),
                ),
        )
    }
}
