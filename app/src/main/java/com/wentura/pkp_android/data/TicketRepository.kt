package com.wentura.pkp_android.data

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.wentura.pkp_android.R
import com.wentura.pkp_android.config.Collections
import com.wentura.pkp_android.config.Fields
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

@Singleton
class TicketRepository @Inject constructor() {
    private val firebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    private var tickets: Map<String, Ticket> = hashMapOf()

    private val _userMessage = MutableStateFlow<Int?>(null)
    val userMessage = _userMessage.asStateFlow()

    fun addTicket(
        connection: Connection,
        passengers: List<Passenger>,
        amountOfDogs: Int,
        amountOfBikes: Int,
        amountOfLuggage: Int,
        selectedClass: Int,
    ) {
        if (firebaseAuth.currentUser == null) {
            return
        }

        val ticket =
            Ticket(
                uid = firebaseAuth.currentUser!!.uid,
                trainNumber = connection.trainNumber,
                trainBrand = connection.trainBrand.displayName,
                trainClass = selectedClass,
                seat = Random.nextInt(1, 65),
                departureStation = connection.departureStation,
                departureDate = connection.departureDateTime.toString(),
                arrivalStation = connection.arrivalStation,
                arrivalDate = connection.arrivalDateTime.toString(),
                dog = amountOfDogs,
                bicycle = amountOfBikes,
                additionalLuggage = amountOfLuggage,
                passengers = passengers,
            )

        db.collection(Collections.TICKETS).add(ticket)

        _userMessage.value = R.string.ticket_bought
    }

    suspend fun getTicketsForCurrentUser(): List<Ticket> {
        if (firebaseAuth.currentUser == null) {
            throw IllegalStateException("User is not logged in")
        }

        val listOfTickets =
            db.collection(Collections.TICKETS)
                .whereEqualTo(Fields.UID, firebaseAuth.currentUser!!.uid)
                .orderBy(Fields.DEPARTURE_DATE, Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects(Ticket::class.java)

        tickets = listOfTickets.associateBy { it.documentPath }

        return listOfTickets
    }

    fun getTicketByIdFromCache(id: String): Ticket {
        return tickets.getOrElse(id) {
            throw IllegalArgumentException("Ticket with id $id not found")
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }
}
