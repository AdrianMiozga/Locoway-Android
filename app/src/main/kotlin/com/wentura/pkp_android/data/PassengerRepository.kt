package com.wentura.pkp_android.data

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.wentura.pkp_android.config.Collections
import com.wentura.pkp_android.config.Fields
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class PassengerRepository @Inject constructor() {
    private val firebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    suspend fun addPassenger(passenger: Passenger) {
        db.collection(Collections.USERS)
            .document(firebaseAuth.uid!!)
            .collection(Collections.PASSENGER)
            .document()
            .set(passenger)
            .await()
    }

    suspend fun getPassengers(): List<Passenger> {
        return db.collection(Collections.USERS)
            .document(firebaseAuth.uid!!)
            .collection(Collections.PASSENGER)
            .orderBy(Fields.ADDED_AT)
            .get()
            .await()
            .toObjects(Passenger::class.java)
    }

    suspend fun updatePassenger(documentPath: String, passenger: Passenger) {
        db.collection(Collections.USERS)
            .document(firebaseAuth.uid!!)
            .collection(Collections.PASSENGER)
            .document(documentPath)
            .set(passenger, SetOptions.merge())
            .await()
    }

    suspend fun deletePassenger(documentPath: String) {
        db.collection(Collections.USERS)
            .document(firebaseAuth.uid!!)
            .collection(Collections.PASSENGER)
            .document(documentPath)
            .delete()
            .await()
    }
}
