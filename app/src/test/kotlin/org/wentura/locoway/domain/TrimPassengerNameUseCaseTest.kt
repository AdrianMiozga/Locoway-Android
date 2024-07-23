package org.wentura.locoway.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import org.wentura.locoway.data.model.Passenger

class TrimPassengerNameUseCaseTest {

    @Test
    fun trimPassengerNameUseCaseTest() {
        val trimPassengerNameUseCase = TrimPassengerNameUseCase()

        assertEquals(
            "Passenger Name",
            trimPassengerNameUseCase.invoke(Passenger("Passenger Name")).name,
        )

        assertEquals(
            "Passenger Name",
            trimPassengerNameUseCase.invoke(Passenger(" Passenger Name ")).name,
        )
    }
}
