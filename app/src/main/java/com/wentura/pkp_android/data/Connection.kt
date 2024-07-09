package com.wentura.pkp_android.data

import java.time.LocalDateTime

data class Connection(
    val trainId: Long,
    val trainNumber: Long,
    val trainBrand: TrainBrand,
    val ticketPrice: String,
    val departureStation: String,
    val arrivalStation: String,
    val departureDateTime: LocalDateTime,
    val arrivalDateTime: LocalDateTime,
    val dogPrice: String = "0",
    val bikePrice: String = "0",
    val luggagePrice: String = "0",
)
