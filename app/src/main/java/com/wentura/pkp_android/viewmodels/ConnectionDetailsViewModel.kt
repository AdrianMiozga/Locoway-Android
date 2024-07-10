package com.wentura.pkp_android.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wentura.pkp_android.data.Connection
import com.wentura.pkp_android.data.ConnectionsRepository
import com.wentura.pkp_android.data.Passenger
import com.wentura.pkp_android.data.PassengerRepository
import com.wentura.pkp_android.data.PriceRepository
import com.wentura.pkp_android.data.TicketRepository
import com.wentura.pkp_android.domain.TrimPassengerNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConnectionDetailsUiState(
    val passengers: List<Passenger> = emptyList(),
    val checkedPassengers: List<Boolean> = emptyList(),
    val currentPassenger: Passenger = Passenger(),
    val openAddPassengerDialog: Boolean = false,
    val connection: Connection,
    val price: BigDecimal = BigDecimal.ZERO,
    val amountOfDogs: Int = 0,
    val amountOfBikes: Int = 0,
    val amountOfLuggage: Int = 0,
    val selectedClass: Int = 0,
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
                connection = connectionsRepository.getConnectionById(trainId.toLong())))
    val uiState = _uiState.asStateFlow()

    init {
        getInitialPassengers()
        getPrice()
    }

    fun addPassenger() {
        viewModelScope.launch {
            passengerRepository.addPassenger(
                trimPassengerNameUseCase(_uiState.value.currentPassenger))

            _uiState.update {
                it.copy(
                    openAddPassengerDialog = false,
                    currentPassenger = Passenger(),
                    checkedPassengers = it.checkedPassengers.plus(true),
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
                    checkedPassengers = List(passengers.size) { false },
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
                hasREGIOCard = !_uiState.value.currentPassenger.hasREGIOCard)

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
                checkedPassengers =
                    it.checkedPassengers.toMutableList().apply { set(index, value) },
            )
        }

        _uiState.update { it.copy(price = getPrice()) }
    }

    private fun getPrice(): BigDecimal {
        val state = _uiState.value

        val selectedPassengers =
            state.passengers.filter { passenger ->
                state.checkedPassengers[state.passengers.indexOf(passenger)]
            }

        return priceRepository.getPrice(
            state.connection,
            selectedPassengers,
            state.amountOfDogs,
            state.amountOfBikes,
            state.amountOfLuggage,
            state.selectedClass,
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
        return _uiState.value.checkedPassengers.count { it }
    }

    fun onBuyTicket() {
        ticketRepository.addTicket(_uiState.value.connection)
    }
}
