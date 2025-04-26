package org.wentura.locoway.data.repository

import android.util.Log
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import org.wentura.locoway.api.KoleoService
import org.wentura.locoway.data.model.Connection
import org.wentura.locoway.data.model.TrainBrand

@Singleton
class ConnectionsRepository @Inject constructor(private val koleoService: KoleoService) {
    companion object {
        private val TAG = ConnectionsRepository::class.java.simpleName
    }

    private var connections: MutableMap<Long, Connection> = mutableMapOf()

    suspend fun getConnections(
        departureDate: String,
        departureTime: String,
        departureStation: String,
        arrivalStation: String,
    ): Map<Long, Connection> {
        val formattedDate =
            LocalDate.parse(departureDate).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

        val formattedTime =
            LocalTime.parse(departureTime).format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val koleoSearchResponse =
            koleoService.getConnections(
                "$formattedDate+$formattedTime",
                departureStation,
                arrivalStation,
            )

        val newConnections = koleoSearchResponse.connections
            .map {
                val trainId = it.trainIds.first()
                val trainMap = koleoSearchResponse.trains.associateBy { train -> train.id }
                val koleoFormatter = DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd")
                val price = koleoService.getPrices(it.priceId).price

                Connection(
                    trainId,
                    trainMap[trainId]?.trainNumber
                        ?: throw IllegalStateException("Unknown train"),
                    TrainBrand.valueOf(
                        trainMap[trainId]?.trainBrand
                            ?: throw IllegalStateException("Unknown train")
                    ),
                    price.ticketPrice,
                    departureStation,
                    arrivalStation,
                    LocalDateTime.parse(it.departureDateTime, koleoFormatter),
                    LocalDateTime.parse(it.arrivalDateTime, koleoFormatter),
                    price.dogPrice ?: "0",
                    price.bikePrice ?: "0",
                    price.luggagePrice ?: "0",
                )
            }
            .associateBy { it.trainId }

        connections.putAll(newConnections)

        Log.d(TAG, "Added new connections")

        return newConnections
    }

    fun getConnectionByIdFromCache(id: Long): Connection {
        return connections[id] ?: throw IllegalArgumentException("Connection with id $id not found")
    }

    fun clear() {
        connections = mutableMapOf()
        Log.d(TAG, "Cleared connections hashMap")
    }
}
