package org.wentura.locoway.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.wentura.locoway.data.model.Ticket
import org.wentura.locoway.data.repository.TicketRepository

data class MyTicketUiState(val ticket: Ticket = Ticket(), val isLoading: Boolean = true)

@HiltViewModel
class MyTicketViewModel
@Inject
constructor(ticketRepository: TicketRepository, savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow(MyTicketUiState())
    val uiState = _uiState.asStateFlow()

    private val ticketId: String = checkNotNull(savedStateHandle["ticketId"])

    init {
        _uiState.update {
            MyTicketUiState(
                ticket = ticketRepository.getTicketByIdFromCache(ticketId),
                isLoading = false,
            )
        }
    }
}
