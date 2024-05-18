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

data class PassengersUiState(
    val isLoading: Boolean = false,
    val passengers: List<Passenger> = emptyList(),
    val currentPassenger: Passenger = Passenger(),
)

@HiltViewModel
class PassengersViewModel @Inject constructor(
    private val passengerRepository: PassengerRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PassengersUiState())
    val uiState: StateFlow<PassengersUiState> = _uiState.asStateFlow()

    init {
        getPassengers()
    }

    fun addPassenger() {
        val state = _uiState.value

        viewModelScope.launch {
            passengerRepository.addPassenger(state.currentPassenger)

            getPassengers()
        }
    }

    private fun getPassengers() {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(passengers = passengerRepository.getPassengers(), isLoading = false)
            }
        }
    }

    fun updatePassenger() {
        val state = _uiState.value

        viewModelScope.launch {
            passengerRepository.updatePassenger(
                state.currentPassenger.documentPath, state.currentPassenger
            )

            getPassengers()
        }
    }

    fun deletePassenger() {
        viewModelScope.launch {
            passengerRepository.deletePassenger(_uiState.value.currentPassenger.documentPath)
            getPassengers()
        }

        resetCurrentPassenger()
    }

    fun setCurrentPassenger(position: Int) {
        val state = _uiState.value

        _uiState.update {
            it.copy(currentPassenger = state.passengers[position])
        }
    }

    fun resetCurrentPassenger() {
        _uiState.update {
            it.copy(currentPassenger = Passenger())
        }
    }

    fun updateName(name: String) {
        val passenger = _uiState.value.currentPassenger.copy(name = name)

        _uiState.update {
            it.copy(currentPassenger = passenger)
        }
    }

    fun toggleREGIOCard() {
        val passenger =
            _uiState.value.currentPassenger.copy(hasREGIOCard = !_uiState.value.currentPassenger.hasREGIOCard)

        _uiState.update {
            it.copy(currentPassenger = passenger)
        }
    }

    fun changeDiscount(id: Int) {
        val passenger = _uiState.value.currentPassenger.copy(discount = id)

        _uiState.update {
            it.copy(currentPassenger = passenger)
        }
    }
}
