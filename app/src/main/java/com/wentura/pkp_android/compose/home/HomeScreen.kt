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
import androidx.compose.material3.DrawerState
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wentura.pkp_android.R
import com.wentura.pkp_android.ui.PKPAndroidTheme
import com.wentura.pkp_android.viewmodels.HomeUiState
import com.wentura.pkp_android.viewmodels.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    onSearchClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onMyAccountClick: () -> Unit = {},
    onMyTicketsClick: () -> Unit = {},
    onPassengersClick: () -> Unit = {},
) {
    HomeScreen(
        homeViewModel.uiState,
        drawerState,
        onSearchClick,
        onLoginClick,
        onMyAccountClick,
        onMyTicketsClick,
        onPassengersClick,
        homeViewModel::snackbarMessageShown
    )
}

@Composable
fun HomeScreen(
    uiState: StateFlow<HomeUiState>,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    onSearchClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onMyAccountClick: () -> Unit = {},
    onMyTicketsClick: () -> Unit = {},
    onPassengersClick: () -> Unit = {},
    onSnackBarMessageShown: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

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
                onClick = { scope.launch { drawerState.close() } },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp))

            val state by uiState.collectAsStateWithLifecycle()

            if (state.isSignedIn) {
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.my_account)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle, contentDescription = null
                        )
                    },
                    selected = false,
                    onClick = onMyAccountClick,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    label = { Text(text = stringResource(R.string.my_tickets)) },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.outline_confirmation_number_24),
                            contentDescription = null
                        )
                    },
                    selected = false,
                    onClick = onMyTicketsClick,
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
                    onClick = onPassengersClick,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            } else {
                NavigationDrawerItem(
                    label = { Text(text = stringResource(R.string.login)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle, contentDescription = null
                        )
                    },
                    selected = false,
                    onClick = onLoginClick,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }) {
        Scaffold(topBar = { TopAppBar(scope, drawerState) },
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }) { innerPadding ->
            Fields(
                modifier = Modifier.padding(innerPadding),
                onSearchClick = onSearchClick,
            )

            val state by uiState.collectAsStateWithLifecycle()

            state.userMessage?.let { message ->
                LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar(context.getString(message))
                    onSnackBarMessageShown()
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopAppBar(
    scope: CoroutineScope, drawerState: DrawerState,
) {
    CenterAlignedTopAppBar(title = { Text(stringResource(R.string.app_name)) }, navigationIcon = {
        IconButton(onClick = { scope.launch { drawerState.open() } }) {
            Icon(
                imageVector = Icons.Filled.Menu, contentDescription = stringResource(R.string.menu)
            )
        }
    })
}

@Composable
fun Fields(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {},
    departureStation: String = "",
    arrivalStation: String = "",
) {
    var departureStationText by rememberSaveable { mutableStateOf(departureStation) }
    var arrivalStationText by rememberSaveable { mutableStateOf(arrivalStation) }
    val departureDate = rememberSaveable {
        mutableStateOf(
            DateFormat.getDateInstance().format(Calendar.getInstance().time)
        )
    }

    val departureTime = rememberSaveable {
        mutableStateOf(
            DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().time)
        )
    }

    val showDatePicker = rememberSaveable { mutableStateOf(false) }

    if (showDatePicker.value) {
        DatePicker(showDatePicker, departureDate)
    }

    val showTimePicker = rememberSaveable { mutableStateOf(false) }

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
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 10.dp)
                .fillMaxWidth()
        )

        OutlinedTextField(
            label = { Text(stringResource(R.string.arrival_station)) },
            onValueChange = { arrivalStationText = it },
            value = arrivalStationText,
            singleLine = true,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(vertical = 10.dp)
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
                    .padding(vertical = 10.dp)
                    .padding(start = 20.dp, end = 10.dp)
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
                    .padding(vertical = 10.dp)
                    .padding(start = 10.dp, end = 20.dp)
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
        HomeScreen(
            uiState = MutableStateFlow(HomeUiState(isSignedIn = false))
        )
    }
}

@Preview
@Composable
fun NavigationDrawerPreview() {
    PKPAndroidTheme {
        HomeScreen(
            uiState = MutableStateFlow(HomeUiState(isSignedIn = false)),
            drawerState = rememberDrawerState(
                initialValue = DrawerValue.Open
            )
        )
    }
}
