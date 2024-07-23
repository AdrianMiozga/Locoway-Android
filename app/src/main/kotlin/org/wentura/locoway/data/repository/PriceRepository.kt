package org.wentura.locoway.data.repository

import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton
import org.wentura.locoway.data.model.Connection
import org.wentura.locoway.data.model.Discount
import org.wentura.locoway.data.model.Passenger

@Singleton
class PriceRepository @Inject constructor() {
    fun getPrice(
        connection: Connection,
        passengers: List<Passenger>,
        amountOfDogs: Int = 0,
        amountOfBikes: Int = 0,
        amountOfLuggage: Int = 0,
    ): BigDecimal {
        return passengers
            .map {
                BigDecimal(1)
                    .minus(BigDecimal(Discount.entries[it.discount].percentage))
                    .times(BigDecimal(connection.ticketPrice))
            }
            .sumOf { it }
            .plus(BigDecimal(connection.dogPrice).times(BigDecimal(amountOfDogs)))
            .plus(BigDecimal(connection.bikePrice).times(BigDecimal(amountOfBikes)))
            .plus(BigDecimal(connection.luggagePrice).times(BigDecimal(amountOfLuggage)))
    }
}
