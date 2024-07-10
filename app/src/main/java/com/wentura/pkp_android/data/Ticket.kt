package com.wentura.pkp_android.data

import com.google.firebase.firestore.PropertyName

data class Ticket(
    val uid: String = "",
    val carrier: String = "",
    val trainNumber: Long = 0,
    val trainCategory: String = "",
    @get:PropertyName("class") val trainClass: Int = 0,
    val seat: Int = 0,
    val departureStation: String = "",
    val departureDate: String = "",
    val arrivalStation: String = "",
    val arrivalDate: String = "",
    val dog: Int = 0,
    val bicycle: Int = 0,
    val additionalLuggage: Int = 0,
)
