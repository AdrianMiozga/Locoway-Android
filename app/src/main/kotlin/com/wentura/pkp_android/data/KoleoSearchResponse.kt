package com.wentura.pkp_android.data

data class KoleoSearchResponse(
    val connections: List<ConnectionApiModel>,
    val trains: List<TrainApiModel>
)
