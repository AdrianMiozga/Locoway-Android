package com.wentura.pkp_android.compose.passengers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wentura.pkp_android.R
import com.wentura.pkp_android.compose.common.AddPassengerDialog
import com.wentura.pkp_android.data.model.Passenger
import com.wentura.pkp_android.ui.PKPAndroidTheme
import com.wentura.pkp_android.viewmodels.PassengersUiState
import com.wentura.pkp_android.viewmodels.PassengersViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PassengersScreen(
    onUpClick: () -> Unit = {},
    passengersViewModel: PassengersViewModel = hiltViewModel(),
) {
    PassengersScreen(
        uiState = passengersViewModel.uiState,
        onUpClick = onUpClick,
        showAddPassengerDialog = passengersViewModel::showAddPassengerDialog,
        onAddPassengerDialogDismissRequest = passengersViewModel::onAddPassengerDismissRequest,
        onAddPassenger = passengersViewModel::addPassenger,
        showEditPassengerDialog = passengersViewModel::showEditPassengerDialog,
        onEditPassengerDialogDismissRequest =
            passengersViewModel::onEditPassengerDialogDismissRequest,
        onEditPassenger = passengersViewModel::updatePassenger,
        onDeletePassenger = passengersViewModel::deletePassenger,
        updateName = passengersViewModel::updateName,
        changeDiscount = passengersViewModel::changeDiscount,
        toggleREGIOCard = passengersViewModel::toggleREGIOCard,
    )
}

@Composable
fun PassengersScreen(
    uiState: StateFlow<PassengersUiState>,
    onUpClick: () -> Unit = {},
    showAddPassengerDialog: () -> Unit = {},
    onAddPassengerDialogDismissRequest: () -> Unit = {},
    onAddPassenger: () -> Unit = {},
    showEditPassengerDialog: (Int) -> Unit = {},
    onEditPassengerDialogDismissRequest: () -> Unit = {},
    onEditPassenger: () -> Unit = {},
    onDeletePassenger: () -> Unit = {},
    updateName: (String) -> Unit = {},
    changeDiscount: (Int) -> Unit = {},
    toggleREGIOCard: () -> Unit = {},
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

    if (state.openEditPassengerDialog) {
        EditPassengerDialog(
            name = state.currentPassenger.name,
            discount = state.currentPassenger.discount,
            hasREGIOCard = state.currentPassenger.hasREGIOCard,
            onDismissRequest = onEditPassengerDialogDismissRequest,
            onSaveClick = onEditPassenger,
            onDeletePassenger = onDeletePassenger,
            updateName = updateName,
            changeDiscount = changeDiscount,
            toggleREGIOCard = toggleREGIOCard,
        )
    }

    Scaffold(
        topBar = { PassengersTopAppBar(onUpClick) },
        floatingActionButton = {
            FloatingActionButton(onClick = showAddPassengerDialog) {
                Icon(Icons.Filled.Add, stringResource(R.string.add_new_passenger))
            }
        }) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                if (state.isLoading) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                LazyColumn(contentPadding = PaddingValues(bottom = 72.dp)) {
                    if (state.passengers.isNotEmpty() || state.isLoading) {
                        items(state.passengers.size) { passenger ->
                            PassengerListItem(
                                passenger = state.passengers[passenger],
                                onEditPassenger = { showEditPassengerDialog(passenger) })

                            if (passenger != state.passengers.size - 1) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                            }
                        }
                    } else {
                        item {
                            Column(
                                modifier = Modifier.fillParentMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(stringResource(R.string.no_passengers))
                                }
                        }
                    }
                }
            }
        }
}

@Composable
fun PassengerListItem(passenger: Passenger, onEditPassenger: () -> Unit = {}) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier.fillMaxWidth().padding(start = 24.dp, end = 16.dp).padding(vertical = 8.dp)) {
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

            IconButton(onClick = onEditPassenger) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = stringResource(R.string.edit_passenger))
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengersTopAppBar(onUpClick: () -> Unit) {
    TopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.passengers))
            }
        },
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
fun PassengersScreenPreview() {
    val passengers =
        listOf(
            Passenger("Adam Majewski", true, 0),
            Passenger("Marzena Nowakowska", false, 1),
            Passenger("Roman Zawadzki", false, 2))

    PKPAndroidTheme {
        PassengersScreen(MutableStateFlow(PassengersUiState(passengers = passengers)))
    }
}
