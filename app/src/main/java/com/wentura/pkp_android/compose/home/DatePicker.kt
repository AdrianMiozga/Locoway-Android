package com.wentura.pkp_android.compose.home

import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.wentura.pkp_android.R
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.Year
import java.time.ZoneId
import androidx.compose.material3.DatePicker as AndroidDatePicker

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DatePicker(
    showDatePicker: MutableState<Boolean>, departureDate: MutableState<LocalDate>,
) {
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val time = Instant.now()
                .minus(Duration.ofDays(1))
                .toEpochMilli()

            return utcTimeMillis >= time
        }

        override fun isSelectableYear(year: Int): Boolean {
            return year >= Year.now().value
        }
    })

    val confirmEnabled by remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }

    DatePickerDialog(onDismissRequest = { showDatePicker.value = false }, confirmButton = {
        TextButton(onClick = {
            showDatePicker.value = false
            departureDate.value = Instant.ofEpochMilli(datePickerState.selectedDateMillis!!)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }, enabled = confirmEnabled) { Text(stringResource(R.string.ok)) }
    }, dismissButton = {
        TextButton(onClick = { showDatePicker.value = false }) {
            Text(stringResource(R.string.cancel))
        }
    }) {
        AndroidDatePicker(state = datePickerState)
    }
}

@Preview(showBackground = true)
@Composable
private fun DatePickerPreview() {
    DatePicker(remember { mutableStateOf(true) }, remember { mutableStateOf(LocalDate.now()) })
}
