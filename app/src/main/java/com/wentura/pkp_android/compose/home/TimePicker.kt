package com.wentura.pkp_android.compose.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.wentura.pkp_android.R
import java.time.LocalTime

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TimePicker(showTimePicker: MutableState<Boolean>, departureTime: MutableState<LocalTime>) {
    val timePickerState =
        rememberTimePickerState(departureTime.value.hour, departureTime.value.minute)

    TimePickerDialog(
        onDismissRequest = { showTimePicker.value = false },
        confirmButton = {
            TextButton(
                onClick = {
                    showTimePicker.value = false
                    departureTime.value = LocalTime.of(timePickerState.hour, timePickerState.minute)
                },
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { showTimePicker.value = false },
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        androidx.compose.material3.TimePicker(state = timePickerState)
    }
}
