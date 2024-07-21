package com.wentura.pkp_android.viewmodels

import android.util.Log
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import com.wentura.pkp_android.R
import com.wentura.pkp_android.data.repository.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthenticationUiState(
    val isSignedIn: Boolean = false,
    val isLoading: Boolean = false,
    val isEmailWrong: Boolean = false,
    val isPasswordWrong: Boolean = false,
    val isConfirmationPasswordWrong: Boolean = false,
    @StringRes val userMessage: Int? = null,
)

@HiltViewModel
class AuthenticationViewModel
@Inject
constructor(
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {
    companion object {
        private val TAG = AuthenticationViewModel::class.java.simpleName
    }

    private val _uiState = MutableStateFlow(AuthenticationUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authenticationRepository.authentication.collect { authentication ->
                _uiState.update {
                    it.copy(
                        isSignedIn = authentication.isSignedIn,
                        isLoading = false,
                        userMessage = authentication.userMessage)
                }
            }
        }
    }

    fun onMessageShown() {
        authenticationRepository.clearMessage()
    }

    fun signInFailed(exception: Exception) {
        Log.e(TAG, "Failure", exception)

        _uiState.update { it.copy(userMessage = R.string.unknown_error) }
    }

    fun handleGoogleSignIn(result: GetCredentialResponse) {
        _uiState.update { it.copy(isLoading = true) }

        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)

                        val firebaseCredential =
                            GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                        viewModelScope.launch {
                            authenticationRepository.signInWithCredential(firebaseCredential)

                            _uiState.update { it.copy(isLoading = false) }
                        }
                    } catch (exception: GoogleIdTokenParsingException) {
                        _uiState.update {
                            it.copy(userMessage = R.string.unknown_error, isLoading = false)
                        }

                        Log.e(TAG, "Received an invalid Google id token response", exception)
                    }
                } else {
                    _uiState.update {
                        it.copy(userMessage = R.string.unknown_error, isLoading = false)
                    }

                    Log.e(TAG, "Unexpected type of credential")
                }
            }
            else -> {
                _uiState.update { it.copy(userMessage = R.string.unknown_error, isLoading = false) }

                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    fun passwordSignUp(
        email: String,
        password: String,
        passwordConfirmation: String,
    ) {
        val isEmailWrong = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordWrong = password.length < 8
        val isConfirmationPasswordWrong = password != passwordConfirmation

        if (isEmailWrong || isPasswordWrong || isConfirmationPasswordWrong) {
            _uiState.update {
                it.copy(
                    isEmailWrong = isEmailWrong,
                    isPasswordWrong = isPasswordWrong,
                    isConfirmationPasswordWrong = isConfirmationPasswordWrong,
                )
            }

            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            authenticationRepository.createUserWithEmailAndPassword(email, password)

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun passwordSignIn(email: String, password: String) {
        val isEmailWrong = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordWrong = password.isBlank()

        if (isEmailWrong || isPasswordWrong) {
            _uiState.update {
                it.copy(
                    isEmailWrong = isEmailWrong,
                    isPasswordWrong = isPasswordWrong,
                )
            }

            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            authenticationRepository.signInWithEmailAndPassword(email, password)

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun resetPassword(email: String): Boolean {
        // TODO: Add UX
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false
        }

        authenticationRepository.resetPassword(email)
        return true
    }
}
