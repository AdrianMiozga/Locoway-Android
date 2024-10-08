package org.wentura.locoway.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.wentura.locoway.R
import org.wentura.locoway.data.model.RecentSearchStation
import org.wentura.locoway.data.model.Station
import org.wentura.locoway.data.repository.AuthenticationRepository
import org.wentura.locoway.data.repository.RecentSearchRepository
import org.wentura.locoway.data.repository.StationRepository
import org.wentura.locoway.data.repository.TicketRepository

data class HomeUiState(
    val isSignedIn: Boolean = false,
    val isLoading: Boolean = false,
    val departureStation: String = "",
    val arrivalStation: String = "",
    val showDepartureStationDialog: Boolean = false,
    val showArrivalStationDialog: Boolean = false,
    val departureQuery: String = "",
    val arrivalQuery: String = "",
    val departureStations: List<Station> = emptyList(),
    val arrivalStations: List<Station> = emptyList(),
    val recentDepartureStations: List<Station> = emptyList(),
    val recentArrivalStations: List<Station> = emptyList(),
    val showNoLocationServiceDialog: Boolean = false,
    @StringRes val userMessage: Int? = null,
)

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val stationRepository: StationRepository,
    private val recentSearchRepository: RecentSearchRepository,
    private val ticketRepository: TicketRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authenticationRepository.authentication.collect { authentication ->
                _uiState.update {
                    it.copy(
                        isSignedIn = authentication.isSignedIn,
                        userMessage = authentication.userMessage,
                    )
                }
            }
        }

        viewModelScope.launch {
            ticketRepository.userMessage.collect { stringResource ->
                _uiState.update { it.copy(userMessage = stringResource) }
            }
        }
    }

    fun onMessageShown() {
        authenticationRepository.clearMessage()
        ticketRepository.clearMessage()

        _uiState.update { it.copy(userMessage = null) }
    }

    fun departureQueryUpdate(query: String) {
        _uiState.update { it.copy(departureQuery = query) }

        if (query.length < MIN_QUERY_LENGTH) {
            viewModelScope.launch { _uiState.update { it.copy(departureStations = emptyList()) } }
        } else {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(departureStations = stationRepository.searchStations(query))
                }
            }
        }
    }

    fun clearDepartureQuery() {
        _uiState.update {
            it.copy(departureQuery = "", departureStation = "", departureStations = emptyList())
        }
    }

    fun arrivalQueryUpdate(query: String) {
        _uiState.update { it.copy(arrivalQuery = query) }

        if (query.length < MIN_QUERY_LENGTH) {
            _uiState.update { it.copy(arrivalStations = emptyList()) }
        } else {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(arrivalStations = stationRepository.searchStations(query))
                }
            }
        }
    }

    fun clearArrivalQuery() {
        _uiState.update {
            it.copy(arrivalQuery = "", arrivalStation = "", arrivalStations = emptyList())
        }
    }

    fun updateDepartureStation(station: String) {
        _uiState.update {
            it.copy(
                departureStation = station,
                departureQuery = station,
                showDepartureStationDialog = false,
            )
        }

        viewModelScope.launch {
            recentSearchRepository.addRecentStation(
                RecentSearchStation(type = "departure", name = station)
            )
        }
    }

    fun updateArrivalStation(station: String) {
        _uiState.update {
            it.copy(
                arrivalStation = station,
                arrivalQuery = station,
                showArrivalStationDialog = false,
            )
        }

        viewModelScope.launch {
            recentSearchRepository.addRecentStation(
                RecentSearchStation(type = "arrival", name = station)
            )
        }
    }

    fun swapStations() {
        _uiState.update {
            it.copy(
                departureStation = it.arrivalStation,
                arrivalStation = it.departureStation,
                departureQuery = it.arrivalStation,
                arrivalQuery = it.departureStation,
                departureStations = it.arrivalStations,
                arrivalStations = it.departureStations,
            )
        }
    }

    fun toggleDepartureStationDialog() {
        _uiState.update { it.copy(showDepartureStationDialog = !it.showDepartureStationDialog) }

        if (_uiState.value.showDepartureStationDialog) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        recentDepartureStations =
                            recentSearchRepository.getRecentDepartureStations().map {
                                recentSearchStation ->
                                Station(recentSearchStation.name)
                            }
                    )
                }
            }
        }
    }

    fun toggleArrivalStationDialog() {
        _uiState.update { it.copy(showArrivalStationDialog = !it.showArrivalStationDialog) }

        if (_uiState.value.showArrivalStationDialog) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        recentArrivalStations =
                            recentSearchRepository.getRecentArrivalStations().map {
                                recentSearchStation ->
                                Station(recentSearchStation.name)
                            }
                    )
                }
            }
        }
    }

    fun onGetCurrentLocation() {
        _uiState.update { it.copy(isLoading = true) }
    }

    fun onCancelLocation() {
        _uiState.update { it.copy(isLoading = false) }
    }

    fun onGotLocality(locality: String) {
        viewModelScope.launch {
            val stations = stationRepository.searchStations(locality)

            if (stations.first().name == locality) {
                _uiState.update {
                    it.copy(
                        departureStation = locality,
                        departureQuery = locality,
                        isLoading = false,
                    )
                }
            } else {
                onGeocoderFail()
            }
        }
    }

    fun onGeocoderFail() {
        _uiState.update {
            it.copy(userMessage = R.string.could_not_find_station, isLoading = false)
        }
    }

    fun toggleOnNoLocationDialog() {
        _uiState.update {
            it.copy(showNoLocationServiceDialog = !_uiState.value.showNoLocationServiceDialog)
        }
    }

    companion object {
        private const val MIN_QUERY_LENGTH = 3
    }
}
