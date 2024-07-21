package com.wentura.pkp_android.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.wentura.pkp_android.config.Collections
import com.wentura.pkp_android.config.Fields
import com.wentura.pkp_android.config.Values
import com.wentura.pkp_android.data.model.RecentSearchStation
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class RecentSearchRepository @Inject constructor() {
    private val firebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    suspend fun addRecentStation(recentSearchStation: RecentSearchStation) {
        if (firebaseAuth.uid == null) {
            return
        }

        val document =
            db.collection(Collections.USERS)
                .document(firebaseAuth.uid!!)
                .collection(Collections.RECENT_SEARCH)
                .whereEqualTo(Fields.NAME, recentSearchStation.name)
                .whereEqualTo(Fields.TYPE, recentSearchStation.type)
                .limit(1)
                .get()
                .await()
                .documents

        if (document.isNotEmpty()) {
            val id = document.first().id

            db.collection(Collections.USERS)
                .document(firebaseAuth.uid!!)
                .collection(Collections.RECENT_SEARCH)
                .document(id)
                .set(recentSearchStation)
        } else {
            db.collection(Collections.USERS)
                .document(firebaseAuth.uid!!)
                .collection(Collections.RECENT_SEARCH)
                .document()
                .set(recentSearchStation)
        }
    }

    suspend fun getRecentDepartureStations(): List<RecentSearchStation> {
        if (firebaseAuth.uid == null) {
            return emptyList()
        }

        return db.collection(Collections.USERS)
            .document(firebaseAuth.uid!!)
            .collection(Collections.RECENT_SEARCH)
            .whereEqualTo(Fields.TYPE, Values.DEPARTURE)
            .orderBy(Fields.ADDED_AT, Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(RecentSearchStation::class.java)
    }

    suspend fun getRecentArrivalStations(): List<RecentSearchStation> {
        if (firebaseAuth.uid == null) {
            return emptyList()
        }

        return db.collection(Collections.USERS)
            .document(firebaseAuth.uid!!)
            .collection(Collections.RECENT_SEARCH)
            .whereEqualTo(Fields.TYPE, Values.ARRIVAL)
            .orderBy(Fields.ADDED_AT, Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(RecentSearchStation::class.java)
    }
}
