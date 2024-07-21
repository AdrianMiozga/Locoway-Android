package com.wentura.pkp_android.data

import androidx.annotation.StringRes

data class Authentication(
    val isSignedIn: Boolean,
    @StringRes val userMessage: Int? = null,
)
