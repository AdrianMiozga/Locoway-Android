package com.wentura.pkp_android.data

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketRepository @Inject constructor() {
    private val firebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    fun addTicket(connection: Connection) {
        if (firebaseAuth.currentUser == null) {
            return
        }

        val ticket =
            Ticket(
                uid = firebaseAuth.currentUser!!.uid,
                carrier = "",
                trainNumber = connection.trainNumber,
                trainBrand = connection.trainBrand.displayName,
                trainClass = 1,
                seat = 1,
                departureStation = connection.departureStation,
                departureDate = connection.departureDateTime.toString(),
                arrivalStation = connection.arrivalStation,
                arrivalDate = connection.arrivalDateTime.toString(),
                dog = 0,
                bicycle = 0,
                additionalLuggage = 0,
            )

        db.collection("tickets").add(ticket)
    }

    suspend fun getTicketsForCurrentUser(): List<Ticket> {
        if (firebaseAuth.currentUser == null) {
            throw IllegalStateException("User is not logged in")
        }

        return db.collection("tickets")
            .whereEqualTo("uid", firebaseAuth.currentUser!!.uid)
            .get()
            .await()
            .toObjects(Ticket::class.java)
    }
}
