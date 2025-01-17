package org.wentura.locoway.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import java.time.LocalDateTime
import org.wentura.locoway.data.model.Connection

class ConnectionsPagingSource(
    private val connectionsRepository: ConnectionsRepository,
    private val departureDate: String,
    private val departureTime: String,
    private val departureStation: String,
    private val arrivalStation: String,
) : PagingSource<LocalDateTime, Connection>() {

    override suspend fun load(
        params: LoadParams<LocalDateTime>
    ): LoadResult<LocalDateTime, Connection> {
        val date = params.key?.toLocalDate()?.toString() ?: departureDate

        val time = params.key?.toLocalTime()?.toString() ?: departureTime

        val data =
            connectionsRepository.getConnections(
                departureDate = date,
                departureTime = time,
                departureStation = departureStation,
                arrivalStation = arrivalStation,
            )

        val list = data.values.toList()

        val nextKey = list.last().departureDateTime.plusMinutes(30).plusSeconds(1)

        return LoadResult.Page(data = list, prevKey = null, nextKey = nextKey)
    }

    override fun getRefreshKey(state: PagingState<LocalDateTime, Connection>): LocalDateTime? {
        return null
    }
}
