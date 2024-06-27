package com.wentura.pkp_android.data

import com.wentura.pkp_android.api.KoleoService
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionsRepository @Inject constructor(private val koleoService: KoleoService) {
    suspend fun getConnections(
        departureDate: String,
        departureTime: String,
        departureStation: String,
        arrivalStation: String
    ): List<Connection> {
        val formattedDate =
            LocalDate.parse(departureDate).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

        val formattedTime =
            LocalTime.parse(departureTime).format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val koleoSearchResponse =
            koleoService.getConnections(
                "$formattedDate+$formattedTime", departureStation, arrivalStation)

        return koleoSearchResponse.connections.map {
            val trainId = it.trainIds.first()
            val trainMap = koleoSearchResponse.trains.associateBy { train -> train.id }
            val koleoFormatter = DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd")
            val price = koleoService.getPrices(it.priceId).price.value

            Connection(
                TrainBrand.valueOf(
                    trainMap[trainId]?.trainBrand
                        ?: throw IllegalStateException("Unknown train brand")),
                price,
                LocalDateTime.parse(it.departureDateTime, koleoFormatter),
                LocalDateTime.parse(it.arrivalDateTime, koleoFormatter))
        }
    }
}
