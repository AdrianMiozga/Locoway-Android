package com.wentura.pkp_android.data.model

import com.google.gson.annotations.SerializedName

data class Price(
    @SerializedName("value") val ticketPrice: String,
    @SerializedName("dog_price") val dogPrice: String,
    @SerializedName("bike_price") val bikePrice: String,
    @SerializedName("luggage_price") val luggagePrice: String,
)
