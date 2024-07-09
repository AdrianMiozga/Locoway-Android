package com.wentura.pkp_android.domain

import com.wentura.pkp_android.data.Passenger
import javax.inject.Inject

class TrimPassengerNameUseCase @Inject constructor() {
    operator fun invoke(passenger: Passenger): Passenger {
        val trimmedName = passenger.name.trim()
        return passenger.copy(name = trimmedName)
    }
}
