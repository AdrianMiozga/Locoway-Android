package com.wentura.pkp_android.compose.passengers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wentura.pkp_android.R
import com.wentura.pkp_android.viewmodels.PassengersUiState
import com.wentura.pkp_android.viewmodels.PassengersViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun EditPassengerDialog(
    onDismissRequest: () -> Unit = {},
    passengerViewModel: PassengersViewModel = hiltViewModel(),
) {
    EditPassengerDialog(
        uiState = passengerViewModel.uiState,
        onDismissRequest = {
            onDismissRequest()
            passengerViewModel.resetCurrentPassenger()
        },
        onSaveClick = {
            onDismissRequest()
            passengerViewModel.updatePassenger()
            passengerViewModel.resetCurrentPassenger()
        },
        onDeletePassenger = {
            onDismissRequest()
            passengerViewModel.deletePassenger()
            passengerViewModel.resetCurrentPassenger()
        },
        updateName = { passengerViewModel.updateName(it) },
        changeDiscount = { passengerViewModel.changeDiscount(it) },
        toggleREGIOCard = { passengerViewModel.toggleREGIOCard() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPassengerDialog(
    uiState: StateFlow<PassengersUiState>,
    onDismissRequest: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onDeletePassenger: () -> Unit = {},
    updateName: (String) -> Unit = {},
    changeDiscount: (Int) -> Unit = {},
    toggleREGIOCard: () -> Unit = {},
) {
    val state by uiState.collectAsStateWithLifecycle()

    val openConfirmationDialog = rememberSaveable { mutableStateOf(false) }

    if (openConfirmationDialog.value) {
        ConfirmationDialog(
            state.currentPassenger.name,
            onDismissRequest = { openConfirmationDialog.value = false },
            onConfirmationRequest = onDeletePassenger
        )
    }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxSize(),
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.edit_passenger)) },
                    navigationIcon = {
                        IconButton(onClick = onDismissRequest) {
                            Icon(imageVector = Icons.Outlined.Close, contentDescription = null)
                        }
                    },
                    actions = {
                        TextButton(onClick = { onSaveClick() }) {
                            Text(stringResource(R.string.save))
                        }
                    }
                )
            }
        ) { paddingValues ->
            val discounts = stringArrayResource(R.array.discounts)

            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                item {
                    OutlinedTextField(
                        value = state.currentPassenger.name,
                        onValueChange = { updateName(it) },
                        label = { Text(stringResource(R.string.full_name)) },
                        singleLine = true,
                        keyboardOptions =
                            KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 20.dp, bottom = 10.dp)
                    )

                    Text(
                        stringResource(R.string.choose_discount),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                items(discounts.size) { discount ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                    ) {
                        Text(discounts[discount])
                        RadioButton(
                            selected = state.currentPassenger.discount == discount,
                            onClick = { changeDiscount(discount) }
                        )
                    }
                }

                item {
                    Text(
                        stringResource(R.string.optionals),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(10.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                    ) {
                        Text(stringResource(R.string.regio_card))
                        Checkbox(
                            checked = state.currentPassenger.hasREGIOCard,
                            onCheckedChange = { toggleREGIOCard() }
                        )
                    }

                    OutlinedButton(
                        onClick = { openConfirmationDialog.value = true },
                        modifier =
                            Modifier.padding(horizontal = 20.dp, vertical = 10.dp).fillMaxWidth()
                    ) {
                        Text(
                            stringResource(R.string.delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(
    name: String,
    onDismissRequest: () -> Unit = {},
    onConfirmationRequest: () -> Unit = {},
) {
    BasicAlertDialog(onDismissRequest = {}) {
        Surface(
            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    stringResource(R.string.passenger_deletion),
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    stringResource(R.string.are_you_sure_you_want_to_delete_passenger, name),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismissRequest) { Text(stringResource(R.string.cancel)) }

                    TextButton(onClick = onConfirmationRequest) {
                        Text(stringResource(R.string.ok))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditPassengerDialogPreview() {
    EditPassengerDialog(uiState = MutableStateFlow(PassengersUiState()))
}

@Preview(showBackground = true)
@Composable
fun ConfirmationDialogPreview() {
    ConfirmationDialog("Adam Majewski")
}
