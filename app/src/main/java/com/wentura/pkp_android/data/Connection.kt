package com.wentura.pkp_android.data

import java.time.LocalDateTime

data class Connection(
    val trainBrand: TrainBrand,
    val price: String,
    val departureDateTime: LocalDateTime,
    val arrivalDateTime: LocalDateTime,
)
