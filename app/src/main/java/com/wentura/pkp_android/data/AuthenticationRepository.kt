package com.wentura.pkp_android.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wentura.pkp_android.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthenticationRepository {
    private val firebaseAuth = Firebase.auth

    private val _authentication = MutableStateFlow(Authentication(isUserSignedIn()))
    val authentication: Flow<Authentication> = _authentication.asStateFlow()

    fun signOut() {
        firebaseAuth.signOut()

        _authentication.update {
            it.copy(isSignedIn = false, userMessage = R.string.signed_out)
        }
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

        _authentication.update {
            it.copy(isSignedIn = false, userMessage = R.string.account_deleted)
        }
    }

    fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getEmail(): String {
        return firebaseAuth.currentUser?.email ?: ""
    }

    fun signedIn() {
        _authentication.update {
            it.copy(isSignedIn = true, userMessage = R.string.signed_in)
        }
    }

    fun clearMessage() {
        _authentication.update {
            it.copy(userMessage = null)
        }
    }
}
