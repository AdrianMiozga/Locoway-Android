package com.wentura.pkp_android

import com.wentura.pkp_android.data.model.Passenger
import com.wentura.pkp_android.domain.TrimPassengerNameUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

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
