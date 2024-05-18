package com.wentura.pkp_android.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wentura.pkp_android.data.AuthenticationRepository
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
    val stations: List<Station> = emptyList(),
    @StringRes val userMessage: Int? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val stationRepository: StationRepository,
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

    fun snackbarMessageShown() {
        authenticationRepository.clearMessage()
    }

    fun searchStations(query: String) {
        if (query.length < 3) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(stations = stationRepository.searchStations(query))
            }
        }
    }

    fun clearStations() {
        _uiState.update {
            it.copy(stations = emptyList())
        }
    }
}
