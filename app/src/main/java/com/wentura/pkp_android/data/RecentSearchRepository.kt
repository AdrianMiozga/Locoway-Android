package com.wentura.pkp_android.data

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentSearchRepository @Inject constructor() {
    private val firebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    suspend fun addRecentStation(recentSearchStation: RecentSearchStation) {
        if (firebaseAuth.uid == null) {
            return
        }

        val document = db.collection("users")
            .document(firebaseAuth.uid!!)
            .collection("recentSearch")
            .whereEqualTo("name", recentSearchStation.name)
            .whereEqualTo("type", recentSearchStation.type)
            .limit(1)
            .get()
            .await().documents

        if (document.isNotEmpty()) {
            val id = document.first().id

            db.collection("users")
                .document(firebaseAuth.uid!!)
                .collection("recentSearch")
                .document(id)
                .set(recentSearchStation)
        } else {
            db.collection("users")
                .document(firebaseAuth.uid!!)
                .collection("recentSearch")
                .document()
                .set(recentSearchStation)
        }
    }

    suspend fun getRecentDepartureStations(): List<RecentSearchStation> {
        if (firebaseAuth.uid == null) {
            return emptyList()
        }

        return db.collection("users")
            .document(firebaseAuth.uid!!)
            .collection("recentSearch")
            .whereEqualTo("type", "departure")
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(RecentSearchStation::class.java)
    }

    suspend fun getRecentArrivalStations(): List<RecentSearchStation> {
        if (firebaseAuth.uid == null) {
            return emptyList()
        }

        return db.collection("users")
            .document(firebaseAuth.uid!!)
            .collection("recentSearch")
            .whereEqualTo("type", "arrival")
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(RecentSearchStation::class.java)
    }
}
