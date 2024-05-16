package com.wentura.pkp_android.data

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PassengerRepository @Inject constructor() {
    private val firebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    fun addPassenger(passenger: Passenger) {
        db.collection("users")
            .document(firebaseAuth.uid!!)
            .collection("passenger")
            .document()
            .set(passenger)
    }

    suspend fun getPassengers(): List<Passenger> {
        return db.collection("users")
            .document(firebaseAuth.uid!!)
            .collection("passenger")
            .orderBy("addedAt")
            .get()
            .await()
            .toObjects(Passenger::class.java)
    }
}
