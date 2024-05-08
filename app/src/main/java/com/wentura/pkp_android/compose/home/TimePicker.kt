package com.wentura.pkp_android.compose.home

import android.icu.util.Calendar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.wentura.pkp_android.R
import java.text.DateFormat

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TimePicker(showTimePicker: MutableState<Boolean>, departureTime: MutableState<String>) {
    val timePickerState = rememberTimePickerState()

    TimePickerDialog(onDismissRequest = { showTimePicker.value = false }, confirmButton = {
        TextButton(
            onClick = {
                showTimePicker.value = false

                val calendar = Calendar.getInstance().apply {
                    clear()
                    set(Calendar.HOUR, timePickerState.hour)
                    set(Calendar.MINUTE, timePickerState.minute)
                }

                departureTime.value =
                    DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)
            },
        ) {
            Text(stringResource(R.string.ok))
        }
    }, dismissButton = {
        TextButton(
            onClick = { showTimePicker.value = false },
        ) {
            Text(stringResource(R.string.cancel))
        }
    }) {
        androidx.compose.material3.TimePicker(state = timePickerState)
    }
}
