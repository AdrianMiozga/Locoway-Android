package org.wentura.locoway.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await
import org.wentura.locoway.config.Collections
import org.wentura.locoway.config.Fields
import org.wentura.locoway.data.model.Passenger

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
