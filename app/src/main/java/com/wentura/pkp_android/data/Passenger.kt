package com.wentura.pkp_android.data

import java.time.LocalDateTime

data class Passenger(
    val name: String = "",
    val hasREGIOCard: Boolean = false,
    val discount: Int = 0,
    val addedAt: String = LocalDateTime.now().toString(),
)
