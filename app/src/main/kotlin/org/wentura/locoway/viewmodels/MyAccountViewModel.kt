package org.wentura.locoway.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.wentura.locoway.data.repository.AuthenticationRepository

data class MyAccountUiState(val email: String, val isSignedIn: Boolean = true)

@HiltViewModel
class MyAccountViewModel
@Inject
constructor(private val authenticationRepository: AuthenticationRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MyAccountUiState(authenticationRepository.getEmail()))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authenticationRepository.authentication.collect { authentication ->
                _uiState.update { it.copy(isSignedIn = authentication.isSignedIn) }
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
