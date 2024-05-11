package com.wentura.pkp_android.viewmodels

import android.app.Activity
import android.util.Log
import androidx.annotation.StringRes
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.wentura.pkp_android.MainApplication
import com.wentura.pkp_android.R
import com.wentura.pkp_android.data.AuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val isSignedIn: Boolean = false,
    val isLoading: Boolean = false,
    @StringRes val userMessage: Int? = null,
)

class AuthenticationViewModel(
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {
    private val _loginState =
        MutableStateFlow(LoginState(isSignedIn = authenticationRepository.isUserSignedIn()))
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    init {
        viewModelScope.launch {
            authenticationRepository.isSignedIn.collect { isSignedIn ->
                _loginState.update {
                    it.copy(isSignedIn = isSignedIn)
                }
            }
        }
    }

    fun snackbarMessageShown() {
        _loginState.update {
            it.copy(userMessage = null)
        }
    }

    fun googleSignIn(activity: Activity) {
        // TODO: Add nonce
        val signInWithGoogle =
            GetSignInWithGoogleOption.Builder(activity.getString(R.string.firebase_web_client_id))
                .build()

        val request = GetCredentialRequest.Builder().addCredentialOption(signInWithGoogle).build()

        val credentialManager = CredentialManager.create(activity)

        viewModelScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = activity,
                    request = request,
                )

                handleSignIn(result, activity)
            } catch (exception: GetCredentialException) {
                _loginState.update {
                    it.copy(userMessage = R.string.unknown_error)
                }

                Log.e(TAG, "Failure", exception)
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse, activity: Activity) {
        _loginState.update {
            it.copy(isLoading = true)
        }

        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)

                        val firebaseCredential =
                            GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                        authenticationRepository.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(activity) { task ->
                                if (task.isSuccessful) {
                                    _loginState.update {
                                        it.copy(
                                            isSignedIn = true,
                                            isLoading = false,
                                            userMessage = R.string.logged_in
                                        )
                                    }
                                } else {
                                    val userMessage = when (task.exception) {
                                        is FirebaseNetworkException -> R.string.network_error
                                        else -> R.string.unknown_error
                                    }

                                    _loginState.update {
                                        it.copy(
                                            userMessage = userMessage, isLoading = false
                                        )
                                    }
                                }
                            }
                    } catch (exception: GoogleIdTokenParsingException) {
                        _loginState.update {
                            it.copy(userMessage = R.string.unknown_error, isLoading = false)
                        }

                        Log.e(TAG, "Received an invalid Google id token response", exception)
                    }
                } else {
                    _loginState.update {
                        it.copy(userMessage = R.string.unknown_error, isLoading = false)
                    }

                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                _loginState.update {
                    it.copy(userMessage = R.string.unknown_error, isLoading = false)
                }

                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    fun passwordSignIn(activity: Activity, email: String, password: String) {
        _loginState.update {
            it.copy(isLoading = true)
        }

        authenticationRepository.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    _loginState.update {
                        it.copy(
                            isSignedIn = true,
                            isLoading = false,
                            userMessage = R.string.created_account
                        )
                    }
                } else {
                    val userMessage = when (task.exception) {
                        is FirebaseAuthUserCollisionException -> R.string.user_with_that_account_exists
                        else -> R.string.unknown_error
                    }

                    _loginState.update {
                        it.copy(userMessage = userMessage, isLoading = false)
                    }
                }
            }
    }

    companion object {
        private val TAG = AuthenticationViewModel::class.java.simpleName

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val authenticationRepository =
                    (this[APPLICATION_KEY] as MainApplication).authenticationRepository

                AuthenticationViewModel(
                    authenticationRepository = authenticationRepository,
                )
            }
        }
    }
}
