package com.wentura.pkp_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wentura.pkp_android.MainApplication
import com.wentura.pkp_android.data.AuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyAccountUiState(
    val email: String,
    val isSignedIn: Boolean = true,
)

class MyAccountViewModel(
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyAccountUiState(authenticationRepository.getEmail()))
    val uiState: StateFlow<MyAccountUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authenticationRepository.authentication.collect { authentication ->
                _uiState.update {
                    it.copy(isSignedIn = authentication.isSignedIn)
                }
            }
        }
    }

    fun signOut() {
        authenticationRepository.signOut()
    }

    fun deleteAccount() {
        authenticationRepository.deleteAccount()
    }

    companion object {
        private val TAG = MyAccountViewModel::class.java.simpleName

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
