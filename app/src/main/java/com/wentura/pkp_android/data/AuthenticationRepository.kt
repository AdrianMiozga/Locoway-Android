package com.wentura.pkp_android.data

import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import com.wentura.pkp_android.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationRepository @Inject constructor() {
    private val firebaseAuth = Firebase.auth

    private val _authentication = MutableStateFlow(Authentication(isUserSignedIn()))
    val authentication: Flow<Authentication> = _authentication.asStateFlow()

    fun signOut() {
        firebaseAuth.signOut()

        _authentication.update { it.copy(isSignedIn = false, userMessage = R.string.signed_out) }
    }

    suspend fun signInWithCredential(authCredential: AuthCredential) {
        try {
            firebaseAuth.signInWithCredential(authCredential).await()

            _authentication.update { it.copy(isSignedIn = true, userMessage = R.string.signed_in) }
        } catch (exception: Exception) {
            val userMessage =
                when (exception) {
                    is FirebaseNetworkException -> R.string.network_error
                    else -> R.string.unknown_error
                }

            _authentication.update { it.copy(userMessage = userMessage) }
        }
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            _authentication.update { it.copy(isSignedIn = true, userMessage = R.string.signed_in) }
        } catch (exception: Exception) {
            val userMessage =
                when (exception) {
                    is FirebaseAuthUserCollisionException -> R.string.user_with_that_account_exists
                    else -> R.string.unknown_error
                }

            _authentication.update { it.copy(userMessage = userMessage) }
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()

            _authentication.update { it.copy(isSignedIn = true, userMessage = R.string.signed_in) }
        } catch (exception: Exception) {
            val userMessage =
                when (exception) {
                    is FirebaseAuthInvalidCredentialsException -> R.string.invalid_credentials
                    else -> R.string.unknown_error
                }

            _authentication.update { it.copy(userMessage = userMessage) }
        }
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

    fun clearMessage() {
        _authentication.update { it.copy(userMessage = null) }
    }

    fun resetPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)

        _authentication.update { it.copy(userMessage = R.string.reset_password_email_sent) }
    }
}
