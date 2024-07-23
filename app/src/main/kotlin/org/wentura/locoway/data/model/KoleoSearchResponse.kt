package org.wentura.locoway.data.model

data class KoleoSearchResponse(
    val connections: List<ConnectionApiModel>,
    val trains: List<TrainApiModel>
)
