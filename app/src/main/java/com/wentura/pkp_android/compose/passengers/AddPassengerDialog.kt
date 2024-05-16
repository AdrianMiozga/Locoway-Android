package com.wentura.pkp_android.compose.passengers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.wentura.pkp_android.viewmodels.AddPassengerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPassengerDialog(
    onDismissRequest: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    addPassengerViewModel: AddPassengerViewModel = hiltViewModel(),
) {
    val uiState = addPassengerViewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.value.savedPassenger) {
        onSaveClick()
        addPassengerViewModel.passengerSaved()
    }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxSize(),
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(topBar = {
            TopAppBar(title = { Text(stringResource(R.string.add_passenger)) }, navigationIcon = {
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Outlined.Close, contentDescription = null
                    )
                }
            }, actions = {
                TextButton(onClick = { addPassengerViewModel.addPassenger() }) {
                    Text(stringResource(R.string.save))
                }
            })
        }) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                OutlinedTextField(
                    value = uiState.value.name,
                    onValueChange = { addPassengerViewModel.updateName(it) },
                    label = { Text(stringResource(R.string.full_name)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp, bottom = 10.dp)
                )

                Text(
                    stringResource(R.string.choose_discount),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(10.dp)
                )

                val discounts = stringArrayResource(R.array.discounts)

                LazyColumn {
                    items(discounts.size) { discount ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(discounts[discount])
                            RadioButton(selected = uiState.value.selectedDiscount == discount,
                                onClick = { addPassengerViewModel.changeDiscount(discount) })
                        }
                    }
                }

                Text(
                    stringResource(R.string.optionals),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(10.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(stringResource(R.string.regio_card))
                    Checkbox(checked = uiState.value.hasREGIOCard,
                        onCheckedChange = { addPassengerViewModel.toggleREGIOCard() })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPassengerDialogPreview() {
    AddPassengerDialog()
}
