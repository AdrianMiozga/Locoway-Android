package com.wentura.pkp_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wentura.pkp_android.MainApplication
import com.wentura.pkp_android.data.AuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MyAccountUiState(
    val email: String,
    val signedOut: Boolean = false,
)

class MyAccountViewModel(
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(MyAccountUiState(authenticationRepository.getEmail()))
    val uiState: StateFlow<MyAccountUiState> = _uiState.asStateFlow()

    fun signOut() {
        authenticationRepository.signOut()
        _uiState.update { it.copy(signedOut = true) }
    }

    fun deleteAccount() {
        authenticationRepository.deleteAccount()
        _uiState.update { it.copy(signedOut = true) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val authenticationRepository =
                    (this[APPLICATION_KEY] as MainApplication).authenticationRepository

                MyAccountViewModel(
                    authenticationRepository = authenticationRepository,
                )
            }
        }
    }
}
