package org.wentura.locoway.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

fun Context.findActivity(): Activity {
    var context = this

    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }

        context = context.baseContext
    }

    throw IllegalStateException("No activity")
}

fun travelTime(departureDateTime: LocalDateTime, arrivalDateTime: LocalDateTime): Int {
    // TODO: What about hour+
    val minutes =
        ceil(
                ChronoUnit.SECONDS.between(
                    departureDateTime,
                    arrivalDateTime,
                ) / 60.0)
            .toInt()

    return minutes
}
