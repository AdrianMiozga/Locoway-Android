package org.wentura.locoway.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.wentura.locoway.data.model.Connection
import org.wentura.locoway.data.model.Passenger
import org.wentura.locoway.data.repository.ConnectionsRepository
import org.wentura.locoway.data.repository.PassengerRepository
import org.wentura.locoway.data.repository.PriceRepository
import org.wentura.locoway.data.repository.TicketRepository
import org.wentura.locoway.domain.TrimPassengerNameUseCase

data class ConnectionDetailsUiState(
    val passengers: List<Passenger> = emptyList(),
    val selectedPassengers: List<Boolean> = emptyList(),
    val currentPassenger: Passenger = Passenger(),
    val openAddPassengerDialog: Boolean = false,
    val connection: Connection,
    val price: BigDecimal = BigDecimal.ZERO,
    val amountOfDogs: Int = 0,
    val amountOfBikes: Int = 0,
    val amountOfLuggage: Int = 0,
    val selectedClass: Int = 0,
    val userMessage: Boolean = false,
)

@HiltViewModel
class ConnectionDetailsViewModel
@Inject
constructor(
    private val passengerRepository: PassengerRepository,
    connectionsRepository: ConnectionsRepository,
    private val priceRepository: PriceRepository,
    private val ticketRepository: TicketRepository,
    private val trimPassengerNameUseCase: TrimPassengerNameUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private var trainId: String = checkNotNull(savedStateHandle["trainId"])

    private val _uiState =
        MutableStateFlow(
            ConnectionDetailsUiState(
                connection = connectionsRepository.getConnectionByIdFromCache(trainId.toLong())
            )
        )
    val uiState = _uiState.asStateFlow()

    init {
        getInitialPassengers()
        getPrice()
    }

    fun addPassenger() {
        if (_uiState.value.currentPassenger.name.isBlank()) {
            return
        }

        viewModelScope.launch {
            passengerRepository.addPassenger(
                trimPassengerNameUseCase(_uiState.value.currentPassenger)
            )

            _uiState.update {
                it.copy(
                    openAddPassengerDialog = false,
                    currentPassenger = Passenger(),
                    selectedPassengers = it.selectedPassengers.plus(true),
                )
            }

            getPassengers()
        }
    }

    private fun getInitialPassengers() {
        viewModelScope.launch {
            val passengers = passengerRepository.getPassengers()

            _uiState.update {
                it.copy(
                    passengers = passengers,
                    selectedPassengers = List(passengers.size) { false },
                )
            }
        }
    }

    private fun getPassengers() {
        viewModelScope.launch {
            val passengers = passengerRepository.getPassengers()

            _uiState.update { it.copy(passengers = passengers) }
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

    fun onAddPassengerDismissRequest() {
        _uiState.update { it.copy(openAddPassengerDialog = false, currentPassenger = Passenger()) }
    }

    fun showAddPassengerDialog() {
        _uiState.update { it.copy(openAddPassengerDialog = true) }
    }

    fun onCheckedChange(index: Int, value: Boolean) {
        _uiState.update {
            it.copy(
                selectedPassengers =
                    it.selectedPassengers.toMutableList().apply { set(index, value) }
            )
        }

        _uiState.update { it.copy(price = getPrice()) }
    }

    private fun getPrice(): BigDecimal {
        val state = _uiState.value

        val selectedPassengers =
            state.passengers.filter { passenger ->
                state.selectedPassengers[state.passengers.indexOf(passenger)]
            }

        return priceRepository.getPrice(
            state.connection,
            selectedPassengers,
            state.amountOfDogs,
            state.amountOfBikes,
            state.amountOfLuggage,
        )
    }

    fun selectDogs(amount: Int) {
        _uiState.update { it.copy(amountOfDogs = amount) }
        _uiState.update { it.copy(price = getPrice()) }
    }

    fun selectBikes(amount: Int) {
        _uiState.update { it.copy(amountOfBikes = amount) }
        _uiState.update { it.copy(price = getPrice()) }
    }

    fun selectLuggage(amount: Int) {
        _uiState.update { it.copy(amountOfLuggage = amount) }
        _uiState.update { it.copy(price = getPrice()) }
    }

    fun selectClass(int: Int) {
        _uiState.update { it.copy(selectedClass = int) }
    }

    fun getSelectedPassengerAmount(): Int {
        return _uiState.value.selectedPassengers.count { it }
    }

    fun onBuyTicket() {
        _uiState.update { it.copy(userMessage = true) }

        val state = _uiState.value

        ticketRepository.addTicket(
            state.connection,
            state.passengers.filter { passenger ->
                state.selectedPassengers[state.passengers.indexOf(passenger)]
            },
            state.amountOfDogs,
            state.amountOfBikes,
            state.amountOfLuggage,
            state.selectedClass,
        )
    }
}
