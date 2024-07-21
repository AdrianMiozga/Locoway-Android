package com.wentura.pkp_android.data.model

import java.time.LocalDateTime

data class RecentSearchStation(
    val type: String = "",
    val name: String = "",
    val addedAt: String = LocalDateTime.now().toString(),
)
