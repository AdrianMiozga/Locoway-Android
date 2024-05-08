package com.wentura.pkp_android.compose.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wentura.pkp_android.R
import com.wentura.pkp_android.ui.PKPAndroidTheme
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Home(onSearchClick: () -> Unit = {}) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet(modifier = Modifier.width(252.dp)) {
            Text(
                stringResource(R.string.app_name),
                modifier = Modifier.padding(start = 28.dp, end = 28.dp, top = 16.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp))

            NavigationDrawerItem(
                label = { Text(text = stringResource(R.string.connection_search)) },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Search, contentDescription = null
                    )
                },
                selected = true,
                onClick = {},
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp))

            NavigationDrawerItem(label = { Text(text = stringResource(R.string.login)) }, icon = {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle, contentDescription = null
                )
            }, selected = false, onClick = {}, modifier = Modifier.padding(horizontal = 12.dp))

            NavigationDrawerItem(
                label = { Text(text = stringResource(R.string.my_tickets)) },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.outline_confirmation_number_24),
                        contentDescription = null
                    )
                },
                selected = false,
                onClick = {},
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            NavigationDrawerItem(
                label = { Text(text = stringResource(R.string.passengers)) },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.outline_groups_24),
                        contentDescription = null
                    )
                },
                selected = false,
                onClick = {},
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = { Text(stringResource(R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = stringResource(R.string.menu)
                            )
                        }
                    })
            }, modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Fields(
                modifier = Modifier.padding(innerPadding),
                onSearchClick = onSearchClick,
            )
        }
    }
}

@Composable
fun Fields(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {},
    departureStation: String = "",
    arrivalStation: String = ""
) {
    var departureStationText by remember { mutableStateOf(departureStation) }
    var arrivalStationText by remember { mutableStateOf(arrivalStation) }
    val departureDate = remember {
        mutableStateOf(
            DateFormat.getDateInstance().format(Calendar.getInstance().time)
        )
    }

    val departureTime = remember {
        mutableStateOf(
            DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().time)
        )
    }

    val showDatePicker = remember { mutableStateOf(false) }

    if (showDatePicker.value) {
        DatePicker(showDatePicker, departureDate)
    }

    val showTimePicker = remember { mutableStateOf(false) }

    if (showTimePicker.value) {
        TimePicker(showTimePicker, departureTime)
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            label = { Text(stringResource(R.string.departure_station)) },
            onValueChange = { departureStationText = it },
            value = departureStationText,
            singleLine = true,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        )

        OutlinedTextField(
            label = { Text(stringResource(R.string.arrival_station)) },
            onValueChange = { arrivalStationText = it },
            value = arrivalStationText,
            singleLine = true,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        )

        Row {
            OutlinedTextField(
                label = { Text(stringResource(R.string.departure_date)) },
                value = departureDate.value,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)
                    .clickable {
                        showDatePicker.value = true
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            OutlinedTextField(
                label = { Text(stringResource(R.string.departure_time)) },
                value = departureTime.value,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)
                    .clickable {
                        showTimePicker.value = true
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }

        Button(
            onClick = onSearchClick, modifier = Modifier.padding(10.dp)
        ) {
            Text(stringResource(R.string.search))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    PKPAndroidTheme {
        Home()
    }
}
