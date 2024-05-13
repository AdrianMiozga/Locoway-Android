package com.wentura.pkp_android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wentura.pkp_android.data.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyAccountUiState(
    val email: String,
    val isSignedIn: Boolean = true,
)

@HiltViewModel
class MyAccountViewModel @Inject constructor(
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
}
