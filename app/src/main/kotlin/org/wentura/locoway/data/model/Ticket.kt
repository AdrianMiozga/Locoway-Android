package org.wentura.locoway.data.model

import com.google.firebase.firestore.DocumentId

data class Ticket(
    @DocumentId val documentPath: String = "",
    val uid: String = "",
    val trainNumber: Long = 0,
    val trainBrand: String = "",
    val trainClass: Int = 0,
    val seat: Int = 0,
    val departureStation: String = "",
    val departureDate: String = "",
    val arrivalStation: String = "",
    val arrivalDate: String = "",
    val dog: Int = 0,
    val bicycle: Int = 0,
    val additionalLuggage: Int = 0,
    val passengers: List<Passenger> = emptyList(),
)
