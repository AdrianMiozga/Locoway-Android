package org.wentura.locoway.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.wentura.locoway.data.model.Passenger
import org.wentura.locoway.data.repository.PassengerRepository
import org.wentura.locoway.domain.TrimPassengerNameUseCase

data class PassengersUiState(
    val isLoading: Boolean = false,
    val passengers: List<Passenger> = emptyList(),
    val currentPassenger: Passenger = Passenger(),
    val openAddPassengerDialog: Boolean = false,
    val openEditPassengerDialog: Boolean = false,
)

@HiltViewModel
class PassengersViewModel
@Inject
constructor(
    private val passengerRepository: PassengerRepository,
    private val trimPassengerNameUseCase: TrimPassengerNameUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PassengersUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getPassengers()
    }

    fun addPassenger() {
        val state = _uiState.value

        if (state.currentPassenger.name.isBlank()) {
            return
        }

        viewModelScope.launch {
            passengerRepository.addPassenger(trimPassengerNameUseCase(state.currentPassenger))

            _uiState.update {
                it.copy(openAddPassengerDialog = false, currentPassenger = Passenger())
            }

            getPassengers()
        }
    }

    private fun getPassengers() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            _uiState.update {
                it.copy(passengers = passengerRepository.getPassengers(), isLoading = false)
            }
        }
    }

    fun updatePassenger() {
        val state = _uiState.value

        if (state.currentPassenger.name.isBlank()) {
            return
        }

        _uiState.update { it.copy(openEditPassengerDialog = false, currentPassenger = Passenger()) }

        viewModelScope.launch {
            passengerRepository.updatePassenger(
                state.currentPassenger.documentPath,
                state.currentPassenger,
            )

            getPassengers()
        }
    }

    fun deletePassenger() {
        viewModelScope.launch {
            passengerRepository.deletePassenger(_uiState.value.currentPassenger.documentPath)

            _uiState.update {
                it.copy(openEditPassengerDialog = false, currentPassenger = Passenger())
            }

            getPassengers()
        }
    }

    fun showEditPassengerDialog(position: Int) {
        val state = _uiState.value

        _uiState.update {
            it.copy(openEditPassengerDialog = true, currentPassenger = state.passengers[position])
        }
    }

    fun updateName(name: String) {
        val passenger = _uiState.value.currentPassenger.copy(name = name)

        _uiState.update { it.copy(currentPassenger = passenger) }
    }

    fun toggleREGIOCard() {
        val passenger =
            _uiState.value.currentPassenger.copy(
                hasREGIOCard = !_uiState.value.currentPassenger.hasREGIOCard
            )

        _uiState.update { it.copy(currentPassenger = passenger) }
    }

    fun changeDiscount(id: Int) {
        val passenger = _uiState.value.currentPassenger.copy(discount = id)

        _uiState.update { it.copy(currentPassenger = passenger) }
    }

    fun onEditPassengerDialogDismissRequest() {
        _uiState.update { it.copy(openEditPassengerDialog = false, currentPassenger = Passenger()) }
    }

    fun onAddPassengerDismissRequest() {
        _uiState.update { it.copy(openAddPassengerDialog = false, currentPassenger = Passenger()) }
    }

    fun showAddPassengerDialog() {
        _uiState.update { it.copy(openAddPassengerDialog = true) }
    }
}
