package com.wentura.pkp_android.ui.home

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
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DatePicker(
    showDatePicker: MutableState<Boolean>, departureDate: MutableState<String>
) {
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis >= (Calendar.getInstance().timeInMillis - (24 * 60 * 60 * 1000))
        }

        override fun isSelectableYear(year: Int): Boolean {
            return year >= Calendar.getInstance().get(Calendar.YEAR)
        }
    })

    val confirmEnabled by remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }

    DatePickerDialog(onDismissRequest = { showDatePicker.value = false }, confirmButton = {
        TextButton(onClick = {
            showDatePicker.value = false
            departureDate.value =
                DateFormat.getDateInstance().format(Date(datePickerState.selectedDateMillis!!))
        }, enabled = confirmEnabled) { Text(stringResource(R.string.ok)) }
    }, dismissButton = {
        TextButton(onClick = { showDatePicker.value = false }) {
            Text(stringResource(R.string.Cancel))
        }
    }) {
        androidx.compose.material3.DatePicker(state = datePickerState)
    }
}

@Preview
@Composable
private fun DatePickerPreview() {
    DatePicker(remember { mutableStateOf(true) }, remember { mutableStateOf("") })
}
