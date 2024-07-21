package com.wentura.pkp_android

import com.wentura.pkp_android.util.travelTime
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class TravelTimeTest {

    @Test
    fun `Departure and arrival the same`() {
        assertEquals(
            0,
            travelTime(
                LocalDateTime.parse("2024-06-24T12:00:00"),
                LocalDateTime.parse("2024-06-24T12:00:00"),
            ))
    }

    @Test
    fun `One hour apart`() {
        assertEquals(
            60,
            travelTime(
                LocalDateTime.parse("2024-06-24T12:00:00"),
                LocalDateTime.parse("2024-06-24T13:00:00"),
            ))
    }

    @Test
    fun `Between days`() {
        assertEquals(
            120,
            travelTime(
                LocalDateTime.parse("2024-06-24T23:00:00"),
                LocalDateTime.parse("2024-06-25T01:00:00"),
            ))
    }

    @Test
    fun `Rounding up`() {
        assertEquals(
            6,
            travelTime(
                LocalDateTime.parse("2024-06-24T10:00:00"),
                LocalDateTime.parse("2024-06-24T10:05:30"),
            ))
    }
}
