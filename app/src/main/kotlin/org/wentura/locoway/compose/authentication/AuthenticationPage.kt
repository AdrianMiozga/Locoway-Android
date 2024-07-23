package org.wentura.locoway.compose.authentication

import androidx.annotation.StringRes
import org.wentura.locoway.R

enum class AuthenticationPage(@StringRes val titleResId: Int) {
    LOGIN(R.string.login),
    REGISTER(R.string.register)
}
