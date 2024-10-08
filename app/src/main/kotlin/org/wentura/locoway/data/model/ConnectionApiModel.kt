package org.wentura.locoway.data.model

import com.google.gson.annotations.SerializedName

data class ConnectionApiModel(
    @SerializedName("train_ids") val trainIds: List<Long>,
    @SerializedName("price_id") val priceId: Long,
    @SerializedName("start_date") val departureDateTime: String,
    @SerializedName("finish_date") val arrivalDateTime: String,
)
