package org.wentura.locoway.data.model

import androidx.annotation.StringRes

data class Authentication(
    val isSignedIn: Boolean,
    @StringRes val userMessage: Int? = null,
)
