package com.wentura.pkp_android.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wentura.pkp_android.MainApplication
import com.wentura.pkp_android.data.Authentication
import com.wentura.pkp_android.data.AuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isSignedIn: Boolean = false,
    @StringRes val userMessage: Int? = null,
)

class HomeViewModel(
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(Authentication(isSignedIn = authenticationRepository.isUserSignedIn()))
    val uiState: StateFlow<Authentication> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authenticationRepository.authentication.collect { authentication ->
                _uiState.update {
                    it.copy(
                        isSignedIn = authentication.isSignedIn,
                        userMessage = authentication.userMessage
                    )
                }
            }
        }
    }

    fun snackbarMessageShown() {
        authenticationRepository.clearMessage()
    }

    companion object {
        private val TAG = HomeViewModel::class.java.simpleName

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val authenticationRepository =
                    (this[APPLICATION_KEY] as MainApplication).authenticationRepository

                HomeViewModel(
                    authenticationRepository = authenticationRepository,
                )
            }
        }
    }
}
