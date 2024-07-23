package org.wentura.locoway.domain

import javax.inject.Inject
import org.wentura.locoway.data.model.Passenger

class TrimPassengerNameUseCase @Inject constructor() {
    operator fun invoke(passenger: Passenger): Passenger {
        val trimmedName = passenger.name.trim()
        return passenger.copy(name = trimmedName)
    }
}
