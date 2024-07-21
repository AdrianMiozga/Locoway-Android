package com.wentura.pkp_android.compose.authentication

import androidx.annotation.StringRes
import com.wentura.pkp_android.R

enum class AuthenticationPage(@StringRes val titleResId: Int) {
    LOGIN(R.string.login),
    REGISTER(R.string.register)
}
