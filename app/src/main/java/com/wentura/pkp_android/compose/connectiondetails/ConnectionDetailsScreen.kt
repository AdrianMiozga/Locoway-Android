package com.wentura.pkp_android.compose.connectiondetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wentura.pkp_android.R
import com.wentura.pkp_android.compose.common.AddPassengerDialog
import com.wentura.pkp_android.data.Connection
import com.wentura.pkp_android.data.Passenger
import com.wentura.pkp_android.data.TrainBrand
import com.wentura.pkp_android.ui.PKPAndroidTheme
import com.wentura.pkp_android.util.travelTime
import com.wentura.pkp_android.viewmodels.ConnectionDetailsUiState
import com.wentura.pkp_android.viewmodels.ConnectionDetailsViewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ConnectionsDetailsScreen(
    connectionDetailsViewModel: ConnectionDetailsViewModel = hiltViewModel(),
    onUpClick: () -> Unit = {},
    onBuyButtonClick: () -> Unit = {},
) {
    ConnectionsDetailsScreen(
        uiState = connectionDetailsViewModel.uiState,
        onUpClick = onUpClick,
        onBuyTicket = connectionDetailsViewModel::onBuyTicket,
        onBuyButtonClick = onBuyButtonClick,
        showAddPassengerDialog = connectionDetailsViewModel::showAddPassengerDialog,
        onAddPassengerDialogDismissRequest =
            connectionDetailsViewModel::onAddPassengerDismissRequest,
        onAddPassenger = connectionDetailsViewModel::addPassenger,
        updateName = connectionDetailsViewModel::updateName,
        changeDiscount = connectionDetailsViewModel::changeDiscount,
        toggleREGIOCard = connectionDetailsViewModel::toggleREGIOCard,
        onCheckedChange = connectionDetailsViewModel::onCheckedChange,
        selectDogs = connectionDetailsViewModel::selectDogs,
        selectBikes = connectionDetailsViewModel::selectBikes,
        selectLuggage = connectionDetailsViewModel::selectLuggage,
        selectClass = connectionDetailsViewModel::selectClass,
        getPassengersAmount = connectionDetailsViewModel::getSelectedPassengerAmount,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionsDetailsScreen(
    uiState: StateFlow<ConnectionDetailsUiState>,
    onUpClick: () -> Unit = {},
    onBuyTicket: () -> Unit = {},
    onBuyButtonClick: () -> Unit = {},
    showAddPassengerDialog: () -> Unit = {},
    onAddPassengerDialogDismissRequest: () -> Unit = {},
    onAddPassenger: () -> Unit = {},
    updateName: (String) -> Unit = {},
    changeDiscount: (Int) -> Unit = {},
    toggleREGIOCard: () -> Unit = {},
    onCheckedChange: (Int, Boolean) -> Unit = { _, _ -> },
    selectDogs: (Int) -> Unit = {},
    selectBikes: (Int) -> Unit = {},
    selectLuggage: (Int) -> Unit = {},
    selectClass: (Int) -> Unit = {},
    getPassengersAmount: () -> Int = { 0 },
) {
    val state by uiState.collectAsStateWithLifecycle()

    if (state.openAddPassengerDialog) {
        AddPassengerDialog(
            name = state.currentPassenger.name,
            discount = state.currentPassenger.discount,
            hasREGIOCard = state.currentPassenger.hasREGIOCard,
            onDismissRequest = onAddPassengerDialogDismissRequest,
            onSaveClick = onAddPassenger,
            updateName = updateName,
            changeDiscount = changeDiscount,
            toggleREGIOCard = toggleREGIOCard,
        )
    }

    Scaffold(
        topBar = { ConnectionsDetailsTopAppBar(onUpClick = onUpClick) },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState())) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            ) {
                Column(
                    modifier =
                        Modifier.padding(horizontal = 10.dp, vertical = 8.dp).fillMaxSize()) {
                        Box(
                            modifier =
                                Modifier.clip(CircleShape)
                                    .background(state.connection.trainBrand.displayColor)
                                    .align(Alignment.CenterHorizontally)) {
                                Text(
                                    "${state.connection.trainBrand.displayShortName} ${state.connection.trainNumber}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(4.dp),
                                    color = Color.White,
                                )
                            }

                        Text(
                            stringResource(
                                R.string.journey,
                                state.connection.departureStation,
                                state.connection.arrivalStation),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )

                        val departureDateTime = state.connection.departureDateTime
                        val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm, dd.MM.yyyy")
                        val arrivalDateTime = state.connection.arrivalDateTime

                        Text(
                            stringResource(
                                R.string.departure, departureDateTime.format(dateTimeFormatter)),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )

                        Text(
                            stringResource(
                                R.string.arrival, arrivalDateTime.format(dateTimeFormatter)),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )

                        Text(
                            stringResource(
                                R.string.travel_time,
                                travelTime(departureDateTime, arrivalDateTime)),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
            }

            Text(
                stringResource(R.string.select_passengers),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(10.dp),
            )

            Column {
                state.passengers.forEachIndexed { index, passenger ->
                    PassengerListItem(
                        index, passenger, state.checkedPassengers[index], onCheckedChange)
                }
            }

            IconButton(
                onClick = showAddPassengerDialog,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
            }

            Text(
                stringResource(R.string.select_additional_options),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(10.dp),
            )

            if (state.connection.trainBrand == TrainBrand.REG) {
                var dogExpanded by remember { mutableStateOf(false) }
                val initialDog = stringResource(R.string.no)
                var dogText by remember { mutableStateOf(initialDog) }

                ExposedDropdownMenuBox(
                    expanded = dogExpanded,
                    onExpandedChange = { dogExpanded = !dogExpanded },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    OutlinedTextField(
                        value = dogText,
                        readOnly = true,
                        enabled = false,
                        label = { Text(stringResource(R.string.travels_with_dog)) },
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dogExpanded)
                        },
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor =
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        modifier = Modifier.fillMaxWidth().menuAnchor())

                    ExposedDropdownMenu(
                        expanded = dogExpanded,
                        onDismissRequest = { dogExpanded = false },
                    ) {
                        repeat(getPassengersAmount() + 1) { iteration ->
                            val option =
                                if (iteration == 0) stringResource(R.string.no)
                                else pluralStringResource(R.plurals.dogs, iteration, iteration)

                            DropdownMenuItem(
                                text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                                onClick = {
                                    selectDogs(iteration)
                                    dogText = option
                                    dogExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }

                var bikeExpanded by remember { mutableStateOf(false) }
                val initialBike = stringResource(R.string.no)
                var bikeText by remember { mutableStateOf(initialBike) }

                ExposedDropdownMenuBox(
                    expanded = bikeExpanded,
                    onExpandedChange = { bikeExpanded = !bikeExpanded },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    OutlinedTextField(
                        value = bikeText,
                        readOnly = true,
                        enabled = false,
                        label = { Text(stringResource(R.string.travels_with_bike)) },
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = bikeExpanded)
                        },
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor =
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        modifier = Modifier.fillMaxWidth().menuAnchor())

                    ExposedDropdownMenu(
                        expanded = bikeExpanded,
                        onDismissRequest = { bikeExpanded = false },
                    ) {
                        repeat(getPassengersAmount() + 1) { iteration ->
                            val option =
                                if (iteration == 0) stringResource(R.string.no)
                                else pluralStringResource(R.plurals.bikes, iteration, iteration)

                            DropdownMenuItem(
                                text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                                onClick = {
                                    selectBikes(iteration)
                                    bikeText = option
                                    bikeExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }

                var luggageExpanded by remember { mutableStateOf(false) }
                val initialLuggage = stringResource(R.string.absence)
                var luggageText by remember { mutableStateOf(initialLuggage) }

                ExposedDropdownMenuBox(
                    expanded = luggageExpanded,
                    onExpandedChange = { luggageExpanded = !luggageExpanded },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    OutlinedTextField(
                        value = luggageText,
                        readOnly = true,
                        enabled = false,
                        label = { Text(stringResource(R.string.extra_luggage)) },
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = luggageExpanded)
                        },
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor =
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        modifier = Modifier.fillMaxWidth().menuAnchor())

                    ExposedDropdownMenu(
                        expanded = luggageExpanded,
                        onDismissRequest = { luggageExpanded = false },
                    ) {
                        repeat(getPassengersAmount() + 1) { iteration ->
                            val option =
                                if (iteration == 0) stringResource(R.string.absence)
                                else pluralStringResource(R.plurals.luggage, iteration, iteration)

                            DropdownMenuItem(
                                text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                                onClick = {
                                    selectLuggage(iteration)
                                    luggageText = option
                                    luggageExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
            } else {
                val classes = stringArrayResource(R.array.classes)
                var classExpanded by remember { mutableStateOf(false) }
                var classText by remember { mutableStateOf(classes[0]) }

                ExposedDropdownMenuBox(
                    expanded = classExpanded,
                    onExpandedChange = { classExpanded = !classExpanded },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    OutlinedTextField(
                        value = classText,
                        readOnly = true,
                        enabled = false,
                        label = { Text(stringResource(R.string.class_string)) },
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded)
                        },
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor =
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        modifier = Modifier.fillMaxWidth().menuAnchor())

                    ExposedDropdownMenu(
                        expanded = classExpanded,
                        onDismissRequest = { classExpanded = false },
                    ) {
                        classes.forEachIndexed { index, option ->
                            DropdownMenuItem(
                                text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                                onClick = {
                                    selectClass(index)
                                    classText = option
                                    classExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
            }

            val numberFormat = NumberFormat.getCurrencyInstance()
            numberFormat.currency = Currency.getInstance("PLN")

            Text(
                text =
                    stringResource(
                        R.string.price_for_selected_passengers, numberFormat.format(state.price)),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(10.dp),
            )

            Button(
                onClick = {
                    if (state.checkedPassengers.any { it }) {
                        onBuyTicket()
                        onBuyButtonClick()
                    }
                },
                modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
            ) {
                Text(stringResource(R.string.buy_ticket))
            }
        }
    }
}

@Composable
fun PassengerListItem(
    index: Int,
    passenger: Passenger,
    checked: Boolean,
    onCheckedChange: (Int, Boolean) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier.fillMaxWidth().padding(start = 24.dp, end = 16.dp).padding(vertical = 8.dp),
    ) {
        Column {
            Text(passenger.name, style = MaterialTheme.typography.bodyLarge)

            val discount = stringArrayResource(R.array.discounts)[passenger.discount]

            if (passenger.hasREGIOCard) {
                Text(
                    stringResource(R.string.passenger_with_regio_card, discount),
                    style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(discount, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Checkbox(checked = checked, onCheckedChange = { onCheckedChange(index, it) })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionsDetailsTopAppBar(onUpClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(R.string.connection_details)) },
        navigationIcon = {
            IconButton(onClick = onUpClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                )
            }
        })
}

@Preview(showBackground = true)
@Composable
fun ConnectionsDetailsREGScreenPreview() {
    val passengers =
        listOf(
            Passenger("Adam Majewski", true, 0),
            Passenger("Marzena Nowakowska", false, 1),
            Passenger("Roman Zawadzki", false, 2),
        )

    PKPAndroidTheme {
        ConnectionsDetailsScreen(
            uiState =
                MutableStateFlow(
                    ConnectionDetailsUiState(
                        passengers = passengers,
                        checkedPassengers = List(passengers.size) { false },
                        connection =
                            Connection(
                                trainId = 1,
                                trainNumber = 64340,
                                trainBrand = TrainBrand.REG,
                                ticketPrice = "12.50",
                                departureStation = "Strzelce Opolskie",
                                arrivalStation = "Gliwice",
                                departureDateTime = LocalDateTime.parse("2024-06-24T12:00:00"),
                                arrivalDateTime = LocalDateTime.parse("2024-06-24T13:00:00")),
                        price = BigDecimal("12.50"))))
    }
}

@Preview(showBackground = true)
@Composable
fun ConnectionsDetailsICScreenPreview() {
    val passengers =
        listOf(
            Passenger("Adam Majewski", true, 0),
            Passenger("Marzena Nowakowska", false, 1),
            Passenger("Roman Zawadzki", false, 2),
        )

    PKPAndroidTheme {
        ConnectionsDetailsScreen(
            uiState =
                MutableStateFlow(
                    ConnectionDetailsUiState(
                        passengers = passengers,
                        checkedPassengers = List(passengers.size) { false },
                        connection =
                            Connection(
                                trainId = 1,
                                trainNumber = 64340,
                                trainBrand = TrainBrand.IC,
                                ticketPrice = "12.50",
                                departureStation = "Strzelce Opolskie",
                                arrivalStation = "Gliwice",
                                departureDateTime = LocalDateTime.parse("2024-06-24T12:00:00"),
                                arrivalDateTime = LocalDateTime.parse("2024-06-24T13:00:00")),
                        price = BigDecimal("12.50"))))
    }
}
