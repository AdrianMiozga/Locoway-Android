package org.wentura.locoway.data.repository

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
    private var connections: Map<Long, Connection> = emptyMap()

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

        connections =
            koleoSearchResponse.connections
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
                        price.dogPrice,
                        price.bikePrice,
                        price.luggagePrice,
                    )
                }
                .associateBy { it.trainId }

        return connections
    }

    fun getConnectionByIdFromCache(id: Long): Connection {
        return connections[id] ?: throw IllegalArgumentException("Connection with id $id not found")
    }
}
