package com.wentura.pkp_android

import android.app.Application
import com.wentura.pkp_android.data.AuthenticationRepository

class MainApplication : Application() {
    val authenticationRepository by lazy { AuthenticationRepository() }
}
