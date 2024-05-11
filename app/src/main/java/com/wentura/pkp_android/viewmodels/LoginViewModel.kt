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
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wentura.pkp_android.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val loggedIn: Boolean = false,
    val loading: Boolean = false,
    @StringRes val userMessage: Int? = null,
)

class LoginViewModel : ViewModel() {
    companion object {
        private val TAG = LoginViewModel::class.java.simpleName
    }

    private lateinit var auth: FirebaseAuth

    private val _loginState = MutableStateFlow(LoginState())

    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

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
            it.copy(loading = true)
        }

        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)

                        val firebaseCredential =
                            GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                        auth = Firebase.auth

                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(activity) { task ->
                                if (task.isSuccessful) {
                                    _loginState.update {
                                        it.copy(
                                            loggedIn = true,
                                            loading = false,
                                            userMessage = R.string.logged_in
                                        )
                                    }
                                } else if (task.exception is FirebaseNetworkException) {
                                    _loginState.update {
                                        it.copy(
                                            userMessage = R.string.network_error, loading = false
                                        )
                                    }
                                } else {
                                    _loginState.update {
                                        it.copy(
                                            userMessage = R.string.unknown_error, loading = false
                                        )
                                    }
                                }
                            }
                    } catch (exception: GoogleIdTokenParsingException) {
                        _loginState.update {
                            it.copy(userMessage = R.string.unknown_error, loading = false)
                        }

                        Log.e(TAG, "Received an invalid Google id token response", exception)
                    }
                } else {
                    _loginState.update {
                        it.copy(userMessage = R.string.unknown_error, loading = false)
                    }

                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                _loginState.update {
                    it.copy(userMessage = R.string.unknown_error, loading = false)
                }

                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }
}
