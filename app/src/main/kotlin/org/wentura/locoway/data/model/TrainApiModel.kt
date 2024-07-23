package org.wentura.locoway.data.model

import com.google.gson.annotations.SerializedName

data class TrainApiModel(
    val id: Long,
    @SerializedName("train_brand") val trainBrand: String,
    @SerializedName("train_nr") val trainNumber: Long,
)
