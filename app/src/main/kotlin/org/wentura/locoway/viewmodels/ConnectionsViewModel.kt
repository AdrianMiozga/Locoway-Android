package org.wentura.locoway.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.wentura.locoway.data.model.Connection
import org.wentura.locoway.data.repository.ConnectionsRepository

data class ConnectionsUiState(
    val isLoading: Boolean = true,
    val departureStation: String = "",
    val arrivalStation: String = "",
    val connections: List<Connection> = emptyList(),
)

@HiltViewModel
class ConnectionsViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val connectionsRepository: ConnectionsRepository
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            ConnectionsUiState(
                departureStation = checkNotNull(savedStateHandle["departureStation"]),
                arrivalStation = checkNotNull(savedStateHandle["arrivalStation"])))

    val uiState = _uiState.asStateFlow()

    private var departureDate: String = checkNotNull(savedStateHandle["departureDate"])
    private var departureTime: String = checkNotNull(savedStateHandle["departureTime"])

    init {
        getConnections()
    }

    private fun getConnections() {
        viewModelScope.launch {
            val connections =
                connectionsRepository.getConnections(
                    departureDate,
                    departureTime,
                    _uiState.value.departureStation,
                    _uiState.value.arrivalStation)

            _uiState.update {
                it.copy(
                    connections = connections.values.toList(),
                    isLoading = false,
                )
            }
        }
    }
}
