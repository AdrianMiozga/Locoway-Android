package com.wentura.pkp_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wentura.pkp_android.data.model.Ticket
import com.wentura.pkp_android.data.repository.TicketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyTicketsUiState(
    val tickets: List<Ticket> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class MyTicketsViewModel @Inject constructor(private val ticketRepository: TicketRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(MyTicketsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val tickets = ticketRepository.getTicketsForCurrentUser()

            _uiState.update { it.copy(tickets = tickets, isLoading = false) }
        }
    }

    suspend fun getTickets() {
        _uiState.update { it.copy(tickets = ticketRepository.getTicketsForCurrentUser()) }
    }
}
