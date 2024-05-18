package com.wentura.pkp_android.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wentura.pkp_android.data.AuthenticationRepository
import com.wentura.pkp_android.data.RecentSearchRepository
import com.wentura.pkp_android.data.RecentSearchStation
import com.wentura.pkp_android.data.Station
import com.wentura.pkp_android.data.StationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isSignedIn: Boolean = false,
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
    @StringRes val userMessage: Int? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val stationRepository: StationRepository,
    private val recentSearchRepository: RecentSearchRepository,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(HomeUiState(isSignedIn = authenticationRepository.isUserSignedIn()))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authenticationRepository.authentication.collect { authentication ->
                _uiState.update {
                    it.copy(
                        isSignedIn = authentication.isSignedIn,
                        userMessage = authentication.userMessage
                    )
                }
            }
        }
    }

    private fun getRecentDepartureStations() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(recentDepartureStations = recentSearchRepository.getRecentDepartureStations()
                    .map { recentSearchStation ->
                        Station(recentSearchStation.name)
                    })
            }
        }
    }

    private fun getRecentArrivalStations() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(recentArrivalStations = recentSearchRepository.getRecentArrivalStations()
                    .map { recentSearchStation ->
                        Station(recentSearchStation.name)
                    })
            }
        }
    }

    fun snackbarMessageShown() {
        authenticationRepository.clearMessage()
    }

    fun departureQueryUpdate(query: String) {
        _uiState.update {
            it.copy(departureQuery = query)
        }

        if (query.length < 3) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(departureStations = emptyList())
                }
            }
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
        _uiState.update {
            it.copy(arrivalQuery = query)
        }

        if (query.length < 3) {
            _uiState.update {
                it.copy(arrivalStations = emptyList())
            }
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
                RecentSearchStation(
                    type = "departure", name = station
                )
            )
        }

        getRecentDepartureStations()
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
                RecentSearchStation(
                    type = "arrival", name = station
                )
            )
        }

        getRecentArrivalStations()
    }

    fun swapStations() {
        _uiState.update {
            it.copy(
                departureStation = it.arrivalStation, arrivalStation = it.departureStation,
                departureQuery = it.arrivalStation, arrivalQuery = it.departureStation,
                departureStations = it.arrivalStations, arrivalStations = it.departureStations,
            )
        }
    }

    fun toggleDepartureStationDialog() {
        _uiState.update {
            it.copy(showDepartureStationDialog = !it.showDepartureStationDialog)
        }

        if (_uiState.value.showDepartureStationDialog) {
            getRecentDepartureStations()
        }
    }

    fun toggleArrivalStationDialog() {
        _uiState.update {
            it.copy(showArrivalStationDialog = !it.showArrivalStationDialog)
        }

        if (_uiState.value.showArrivalStationDialog) {
            getRecentArrivalStations()
        }
    }
}
