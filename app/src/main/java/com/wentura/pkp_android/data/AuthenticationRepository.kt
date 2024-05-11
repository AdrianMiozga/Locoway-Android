package com.wentura.pkp_android.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AuthenticationRepository {
    private val firebaseAuth = Firebase.auth

    val isSignedIn: Flow<Boolean> = callbackFlow {
        val listener = AuthStateListener {
            trySend(it.currentUser != null)
        }

        firebaseAuth.addAuthStateListener(listener)

        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun signInWithCredential(authCredential: AuthCredential): Task<AuthResult> {
        return firebaseAuth.signInWithCredential(authCredential)
    }

    fun createUserWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
    }

    fun deleteAccount() {
        // TODO: Handle exceptions
        firebaseAuth.currentUser?.delete()
    }

    fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getEmail(): String {
        return firebaseAuth.currentUser?.email ?: ""
    }
}
