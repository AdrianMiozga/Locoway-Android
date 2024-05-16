package com.wentura.pkp_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wentura.pkp_android.data.Passenger
import com.wentura.pkp_android.data.PassengerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PassengerUiState(
    val passengers: List<Passenger> = emptyList(),
    val isLoading: Boolean = false,
)

@HiltViewModel
class PassengersViewModel @Inject constructor(
    private val passengerRepository: PassengerRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PassengerUiState(isLoading = true))
    val uiState: StateFlow<PassengerUiState> = _uiState.asStateFlow()

    init {
        getPassengers()
    }

    fun getPassengers() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(passengers = passengerRepository.getPassengers(), isLoading = false)
            }
        }
    }
}
