package com.wentura.pkp_android.viewmodels

import androidx.lifecycle.ViewModel
import com.wentura.pkp_android.data.Passenger
import com.wentura.pkp_android.data.PassengerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class AddPassengerUiState(
    val name: String = "",
    val hasREGIOCard: Boolean = false,
    val selectedDiscount: Int = 0,
    val savedPassenger: Boolean = false,
)

@HiltViewModel
class AddPassengerViewModel @Inject constructor(
    private val passengerRepository: PassengerRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddPassengerUiState())
    val uiState: StateFlow<AddPassengerUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    fun toggleREGIOCard() {
        _uiState.update {
            it.copy(hasREGIOCard = !_uiState.value.hasREGIOCard)
        }
    }

    fun changeDiscount(id: Int) {
        _uiState.update {
            it.copy(selectedDiscount = id)
        }
    }

    fun addPassenger() {
        val passenger = _uiState.value

        passengerRepository.addPassenger(
            Passenger(
                passenger.name,
                passenger.hasREGIOCard,
                passenger.selectedDiscount
            )
        )

        _uiState.update {
            it.copy(savedPassenger = true)
        }
    }

    fun passengerSaved() {
        _uiState.value = AddPassengerUiState()
    }
}
