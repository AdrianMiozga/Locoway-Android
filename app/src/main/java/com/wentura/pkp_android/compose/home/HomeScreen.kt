package com.wentura.pkp_android.compose.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.wentura.pkp_android.R
import com.wentura.pkp_android.data.LocationServiceBroadcastMonitor
import com.wentura.pkp_android.ui.PKPAndroidTheme
import com.wentura.pkp_android.util.findActivity
import com.wentura.pkp_android.viewmodels.HomeUiState
import com.wentura.pkp_android.viewmodels.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

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
        uiState = homeViewModel.uiState,
        drawerState = drawerState,
        onSearchClick = onSearchClick,
        onLoginClick = onLoginClick,
        onMyAccountClick = onMyAccountClick,
        onMyTicketsClick = onMyTicketsClick,
        onPassengersClick = onPassengersClick,
        onDepartureStationClick = homeViewModel::updateDepartureStation,
        onArrivalStationClick = homeViewModel::updateArrivalStation,
        toggleDepartureStationDialog = homeViewModel::toggleDepartureStationDialog,
        toggleArrivalStationDialog = homeViewModel::toggleArrivalStationDialog,
        onDepartureQueryUpdate = homeViewModel::departureQueryUpdate,
        onArrivalQueryUpdate = homeViewModel::arrivalQueryUpdate,
        onClearDepartureQuery = homeViewModel::clearDepartureQuery,
        onClearArrivalQuery = homeViewModel::clearArrivalQuery,
        onSwapStationsClick = homeViewModel::swapStations,
        onGetCurrentLocation = homeViewModel::onGetCurrentLocation,
        onGotLocality = homeViewModel::onGotLocality,
        onGeocoderFail = homeViewModel::onGeocoderFail,
        onCancelLocation = homeViewModel::onCancelLocation,
        toggleOnNoLocationDialog = homeViewModel::toggleOnNoLocationDialog,
        onMessageShown = homeViewModel::onMessageShown
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
    onDepartureStationClick: (String) -> Unit = {},
    onArrivalStationClick: (String) -> Unit = {},
    toggleDepartureStationDialog: () -> Unit = {},
    toggleArrivalStationDialog: () -> Unit = {},
    onDepartureQueryUpdate: (String) -> Unit = {},
    onArrivalQueryUpdate: (String) -> Unit = {},
    onClearDepartureQuery: () -> Unit = {},
    onClearArrivalQuery: () -> Unit = {},
    onSwapStationsClick: () -> Unit = {},
    onGetCurrentLocation: () -> Unit = {},
    onGotLocality: (String) -> Unit = {},
    onGeocoderFail: () -> Unit = {},
    onCancelLocation: () -> Unit = {},
    toggleOnNoLocationDialog: () -> Unit = {},
    onMessageShown: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    var locationServiceJob: Job? = null
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    HomeNavigationDrawer(
        uiState = uiState,
        drawerState = drawerState,
        scope = scope,
        onLoginClick = onLoginClick,
        onMyAccountClick = onMyAccountClick,
        onMyTicketsClick = onMyTicketsClick,
        onPassengersClick = onPassengersClick
    ) {
        Scaffold(
            topBar = { TopAppBar(scope, drawerState) },
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            val state by uiState.collectAsStateWithLifecycle()

            val departureDate = rememberSaveable { mutableStateOf(LocalDate.now()) }
            val departureTime = rememberSaveable { mutableStateOf(LocalTime.now()) }

            val showDatePicker = rememberSaveable { mutableStateOf(false) }
            val showTimePicker = rememberSaveable { mutableStateOf(false) }

            val showLocationRationale = rememberSaveable { mutableStateOf(false) }

            val locationPermissionRequest =
                rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
                    isGranted ->
                    if (isGranted) {
                        getCurrentLocation(
                            context.findActivity(),
                            onGetCurrentLocation,
                            onGotLocality,
                            toggleOnNoLocationDialog,
                            onGeocoderFail
                        )
                    }
                }

            if (showDatePicker.value) {
                DatePicker(showDatePicker, departureDate)
            }

            if (showTimePicker.value) {
                TimePicker(showTimePicker, departureTime)
            }

            if (state.showDepartureStationDialog) {
                StationSearchDialog(
                    dialogTitle = R.string.departure_station,
                    query = state.departureQuery,
                    stations = state.departureStations,
                    recentStations = state.recentDepartureStations,
                    onQueryUpdate = onDepartureQueryUpdate,
                    onQueryClear = onClearDepartureQuery,
                    onDismissRequest = toggleDepartureStationDialog,
                    onStationClick = onDepartureStationClick,
                )
            }

            if (state.showArrivalStationDialog) {
                StationSearchDialog(
                    dialogTitle = R.string.arrival_station,
                    query = state.arrivalQuery,
                    stations = state.arrivalStations,
                    recentStations = state.recentArrivalStations,
                    onQueryUpdate = onArrivalQueryUpdate,
                    onQueryClear = onClearArrivalQuery,
                    onDismissRequest = toggleArrivalStationDialog,
                    onStationClick = onArrivalStationClick,
                )
            }

            if (showLocationRationale.value) {
                LocationRationaleDialog(
                    onDismissRequest = { showLocationRationale.value = false },
                    onPermissionRequest = {
                        showLocationRationale.value = false
                        locationPermissionRequest.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    }
                )
            }

            if (state.showNoLocationServiceDialog) {
                NoLocationServiceDialog(
                    onDismissRequest = {
                        toggleOnNoLocationDialog()
                        onCancelLocation()
                        locationServiceJob?.cancel()
                    },
                    onConfirmRequest = {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

                        // TODO: Use DI
                        val locationServiceBroadcastMonitor =
                            LocationServiceBroadcastMonitor(context, scope)

                        val areLocationServicesEnabled =
                            locationServiceBroadcastMonitor.isEnabled.stateIn(
                                scope,
                                SharingStarted.WhileSubscribed(),
                                false
                            )

                        locationServiceJob =
                            scope.launch {
                                areLocationServicesEnabled.collect { enabled ->
                                    if (enabled) {
                                        getCurrentLocation(
                                            context.findActivity(),
                                            onGetCurrentLocation,
                                            onGotLocality,
                                            toggleOnNoLocationDialog,
                                            onGeocoderFail
                                        )

                                        toggleOnNoLocationDialog()
                                        cancel()
                                    }
                                }
                            }
                    }
                )
            }

            Column(
                modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.isLoading) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                OutlinedTextField(
                    label = { Text(stringResource(R.string.departure_station)) },
                    onValueChange = {},
                    value = state.departureStation,
                    readOnly = true,
                    enabled = false,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (
                                    context.checkSelfPermission(
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    getCurrentLocation(
                                        context.findActivity(),
                                        onGetCurrentLocation,
                                        onGotLocality,
                                        toggleOnNoLocationDialog,
                                        onGeocoderFail
                                    )
                                } else if (
                                    ActivityCompat.shouldShowRequestPermissionRationale(
                                        context.findActivity(),
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                ) {
                                    showLocationRationale.value = true
                                } else {
                                    locationPermissionRequest.launch(
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.my_location_24),
                                contentDescription = stringResource(R.string.swap_stations)
                            )
                        }
                    },
                    modifier =
                        Modifier.padding(horizontal = 20.dp)
                            .padding(top = 20.dp, bottom = 10.dp)
                            .fillMaxWidth()
                            .clickable { toggleDepartureStationDialog() },
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        )
                )

                OutlinedTextField(
                    label = { Text(stringResource(R.string.arrival_station)) },
                    onValueChange = {},
                    value = state.arrivalStation,
                    readOnly = true,
                    enabled = false,
                    trailingIcon = {
                        if (
                            state.arrivalStation.isNotEmpty() || state.departureStation.isNotEmpty()
                        ) {
                            IconButton(onClick = onSwapStationsClick) {
                                Icon(
                                    painter = painterResource(R.drawable.outline_swap_vert_24),
                                    contentDescription = stringResource(R.string.swap_stations)
                                )
                            }
                        }
                    },
                    modifier =
                        Modifier.padding(horizontal = 20.dp)
                            .padding(vertical = 10.dp)
                            .fillMaxWidth()
                            .clickable { toggleArrivalStationDialog() },
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        )
                )

                Row {
                    OutlinedTextField(
                        label = { Text(stringResource(R.string.departure_date)) },
                        value =
                            departureDate.value.format(
                                DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                            ),
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        modifier =
                            Modifier.padding(vertical = 10.dp)
                                .padding(start = 20.dp, end = 10.dp)
                                .weight(1f)
                                .clickable { showDatePicker.value = true },
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                    )

                    OutlinedTextField(
                        label = { Text(stringResource(R.string.departure_time)) },
                        value =
                            departureTime.value.format(
                                DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                            ),
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        modifier =
                            Modifier.padding(vertical = 10.dp)
                                .padding(start = 10.dp, end = 20.dp)
                                .weight(1f)
                                .clickable { showTimePicker.value = true },
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                    )
                }

                Button(onClick = onSearchClick, modifier = Modifier.padding(10.dp)) {
                    Text(stringResource(R.string.search))
                }
            }

            state.userMessage?.let { message ->
                LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar(context.getString(message))
                    onMessageShown()
                }
            }
        }
    }
}

private fun getCurrentLocation(
    activity: Activity,
    onGetCurrentLocation: () -> Unit,
    onGotLocality: (String) -> Unit,
    onNoLocationService: () -> Unit,
    onGeocoderFail: () -> Unit,
) {
    if (
        activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
    ) {
        onGetCurrentLocation()

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                if (location == null) {
                    onNoLocationService()
                    return@addOnSuccessListener
                }

                val result =
                    Geocoder(activity)
                        .getFromLocation(location.latitude, location.longitude, 1)
                        ?.first()

                if (result == null) {
                    onGeocoderFail()
                } else {
                    onGotLocality(result.locality)
                }
            }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopAppBar(
    scope: CoroutineScope,
    drawerState: DrawerState,
) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.menu)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    PKPAndroidTheme { HomeScreen(uiState = MutableStateFlow(HomeUiState(isSignedIn = false))) }
}
