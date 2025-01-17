package org.wentura.locoway.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import org.wentura.locoway.data.model.Connection
import org.wentura.locoway.data.model.ConnectionPagingModel
import org.wentura.locoway.data.repository.AuthenticationRepository
import org.wentura.locoway.data.repository.ConnectionsPagingSource
import org.wentura.locoway.data.repository.ConnectionsRepository

data class ConnectionsUiState(
    val isLoading: Boolean = true,
    val isSignedIn: Boolean = false,
    val departureStation: String = "",
    val arrivalStation: String = "",
    val connections: List<Connection> = emptyList(),
)

@HiltViewModel
class ConnectionsViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val connectionsRepository: ConnectionsRepository,
    authenticationRepository: AuthenticationRepository,
) : ViewModel() {
    private var departureDate: String = checkNotNull(savedStateHandle["departureDate"])
    private var departureTime: String = checkNotNull(savedStateHandle["departureTime"])

    private val departureStation: String = checkNotNull(savedStateHandle["departureStation"])
    private val arrivalStation: String = checkNotNull(savedStateHandle["arrivalStation"])

    private val _uiState =
        MutableStateFlow(
            ConnectionsUiState(
                isSignedIn = authenticationRepository.isSignedIn(),
                departureStation = departureStation,
                arrivalStation = arrivalStation,
            )
        )

    val uiState = _uiState.asStateFlow()

    private val _pagingDataFlow = MutableStateFlow(createPagerFlow())
    val pagingDataFlow = _pagingDataFlow.asStateFlow()

    private fun createPagerFlow(): Flow<PagingData<ConnectionPagingModel>> {
        return Pager(PagingConfig(pageSize = 8, prefetchDistance = 1, enablePlaceholders = false)) {
                ConnectionsPagingSource(
                    connectionsRepository = connectionsRepository,
                    departureDate = departureDate,
                    departureTime = departureTime,
                    departureStation = departureStation,
                    arrivalStation = arrivalStation,
                )
            }
            .flow
            .map { pagingData ->
                pagingData.map { connection -> ConnectionPagingModel.UiConnection(connection) }
            }
            .map {
                it.insertSeparators { before, after ->
                    if (after == null) {
                        return@insertSeparators null
                    }

                    val afterDate = after.connection.departureDateTime.toLocalDate()

                    if (before == null) {
                        return@insertSeparators ConnectionPagingModel.DateSeparator(afterDate)
                    }

                    val beforeDate = before.connection.departureDateTime.toLocalDate()

                    if (beforeDate != afterDate) {
                        ConnectionPagingModel.DateSeparator(afterDate)
                    } else {
                        ConnectionPagingModel.Divider
                    }
                }
            }
            .cachedIn(viewModelScope)
    }
}
